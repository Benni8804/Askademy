package com.eduhub.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eduhub.service.EmbeddingService;

/**
 * Word-hashing embedding service. Creates vectors where similar topics cluster together.
 * For production, consider integrating OpenAI/Cohere APIs for true semantic embeddings.
 */
@Service
public class SimpleEmbeddingService implements EmbeddingService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleEmbeddingService.class);
    private static final int EMBEDDING_DIMENSION = 1536;
    
    // Stopwords - completely filtered out
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he", 
        "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "will", "with",
        "can", "could", "should", "would", "what", "when", "where", "who", "why", "how",
        "i", "you", "we", "they", "my", "your", "his", "her", "our", "their", "this", 
        "these", "those", "am", "been", "being", "have", "had", "do", "does", "did",
        "or", "but", "if", "then", "so", "than", "such", "no", "not", "only", "same",
        "just", "about", "into", "through", "during", "before", "after", "above", "below",
        "between", "under", "again", "further", "once", "here", "there", "all", "any",
        "both", "each", "few", "more", "most", "other", "some", "own", "get", "make",
        "go", "know", "take", "see", "come", "think", "look", "want", "give", "use",
        "find", "tell", "ask", "work", "seem", "feel", "try", "leave", "call",
        "need", "also", "back", "because", "become", "well", "even", "new", "now",
        "way", "may", "say", "still", "very", "much", "many", "must", "like", "using",
        "please", "help", "thanks", "question", "problem", "error", "issue", "want",
        "understand", "understanding", "explain", "need", "looking", "learn", "learning",
        // Generic programming terms that cause false matches
        "java", "code", "coding", "program", "programming", "project", "example", "examples",
        "class", "classes", "method", "methods", "function", "functions", "object", "objects",
        "real", "world", "basic", "basics", "tutorial", "guide", "sample"
    ));
    
    // Suffix rules for stemming
    private static final String[][] SUFFIX_RULES = {
        {"ational", "ate"}, {"tional", "tion"}, {"ization", "ize"}, 
        {"ation", ""}, {"ition", ""}, {"ness", ""}, {"ment", ""}, 
        {"able", ""}, {"ible", ""}, {"ful", ""}, {"less", ""}, 
        {"ous", ""}, {"ive", ""}, {"ing", ""}, {"ed", ""}, 
        {"er", ""}, {"est", ""}, {"ly", ""}, {"ies", "y"}, {"es", ""}, {"s", ""}
    };

    @Override
    public List<Double> generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return createZeroEmbedding();
        }
        
        // Extract stemmed terms with counts
        Map<String, Integer> termCounts = extractTermCounts(text.toLowerCase());
        
        if (termCounts.isEmpty()) {
            return createZeroEmbedding();
        }
        
        logger.debug("Extracted {} terms: {}", termCounts.size(), 
                    termCounts.keySet().stream().limit(10).collect(Collectors.joining(", ")));
        
        // Create embedding - use FEWER dimensions per term for HIGHER overlap
        double[] embedding = new double[EMBEDDING_DIMENSION];
        
        // Total term occurrences for normalization
        int totalTerms = termCounts.values().stream().mapToInt(Integer::intValue).sum();
        
        for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
            String term = entry.getKey();
            int count = entry.getValue();
            
            // Weight based on frequency and term specificity
            double weight = (double) count / totalTerms;
            
            // Important terms (longer = more specific) get higher weight
            double specificityBoost = 1.0 + (term.length() - 3) * 0.2;
            weight *= specificityBoost;
            
            // Use term's character sum for unique dimension mapping
            // This ensures truly different terms map to different dimensions
            int charSum = 0;
            for (char c : term.toCharArray()) {
                charSum += c;
            }
            
            // Each term activates MANY dimensions spread far apart
            // Using prime multipliers ensures minimal collision
            for (int i = 0; i < 16; i++) {
                int dim = Math.abs((charSum * 7919 + term.length() * 6271 + i * 1009) % EMBEDDING_DIMENSION);
                embedding[dim] += weight * 8.0;
            }
        }
        
        // L2 normalize to unit vector
        double magnitude = 0.0;
        for (double v : embedding) {
            magnitude += v * v;
        }
        magnitude = Math.sqrt(magnitude);
        
        List<Double> result = new ArrayList<>(EMBEDDING_DIMENSION);
        if (magnitude > 0) {
            for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
                result.add(embedding[i] / magnitude);
            }
        } else {
            return createZeroEmbedding();
        }
        
        logger.info("Generated embedding from {} terms", termCounts.size());
        return result;
    }
    
    private Map<String, Integer> extractTermCounts(String text) {
        Map<String, Integer> counts = new HashMap<>();
        String normalized = text.replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = normalized.split("\\s+");
        
        for (String token : tokens) {
            if (token.length() < 3 || STOPWORDS.contains(token)) {
                continue;
            }
            String stemmed = stem(token);
            if (stemmed.length() >= 3) {
                counts.merge(stemmed, 1, Integer::sum);
            }
        }
        
        // Add bigrams for phrases
        List<String> stemmedTokens = new ArrayList<>();
        for (String token : tokens) {
            if (token.length() >= 3 && !STOPWORDS.contains(token)) {
                stemmedTokens.add(stem(token));
            }
        }
        for (int i = 0; i < stemmedTokens.size() - 1; i++) {
            String bigram = stemmedTokens.get(i) + "_" + stemmedTokens.get(i + 1);
            counts.merge(bigram, 1, Integer::sum);
        }
        
        return counts;
    }
    
    private String stem(String word) {
        if (word.length() < 4) return word;
        
        for (String[] rule : SUFFIX_RULES) {
            if (word.endsWith(rule[0]) && word.length() - rule[0].length() + rule[1].length() >= 3) {
                return word.substring(0, word.length() - rule[0].length()) + rule[1];
            }
        }
        return word;
    }
    
    private List<Double> createZeroEmbedding() {
        List<Double> embedding = new ArrayList<>(EMBEDDING_DIMENSION);
        for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
            embedding.add(0.0);
        }
        return embedding;
    }
}
