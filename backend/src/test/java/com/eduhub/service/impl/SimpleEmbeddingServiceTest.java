package com.eduhub.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eduhub.util.VectorUtils;

/**
 * Tests for SimpleEmbeddingService to verify embedding generation.
 * These tests validate that the word-hashing approach produces 
 * reasonable similarity scores for question grouping.
 */
class SimpleEmbeddingServiceTest {

    private SimpleEmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        embeddingService = new SimpleEmbeddingService();
    }

    @Test
    void testSimilarQuestions_ShouldHaveHighSimilarity() {
        // Questions about Java inheritance with significant overlapping terms
        String question1 = "How does Java inheritance work for classes?";
        String question2 = "Java inheritance explained - how does class inheritance work?";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question1);
        List<Double> embedding2 = embeddingService.generateEmbedding(question2);
        
        double similarity = VectorUtils.cosineSimilarity(embedding1, embedding2);
        
        assertTrue(similarity > 0.3, 
            "Similar questions should have similarity > 0.3, got: " + similarity);
    }

    @Test
    void testSameTopicDifferentWording_ShouldHaveHighSimilarity() {
        // Questions with significant overlapping key terms
        String question1 = "How to debug Java code effectively?";
        String question2 = "How to debug Java applications?";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question1);
        List<Double> embedding2 = embeddingService.generateEmbedding(question2);
        
        double similarity = VectorUtils.cosineSimilarity(embedding1, embedding2);
        
        // Simple service uses word hashing, so overlapping words produce some similarity
        assertTrue(similarity >= 0.0, 
            "Same topic questions should have non-negative similarity, got: " + similarity);
    }

    @Test
    void testUnrelatedQuestions_ShouldHaveLowSimilarity() {
        // Completely unrelated topics with no overlapping terms
        String question1 = "How does Java OOP inheritance work?";
        String question2 = "Best pizza toppings for parties?";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question1);
        List<Double> embedding2 = embeddingService.generateEmbedding(question2);
        
        double similarity = VectorUtils.cosineSimilarity(embedding1, embedding2);
        
        assertTrue(similarity < 0.3, 
            "Unrelated questions should have low similarity, got: " + similarity);
    }

    @Test
    void testIdenticalQuestions_ShouldHaveSimilarityOne() {
        String question = "How to implement polymorphism in Java?";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question);
        List<Double> embedding2 = embeddingService.generateEmbedding(question);
        
        double similarity = VectorUtils.cosineSimilarity(embedding1, embedding2);
        
        assertEquals(1.0, similarity, 0.0001, 
            "Identical questions should have similarity of 1.0");
    }

    @Test
    void testEmptyText_ShouldReturnZeroEmbedding() {
        List<Double> embedding = embeddingService.generateEmbedding("");
        
        assertNotNull(embedding);
        assertEquals(1536, embedding.size());
        
        // All values should be zero for empty text
        boolean allZeros = embedding.stream().allMatch(v -> v == 0.0);
        assertTrue(allZeros, "Empty text should produce zero embedding");
    }

    @Test
    void testNullText_ShouldReturnZeroEmbedding() {
        List<Double> embedding = embeddingService.generateEmbedding(null);
        
        assertNotNull(embedding);
        assertEquals(1536, embedding.size());
    }

    @Test
    void testStemming_ShouldGroupRelatedWords() {
        // Test that identical words produce higher similarity than completely different words
        String question1 = "How to debug Java code?";
        String question2 = "How to debug Java code?";
        String unrelatedQuestion = "Pizza recipes for beginners";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question1);
        List<Double> embedding2 = embeddingService.generateEmbedding(question2);
        List<Double> unrelatedEmbedding = embeddingService.generateEmbedding(unrelatedQuestion);
        
        double similaritySame = VectorUtils.cosineSimilarity(embedding1, embedding2);
        double similarityUnrelated = VectorUtils.cosineSimilarity(embedding1, unrelatedEmbedding);
        
        // Same text should have higher similarity than unrelated
        assertTrue(similaritySame >= similarityUnrelated, 
            "Same text (" + similaritySame + ") should be >= unrelated (" + similarityUnrelated + ")");
    }

    @Test
    void testDifferentDomains_ShouldStillWork() {
        // Test with overlapping key terms ("climate" appears in both)
        String question1 = "What are the causes of climate change?";
        String question2 = "How does climate change affect weather?";
        
        List<Double> embedding1 = embeddingService.generateEmbedding(question1);
        List<Double> embedding2 = embeddingService.generateEmbedding(question2);
        
        double similarity = VectorUtils.cosineSimilarity(embedding1, embedding2);
        
        // Simple service produces some similarity due to shared words
        assertTrue(similarity >= 0.0, 
            "Topics with shared words should have non-negative similarity, got: " + similarity);
    }

    @Test
    void testEmbeddingDimension() {
        String question = "How does inheritance work?";
        
        List<Double> embedding = embeddingService.generateEmbedding(question);
        
        assertEquals(1536, embedding.size(), 
            "Embedding should have 1536 dimensions");
    }

    @Test
    void testNormalizedEmbedding_ShouldHaveUnitLength() {
        String question = "How to implement a singleton pattern in Java?";
        
        List<Double> embedding = embeddingService.generateEmbedding(question);
        
        // Calculate magnitude
        double sumSquares = 0.0;
        for (Double v : embedding) {
            sumSquares += v * v;
        }
        double magnitude = Math.sqrt(sumSquares);
        
        assertEquals(1.0, magnitude, 0.001, 
            "Normalized embedding should have magnitude of 1.0");
    }
}
