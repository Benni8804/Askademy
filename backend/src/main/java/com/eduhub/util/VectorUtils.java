package com.eduhub.util;

import java.util.List;

/**
 * Utility class for vector operations, specifically for semantic similarity calculations.
 */
public class VectorUtils {

    /**
     * Calculate cosine similarity between two vectors.
     * Cosine similarity = (A · B) / (||A|| * ||B||)
     * 
     * @param vectorA First vector
     * @param vectorB Second vector
     * @return Cosine similarity score between -1 and 1 (typically 0 to 1 for normalized vectors)
     * @throws IllegalArgumentException if vectors have different dimensions or are null/empty
     */
    public static double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }
        
        if (vectorA.isEmpty() || vectorB.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be empty");
        }
        
        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException(
                String.format("Vectors must have the same dimension. Got %d and %d", 
                    vectorA.size(), vectorB.size())
            );
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            double a = vectorA.get(i);
            double b = vectorB.get(i);
            
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        // Avoid division by zero
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (normA * normB);
    }

    /**
     * Check if a vector is normalized (has magnitude of 1).
     * Useful for verifying embedding vectors from the embedding service.
     * 
     * @param vector The vector to check
     * @param tolerance Tolerance for floating-point comparison (e.g., 1e-6)
     * @return true if the vector is normalized within the tolerance
     */
    public static boolean isNormalized(List<Double> vector, double tolerance) {
        if (vector == null || vector.isEmpty()) {
            return false;
        }

        double magnitude = 0.0;
        for (Double value : vector) {
            magnitude += value * value;
        }
        magnitude = Math.sqrt(magnitude);

        return Math.abs(magnitude - 1.0) < tolerance;
    }

    /**
     * Calculate the Euclidean distance between two vectors.
     * This can be used as an alternative similarity metric.
     * 
     * @param vectorA First vector
     * @param vectorB Second vector
     * @return Euclidean distance
     */
    public static double euclideanDistance(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("Vectors cannot be null");
        }
        
        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double sum = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            double diff = vectorA.get(i) - vectorB.get(i);
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }
}
