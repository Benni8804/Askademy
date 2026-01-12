package com.eduhub.service;

import java.util.List;

/**
 * Service interface for generating vector embeddings from text.
 * Used to convert questions into semantic vectors for similarity search.
 */
public interface EmbeddingService {
    
    /**
     * Converts text into a vector embedding.
     * 
     * @param text The input text to be embedded (e.g., question title + content)
     * @return A vector of 1536 dimensions (standard for OpenAI text-embedding-3-small)
     */
    List<Double> generateEmbedding(String text);
}
