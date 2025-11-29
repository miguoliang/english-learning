use axum::{
    extract::{Path, Query, State},
    Json,
};
use std::sync::Arc;

use crate::{
    auth::Claims,
    error::Result,
    models::{CardType, Page},
    services::CardTypeService,
    api::{AppState, PaginationParams},
};

pub async fn list_card_types(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<CardType>>> {
    let size = params.size.min(100);
    let page = CardTypeService::get_card_types(&state.db, params.page, size).await?;
    Ok(Json(page))
}

pub async fn get_card_type(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Path(code): Path<String>,
) -> Result<Json<CardType>> {
    let card_type = CardTypeService::get_card_type_by_code(&state.db, &code).await?;
    Ok(Json(card_type))
}
