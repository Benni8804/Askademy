package com.eduhub.util;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class VectorUtilsTest {

    @Test
    void testCosineSimilarity_IdenticalVectors() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(1.0, 2.0, 3.0);
        
        double similarity = VectorUtils.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(1.0, similarity, 0.0001, "Identical vectors should have similarity of 1.0");
    }

    @Test
    void testCosineSimilarity_OrthogonalVectors() {
        List<Double> vectorA = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> vectorB = Arrays.asList(0.0, 1.0, 0.0);
        
        double similarity = VectorUtils.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(0.0, similarity, 0.0001, "Orthogonal vectors should have similarity of 0.0");
    }

    @Test
    void testCosineSimilarity_OppositeVectors() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(-1.0, -2.0, -3.0);
        
        double similarity = VectorUtils.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(-1.0, similarity, 0.0001, "Opposite vectors should have similarity of -1.0");
    }

    @Test
    void testCosineSimilarity_SimilarVectors() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(1.1, 2.1, 2.9);
        
        double similarity = VectorUtils.cosineSimilarity(vectorA, vectorB);
        
        assertTrue(similarity > 0.95, "Similar vectors should have high similarity");
    }

    @Test
    void testCosineSimilarity_DifferentDimensions() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(1.0, 2.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            VectorUtils.cosineSimilarity(vectorA, vectorB);
        }, "Should throw exception for different dimensions");
    }

    @Test
    void testCosineSimilarity_NullVectors() {
        List<Double> vectorA = null;
        List<Double> vectorB = Arrays.asList(1.0, 2.0, 3.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            VectorUtils.cosineSimilarity(vectorA, vectorB);
        }, "Should throw exception for null vectors");
    }

    @Test
    void testCosineSimilarity_ZeroVector() {
        List<Double> vectorA = Arrays.asList(0.0, 0.0, 0.0);
        List<Double> vectorB = Arrays.asList(1.0, 2.0, 3.0);
        
        double similarity = VectorUtils.cosineSimilarity(vectorA, vectorB);
        
        assertEquals(0.0, similarity, 0.0001, "Zero vector should have similarity of 0.0");
    }

    @Test
    void testIsNormalized_UnitVector() {
        // Unit vector in 3D space
        List<Double> vector = Arrays.asList(1.0, 0.0, 0.0);
        
        assertTrue(VectorUtils.isNormalized(vector, 1e-6), "Unit vector should be normalized");
    }

    @Test
    void testIsNormalized_NormalizedVector() {
        // Normalized vector: sqrt(0.6^2 + 0.8^2) = 1.0
        List<Double> vector = Arrays.asList(0.6, 0.8, 0.0);
        
        assertTrue(VectorUtils.isNormalized(vector, 1e-6), "Vector with magnitude 1 should be normalized");
    }

    @Test
    void testIsNormalized_NotNormalizedVector() {
        List<Double> vector = Arrays.asList(1.0, 2.0, 3.0);
        
        assertFalse(VectorUtils.isNormalized(vector, 1e-6), "Vector with magnitude != 1 should not be normalized");
    }

    @Test
    void testEuclideanDistance_IdenticalVectors() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(1.0, 2.0, 3.0);
        
        double distance = VectorUtils.euclideanDistance(vectorA, vectorB);
        
        assertEquals(0.0, distance, 0.0001, "Identical vectors should have distance of 0.0");
    }

    @Test
    void testEuclideanDistance_KnownDistance() {
        // Distance between (0,0,0) and (3,4,0) = 5
        List<Double> vectorA = Arrays.asList(0.0, 0.0, 0.0);
        List<Double> vectorB = Arrays.asList(3.0, 4.0, 0.0);
        
        double distance = VectorUtils.euclideanDistance(vectorA, vectorB);
        
        assertEquals(5.0, distance, 0.0001, "3-4-5 triangle distance should be 5.0");
    }

    @Test
    void testEuclideanDistance_DifferentDimensions() {
        List<Double> vectorA = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> vectorB = Arrays.asList(1.0, 2.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            VectorUtils.euclideanDistance(vectorA, vectorB);
        }, "Should throw exception for different dimensions");
    }
}
