use axum::{
    extract::{Path, Query, State},
    routing::{get, post},
    Json, Router,
};
use serde::{Deserialize, Serialize};
use std::sync::Arc;

use crate::{
    auth::{require_client_role, Claims},
    config::Config,
    db::DbPool,
    error::Result,
    models::*,
    services::*,
};

pub struct AppState {
    pub db: DbPool,
    pub config: Config,
}

/// Creates and configures the API router with all endpoints.
/// 
/// Sets up routes for knowledge, card types, account cards, and statistics.
/// All routes are protected by authentication middleware.
/// 
/// # Arguments
/// * `state` - Shared application state containing database pool and configuration
/// 
/// # Returns
/// Configured Axum router with all API endpoints
pub fn routes(state: Arc<AppState>) -> Router {
    Router::new()
        // Knowledge endpoints
        .route("/knowledge", get(list_knowledge))
        .route("/knowledge/:code", get(get_knowledge))
        // Card types endpoints
        .route("/card-types", get(list_card_types))
        .route("/card-types/:code", get(get_card_type))
        // Account card endpoints
        .route("/accounts/me/cards", get(list_my_cards))
        .route("/accounts/me/cards:due", get(get_due_cards))
        .route("/accounts/me/cards:initialize", post(initialize_cards))
        .route("/accounts/me/cards/:card_id", get(get_my_card))
        .route("/accounts/me/cards/:card_id:review", post(review_card))
        // Stats endpoints
        .route("/accounts/me/stats", get(get_my_stats))
        .with_state(state)
}

#[derive(Debug, Deserialize)]
struct PaginationParams {
    #[serde(default)]
    page: i64,
    #[serde(default = "default_page_size")]
    size: i64,
}

/// Returns the default page size for pagination.
/// 
/// Used as a default value when the page size is not specified in query parameters.
/// 
/// # Returns
/// Default page size of 20 items
fn default_page_size() -> i64 {
    20
}

// Knowledge handlers

/// Lists knowledge items with pagination.
/// 
/// Retrieves a paginated list of all knowledge items available in the system.
/// Requires authentication but any authenticated user can access this endpoint.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `_claims` - JWT claims from authenticated user
/// * `params` - Pagination parameters (page number and page size)
/// 
/// # Returns
/// Paginated list of knowledge items
async fn list_knowledge(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<Knowledge>>> {
    let size = params.size.min(100);
    let page = KnowledgeService::get_knowledge_list(&state.db, params.page, size).await?;
    Ok(Json(page))
}

/// Retrieves a specific knowledge item by its code.
/// 
/// Fetches a single knowledge item using its unique code identifier.
/// Requires authentication but any authenticated user can access this endpoint.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `_claims` - JWT claims from authenticated user
/// * `code` - Unique code identifier for the knowledge item
/// 
/// # Returns
/// The knowledge item matching the provided code
async fn get_knowledge(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Path(code): Path<String>,
) -> Result<Json<Knowledge>> {
    let knowledge = KnowledgeService::get_knowledge_by_code(&state.db, &code).await?;
    Ok(Json(knowledge))
}

// Card type handlers

/// Lists card types with pagination.
/// 
/// Retrieves a paginated list of all card types available in the system.
/// Requires authentication but any authenticated user can access this endpoint.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `_claims` - JWT claims from authenticated user
/// * `params` - Pagination parameters (page number and page size)
/// 
/// # Returns
/// Paginated list of card types
async fn list_card_types(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<CardType>>> {
    let size = params.size.min(100);
    let page = CardTypeService::get_card_types(&state.db, params.page, size).await?;
    Ok(Json(page))
}

/// Retrieves a specific card type by its code.
/// 
/// Fetches a single card type using its unique code identifier.
/// Requires authentication but any authenticated user can access this endpoint.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `_claims` - JWT claims from authenticated user
/// * `code` - Unique code identifier for the card type
/// 
/// # Returns
/// The card type matching the provided code
async fn get_card_type(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Path(code): Path<String>,
) -> Result<Json<CardType>> {
    let card_type = CardTypeService::get_card_type_by_code(&state.db, &code).await?;
    Ok(Json(card_type))
}

// Account card handlers

#[derive(Debug, Deserialize)]
struct CardListParams {
    #[serde(default)]
    page: i64,
    #[serde(default = "default_page_size")]
    size: i64,
    card_type_code: Option<String>,
}

/// Lists cards belonging to the authenticated user with pagination.
/// 
/// Retrieves a paginated list of cards for the currently authenticated user.
/// Optionally filters by card type code. Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// * `params` - Query parameters including pagination and optional card type filter
/// 
/// # Returns
/// Paginated list of account cards for the authenticated user
async fn list_my_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Query(params): Query<CardListParams>,
) -> Result<Json<Page<AccountCard>>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

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

/// Retrieves cards that are due for review for the authenticated user.
/// 
/// Fetches a paginated list of cards that are scheduled for review based on
/// their spaced repetition algorithm calculations. Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// * `params` - Pagination parameters (page number and page size)
/// 
/// # Returns
/// Paginated list of cards due for review
async fn get_due_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<AccountCard>>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

    let size = params.size.min(100);
    let page = AccountCardService::get_due_cards(&state.db, account_id, params.page, size).await?;

    Ok(Json(page))
}

/// Retrieves a specific card by ID for the authenticated user.
/// 
/// Fetches a single card by its ID, ensuring it belongs to the authenticated user.
/// Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// * `card_id` - Unique identifier of the card to retrieve
/// 
/// # Returns
/// The account card matching the provided ID and belonging to the authenticated user
async fn get_my_card(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(card_id): Path<i64>,
) -> Result<Json<AccountCard>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

    let card = AccountCardService::get_card_by_id(&state.db, card_id, account_id).await?;
    Ok(Json(card))
}

#[derive(Debug, Deserialize)]
struct ReviewRequest {
    quality: i32,
}

/// Reviews a card with a quality rating and updates its spaced repetition schedule.
/// 
/// Processes a card review by recording the user's quality rating (typically 0-5),
/// updates the card's next review date using the SM-2 algorithm, and returns
/// the updated card. Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// * `card_id` - Unique identifier of the card being reviewed
/// * `req` - Review request containing the quality rating
/// 
/// # Returns
/// The updated account card with new review schedule
async fn review_card(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(card_id): Path<i64>,
    Json(req): Json<ReviewRequest>,
) -> Result<Json<AccountCard>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

    let card = AccountCardService::review_card(&state.db, card_id, account_id, req.quality).await?;
    Ok(Json(card))
}

#[derive(Debug, Serialize)]
struct InitializeCardsResponse {
    created: i64,
    skipped: i64,
}

/// Initializes cards for the authenticated user based on available knowledge items.
/// 
/// Creates account cards for all knowledge items that the user doesn't already have cards for.
/// Skips cards that already exist. This is typically called when a user first starts
/// using the system or wants to add new knowledge items to their study deck.
/// Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// 
/// # Returns
/// Response containing the count of created and skipped cards
async fn initialize_cards(
    State(state): State<Arc<AppState>>,
    claims: Claims,
) -> Result<Json<InitializeCardsResponse>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

    let (created, skipped) = AccountCardService::initialize_cards(&state.db, account_id).await?;

    Ok(Json(InitializeCardsResponse { created, skipped }))
}

/// Retrieves statistics for the authenticated user.
/// 
/// Fetches learning statistics including card counts, review counts, and other
/// metrics related to the user's progress. Requires client role authentication.
/// 
/// # Arguments
/// * `state` - Shared application state
/// * `claims` - JWT claims from authenticated user (used to identify the account)
/// 
/// # Returns
/// Statistics object containing various learning metrics
async fn get_my_stats(
    State(state): State<Arc<AppState>>,
    claims: Claims,
) -> Result<Json<Stats>> {
    require_client_role(&claims)?;

    let account_id = claims.sub.parse::<i64>()
        .map_err(|_| crate::error::AppError::BadRequest("Invalid account ID".to_string()))?;

    let stats = StatsService::get_stats(&state.db, account_id).await?;
    Ok(Json(stats))
}
