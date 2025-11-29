use crate::db::DbPool;
use crate::error::Result;
use crate::models::Stats;
use chrono::Utc;

pub struct StatsService;

impl StatsService {
    pub async fn get_stats(pool: &DbPool, account_id: i64) -> Result<Stats> {
        let total_cards: i64 = sqlx::query_scalar(
            "SELECT COUNT(*) FROM account_cards WHERE account_id = $1",
        )
        .bind(account_id)
        .fetch_one(pool)
        .await?;

        let new_cards: i64 = sqlx::query_scalar(
            "SELECT COUNT(*) FROM account_cards WHERE account_id = $1 AND repetitions = 0",
        )
        .bind(account_id)
        .fetch_one(pool)
        .await?;

        let learning_cards: i64 = sqlx::query_scalar(
            "SELECT COUNT(*) FROM account_cards WHERE account_id = $1 AND repetitions > 0 AND repetitions < 3",
        )
        .bind(account_id)
        .fetch_one(pool)
        .await?;

        let due_today: i64 = sqlx::query_scalar(
            "SELECT COUNT(*) FROM account_cards WHERE account_id = $1 AND next_review_date <= $2",
        )
        .bind(account_id)
        .bind(Utc::now())
        .fetch_one(pool)
        .await?;

        // Get breakdown by card type
        let by_card_type_rows = sqlx::query_as::<_, (String, i64)>(
            "SELECT card_type_code, COUNT(*) as count FROM account_cards WHERE account_id = $1 GROUP BY card_type_code",
        )
        .bind(account_id)
        .fetch_all(pool)
        .await?;

        let by_card_type = by_card_type_rows
            .into_iter()
            .collect::<std::collections::HashMap<String, i64>>();

        Ok(Stats {
            total_cards,
            new_cards,
            learning_cards,
            due_today,
            by_card_type,
        })
    }
}
