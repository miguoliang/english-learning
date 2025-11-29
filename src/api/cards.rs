use axum::{
    extract::{Path, Query, State},
    Json,
};
use serde::{Deserialize, Serialize};
use std::sync::Arc;

use crate::{
    auth::{require_client_role, Claims},
    error::{AppError, Result},
    models::{AccountCard, Page},
    services::AccountCardService,
    api::{AppState, PaginationParams, default_page_size},
};

#[derive(Debug, Deserialize)]
pub struct CardListParams {
    #[serde(default)]
    pub page: i64,
    #[serde(default = "default_page_size")]
    pub size: i64,
    pub card_type_code: Option<String>,
}

pub async fn list_my_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Query(params): Query<CardListParams>,
) -> Result<Json<Page<AccountCard>>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let size = params.size.min(100);
    let page = AccountCardService::get_cards(
        &state.db,
        account_id,
        params.page,
        size,
        params.card_type_code,
    )
    .await?;

    Ok(Json(page))
}

pub async fn get_due_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<AccountCard>>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let size = params.size.min(100);
    let page = AccountCardService::get_due_cards(&state.db, account_id, params.page, size).await?;

    Ok(Json(page))
}

pub async fn get_my_card(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(card_id): Path<i64>,
) -> Result<Json<AccountCard>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let card = AccountCardService::get_card_by_id(&state.db, card_id, account_id).await?;
    Ok(Json(card))
}

#[derive(Debug, Deserialize)]
pub struct ReviewRequest {
    pub quality: i32,
}

pub async fn review_card(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(card_id): Path<i64>,
    Json(req): Json<ReviewRequest>,
) -> Result<Json<AccountCard>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let card = AccountCardService::review_card(&state.db, card_id, account_id, req.quality).await?;
    Ok(Json(card))
}

#[derive(Debug, Serialize)]
pub struct InitializeCardsResponse {
    pub created: i64,
    pub skipped: i64,
}

pub async fn initialize_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
) -> Result<Json<InitializeCardsResponse>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let (created, skipped) = AccountCardService::initialize_cards(&state.db, account_id).await?;

    Ok(Json(InitializeCardsResponse { created, skipped }))
}
