import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import type { Card } from "../types";

export function useCards() {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    let isMounted = true;

    const loadCards = async () => {
      const res = await fetch("/api/cards/due");
      if (!res.ok) {
        if (res.status === 401 && isMounted) {
          router.push("/");
        }
        return;
      }
      const data = await res.json();
      if (isMounted) {
        setCards(data);
        setLoading(false);
      }
    };

    loadCards();

    return () => {
      isMounted = false;
    };
  }, [router]);

  return { cards, setCards, loading };
}

