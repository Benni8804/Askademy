package com.eduhub.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduhub.dto.QuestionGroupDto;
import com.eduhub.dto.QuestionGroupDto.SimilarQuestionDto;
import com.eduhub.dto.QuestionRequest;
import com.eduhub.model.Course;
import com.eduhub.model.Question;
import com.eduhub.model.User;
import com.eduhub.repository.CourseRepository;
import com.eduhub.repository.QuestionRepository;
import com.eduhub.repository.UserRepository;
import com.eduhub.util.VectorUtils;

/**
 * Service layer for Question management.
 * Handles business logic including embedding generation for semantic search.
 */
@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmbeddingService embeddingService;

    @Value("${app.ai.enabled:false}")
    private boolean aiEnabled;

    public QuestionService(QuestionRepository questionRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            EmbeddingService embeddingService) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.embeddingService = embeddingService;
    }

    /**
     * Creates a new question with semantic embedding.
     * Combines title + content to generate a vector for similarity search.
     */
    @Transactional
    public Question createQuestion(QuestionRequest request, User currentUser) {
        logger.info("Creating question: {} by user: {}", request.getTitle(), currentUser.getEmail());

        // Validate course exists
        Course course = courseRepository.findById(Objects.requireNonNull(request.getCourseId()))
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        // Validate user exists
        User author = userRepository.findById(Objects.requireNonNull(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + currentUser.getId()));

        // Create question entity
        Question question = new Question(
                request.getTitle(),
                request.getContent(),
                author,
                course,
                request.isAnonymous());

        // Generate semantic embedding from title + content
        // Only enabled when app.ai.enabled=true (PostgreSQL with pgvector)
        if (aiEnabled) {
            try {
                String textToEmbed = request.getTitle() + " " + request.getContent();
                logger.debug("AI mode enabled - Generating embedding for combined text ({} chars)",
                        textToEmbed.length());

                List<Double> embedding = embeddingService.generateEmbedding(textToEmbed);
                question.setEmbedding(embedding);

                logger.info("Embedding generated successfully with {} dimensions", embedding.size());
            } catch (Exception e) {
                logger.error("Failed to generate embedding in AI mode: {}", e.getMessage(), e);
            }
        } else {
            logger.debug("AI mode disabled - Skipping embedding generation");
        }

        // Save to database
        Question savedQuestion = questionRepository.save(question);
        logger.info("Question saved with ID: {} and embedding persisted", savedQuestion.getId());

        return savedQuestion;
    }

    /**
     * Creates a question directly (for demo data) with embedding generation.
     * Bypasses DTO validation for demo purposes.
     */
    @Transactional
    public Question createQuestionDirect(String title, String content, User author, Course course, boolean anonymous) {
        logger.info("Creating demo question: {}", title);

        // Create question entity
        Question question = new Question(title, content, author, course, anonymous);

        // Generate semantic embedding if AI is enabled
        if (aiEnabled) {
            try {
                String textToEmbed = title + " " + content;
                logger.debug("AI mode enabled - Generating embedding for combined text ({} chars)",
                        textToEmbed.length());

                List<Double> embedding = embeddingService.generateEmbedding(textToEmbed);
                question.setEmbedding(embedding);

                logger.info("Embedding generated successfully with {} dimensions", embedding.size());
            } catch (Exception e) {
                logger.error("Failed to generate embedding: {}", e.getMessage(), e);
            }
        }

        return questionRepository.save(question);
    }

    /**
     * Retrieves questions by course ID with optional filtering.
     */
    public List<Question> getQuestionsByCourse(Integer courseId, String filter) {
        logger.debug("Fetching questions for course ID: {} with filter: {}", courseId, filter);

        return switch (filter == null ? "" : filter) {
            case "unanswered" -> questionRepository.findUnansweredQuestionsByCourseId(courseId);
            case "answered" -> questionRepository.findAnsweredQuestionsByCourseId(courseId);
            default -> questionRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        };
    }

    /**
     * Deletes a question if user has permission (author, professor, or admin).
     */
    @Transactional
    public void deleteQuestion(Long questionId, User currentUser) {
        logger.info("Deleting question ID: {} by user: {}", questionId, currentUser.getEmail());

        Question question = questionRepository.findById(Objects.requireNonNull(questionId))
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        // Authorization check
        boolean isAuthor = question.getAuthor() != null &&
                question.getAuthor().getId().equals(currentUser.getId());
        boolean isProfessor = "PROFESSOR".equals(currentUser.getRole().name());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole().name());

        if (!isAuthor && !isProfessor && !isAdmin) {
            throw new RuntimeException("Unauthorized: Only question author, professor, or admin can delete");
        }

        questionRepository.delete(question);
        logger.info("Question ID: {} deleted successfully", questionId);
    }

    /**
     * Groups similar questions using cosine similarity on embeddings.
     * Uses greedy clustering: each ungrouped question becomes a group leader,
     * and all questions above the similarity threshold join its group.
     */
    public List<QuestionGroupDto> getGroupedQuestions(Integer courseId, double similarityThreshold) {
        logger.info("Grouping questions for course ID: {} with threshold: {}", courseId, similarityThreshold);

        // Check if AI mode is enabled
        if (!aiEnabled) {
            logger.warn("AI mode disabled - cannot group questions without embeddings. Returning empty list.");
            return List.of();
        }

        // Get all questions for the course
        List<Question> allQuestions = questionRepository.findByCourseIdOrderByCreatedAtDesc(courseId);

        // Filter questions that have embeddings
        List<Question> questionsWithEmbeddings = allQuestions.stream()
                .filter(q -> q.getEmbedding() != null && !q.getEmbedding().isEmpty())
                .toList();

        logger.info("Found {} questions with embeddings out of {} total",
                questionsWithEmbeddings.size(), allQuestions.size());

        if (questionsWithEmbeddings.isEmpty()) {
            logger.warn("No questions with embeddings found. Cannot perform grouping.");
            return List.of();
        }

        List<QuestionGroupDto> groups = new ArrayList<>();
        Set<Long> processedQuestionIds = new HashSet<>();

        // Greedy clustering: iterate through questions in chronological order
        for (Question mainQuestion : questionsWithEmbeddings) {
            // Skip if already grouped
            if (processedQuestionIds.contains(mainQuestion.getId())) {
                continue;
            }

            List<SimilarQuestionDto> similarQuestions = new ArrayList<>();

            // Find all similar questions
            for (Question otherQuestion : questionsWithEmbeddings) {
                // Skip self and already processed questions
                if (otherQuestion.getId().equals(mainQuestion.getId()) ||
                        processedQuestionIds.contains(otherQuestion.getId())) {
                    continue;
                }

                // Calculate cosine similarity
                try {
                    double similarity = VectorUtils.cosineSimilarity(
                            mainQuestion.getEmbedding(),
                            otherQuestion.getEmbedding());

                    logger.debug("Similarity between Q{} and Q{}: {}",
                            mainQuestion.getId(), otherQuestion.getId(), similarity);

                    // If above threshold, add to similar group
                    if (similarity >= similarityThreshold) {
                        similarQuestions.add(new SimilarQuestionDto(otherQuestion, similarity));
                        processedQuestionIds.add(otherQuestion.getId());
                    }
                } catch (Exception e) {
                    logger.error("Error calculating similarity between Q{} and Q{}: {}",
                            mainQuestion.getId(), otherQuestion.getId(), e.getMessage());
                }
            }

            // Only create a group if there are similar questions
            // Otherwise, treat as standalone question
            if (!similarQuestions.isEmpty()) {
                // Sort similar questions by similarity score (descending)
                similarQuestions.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));

                QuestionGroupDto group = new QuestionGroupDto(mainQuestion, similarQuestions);
                groups.add(group);

                logger.info("Created group with main Q{} and {} similar questions",
                        mainQuestion.getId(), similarQuestions.size());
            } else {
                // Standalone question (no similar ones found)
                QuestionGroupDto standaloneGroup = new QuestionGroupDto(mainQuestion, List.of());
                groups.add(standaloneGroup);

                logger.debug("Q{} has no similar questions (standalone)", mainQuestion.getId());
            }

            // Mark main question as processed
            processedQuestionIds.add(mainQuestion.getId());
        }

        logger.info("Grouping complete: {} groups created from {} questions",
                groups.size(), questionsWithEmbeddings.size());

        return groups;
    }

    /**
     * Overloaded method with default similarity threshold of 0.85 (85% similarity).
     * This is a good default for semantic similarity with embedding models.
     */
    public List<QuestionGroupDto> getGroupedQuestions(Integer courseId) {
        return getGroupedQuestions(courseId, 0.75);
    }
}
