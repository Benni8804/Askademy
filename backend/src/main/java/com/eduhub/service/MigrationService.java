package com.eduhub.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eduhub.model.Question;
import com.eduhub.repository.QuestionRepository;

/**
 * Service responsible for data migrations and schema upgrades.
 * Currently handles backfilling embeddings for legacy questions.
 */
@Service
public class MigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Value("${app.ai.enabled:false}")
    private boolean aiEnabled;
    
    /**
     * Runs after application startup to perform data migrations.
     * Backfills embeddings for questions that don't have them.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void backfillEmbeddings() {
        if (!aiEnabled) {
            logger.info("AI mode disabled - skipping embedding backfill");
            return;
        }
        
        try {
            logger.info("Starting embedding backfill migration...");
            
            // Find all questions with NULL embeddings
            List<Question> allQuestions = questionRepository.findAll();
            List<Question> questionsNeedingEmbeddings = allQuestions.stream()
                    .filter(q -> q.getEmbedding() == null || q.getEmbedding().isEmpty())
                    .toList();
            
            if (questionsNeedingEmbeddings.isEmpty()) {
                logger.info("No legacy questions found - all embeddings are up to date ✓");
                return;
            }
            
            logger.info("Backfilling embeddings for {} legacy questions...", questionsNeedingEmbeddings.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (Question question : questionsNeedingEmbeddings) {
                try {
                    // Generate combined text from question title and content
                    String combinedText = question.getTitle();
                    if (question.getContent() != null && !question.getContent().isEmpty()) {
                        combinedText += " " + question.getContent();
                    }
                    
                    // Generate and set embedding
                    List<Double> embedding = embeddingService.generateEmbedding(combinedText);
                    question.setEmbedding(embedding);
                    
                    // Save updated question
                    questionRepository.save(question);
                    successCount++;
                    
                    logger.debug("Generated embedding for question ID {}: '{}'", 
                            question.getId(), question.getTitle());
                    
                } catch (Exception e) {
                    failCount++;
                    logger.error("Failed to generate embedding for question ID {}: {}", 
                            question.getId(), e.getMessage());
                }
            }
            
            logger.info("Embedding backfill completed: {} successful, {} failed ✓", 
                    successCount, failCount);
            
        } catch (Exception e) {
            logger.error("Embedding backfill migration failed: {}", e.getMessage(), e);
            // Don't throw - allow application to start even if migration fails
        }
    }
}
