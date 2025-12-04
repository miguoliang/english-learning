import { useEffect, useState } from "react";
import { createClient } from "@/lib/supabaseClient";

export function useDueCount() {
  const [due, setDue] = useState(0);
  const supabase = createClient();

  useEffect(() => {
    const fetchDue = async () => {
      const {
        data: { user },
      } = await supabase.auth.getUser();
      if (!user) return;
      const { count } = await supabase
        .from("account_cards")
        .select("*", { count: "exact", head: true })
        .eq("account_id", user.id)
        .lte("next_review_date", new Date().toISOString());
      setDue(count || 0);
    };
    fetchDue();
    const interval = setInterval(fetchDue, 60000); // 每分钟更新一次
    return () => clearInterval(interval);
  }, [supabase]);

  return due;
}

