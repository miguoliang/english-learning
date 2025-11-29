use axum::{extract::State, Json};
use std::sync::Arc;

use crate::{
    auth::{require_client_role, Claims},
    error::{AppError, Result},
    models::Stats,
    services::StatsService,
    api::AppState,
};

pub async fn get_my_stats(
    State(state): State<Arc<AppState>>,
    claims: Claims,
) -> Result<Json<Stats>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let stats = StatsService::get_stats(&state.db, account_id).await?;
    Ok(Json(stats))
}
