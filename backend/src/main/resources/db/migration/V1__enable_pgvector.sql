-- Enable pgvector for semantic similarity search
CREATE EXTENSION IF NOT EXISTS vector;

-- Add 1536-dimension embedding column (matches OpenAI text-embedding-3-small)
ALTER TABLE questions 
ADD COLUMN IF NOT EXISTS embedding vector(1536);

-- IVFFlat index for fast cosine similarity searches
-- Tune 'lists' based on dataset size: lists ≈ sqrt(total_rows), default 100 for <10k rows
CREATE INDEX IF NOT EXISTS questions_embedding_idx 
ON questions 
USING ivfflat (embedding vector_cosine_ops)
WITH (lists = 100);
