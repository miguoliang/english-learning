import type { Card } from "../types";

interface UseCardReviewParams {
  cards: Card[];
  currentIndex: number;
  setCurrentIndex: (updater: (i: number) => number) => void;
  setCards: (cards: Card[]) => void;
  resetFlip: () => void;
}

export function useCardReview({
  cards,
  currentIndex,
  setCurrentIndex,
  setCards,
  resetFlip,
}: UseCardReviewParams) {
  const handleRate = async (quality: number) => {
    const card = cards[currentIndex];
    await fetch(`/api/cards/${card.id}/review`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ quality }),
    });

    if (currentIndex < cards.length - 1) {
      setCurrentIndex((i) => i + 1);
      resetFlip();
    } else {
      setCards([]);
      alert("今日复习完成！明天再来！");
    }
  };

  return { handleRate };
}

