use axum::{
    extract::{Path, Query, State},
    Json,
};
use std::sync::Arc;

use crate::{
    auth::Claims,
    error::Result,
    models::{Knowledge, Page},
    services::KnowledgeService,
    api::{AppState, PaginationParams},
};

pub async fn list_knowledge(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Query(params): Query<PaginationParams>,
) -> Result<Json<Page<Knowledge>>> {
    let size = params.size.min(100);
    let page = KnowledgeService::get_knowledge_list(&state.db, params.page, size).await?;
    Ok(Json(page))
}

pub async fn get_knowledge(
    State(state): State<Arc<AppState>>,
    _claims: Claims,
    Path(code): Path<String>,
) -> Result<Json<Knowledge>> {
    let knowledge = KnowledgeService::get_knowledge_by_code(&state.db, &code).await?;
    Ok(Json(knowledge))
}
