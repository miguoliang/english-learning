export interface KnowledgeMetadata {
  phonetic?: string;
  [key: string]: unknown;
}

export interface Knowledge {
  code: string;
  name: string;
  description: string;
  metadata: KnowledgeMetadata;
}

export interface Card {
  id: number;
  knowledge_code: string;
  knowledge: Knowledge;
  next_review_date: string;
}

