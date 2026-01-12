package com.eduhub.dto;

import java.util.List;

import com.eduhub.model.Question;

/**
 * DTO representing a group of semantically similar questions.
 * Contains the main question and a list of similar questions with their similarity scores.
 */
public class QuestionGroupDto {
    
    private Question mainQuestion;
    private List<SimilarQuestionDto> similarQuestions;
    private int totalSimilar;

    public QuestionGroupDto() {
    }

    public QuestionGroupDto(Question mainQuestion, List<SimilarQuestionDto> similarQuestions) {
        this.mainQuestion = mainQuestion;
        this.similarQuestions = similarQuestions;
        this.totalSimilar = similarQuestions != null ? similarQuestions.size() : 0;
    }

    // Getters and Setters
    public Question getMainQuestion() {
        return mainQuestion;
    }

    public void setMainQuestion(Question mainQuestion) {
        this.mainQuestion = mainQuestion;
    }

    public List<SimilarQuestionDto> getSimilarQuestions() {
        return similarQuestions;
    }

    public void setSimilarQuestions(List<SimilarQuestionDto> similarQuestions) {
        this.similarQuestions = similarQuestions;
        this.totalSimilar = similarQuestions != null ? similarQuestions.size() : 0;
    }

    public int getTotalSimilar() {
        return totalSimilar;
    }

    public void setTotalSimilar(int totalSimilar) {
        this.totalSimilar = totalSimilar;
    }

    /**
     * Inner DTO for similar questions with their similarity scores.
     */
    public static class SimilarQuestionDto {
        private Question question;
        private double similarityScore;

        public SimilarQuestionDto() {
        }

        public SimilarQuestionDto(Question question, double similarityScore) {
            this.question = question;
            this.similarityScore = similarityScore;
        }

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public double getSimilarityScore() {
            return similarityScore;
        }

        public void setSimilarityScore(double similarityScore) {
            this.similarityScore = similarityScore;
        }
    }
}
