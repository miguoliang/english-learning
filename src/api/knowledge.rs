use axum::{
    extract::{Path, Query, State},
    http::StatusCode,
    Json,
};
use std::sync::Arc;

use crate::{
    auth::{require_operator_role, Claims},
    error::Result,
    models::{CreateKnowledgeRequest, Knowledge, Page, UpdateKnowledgeRequest},
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

pub async fn create_knowledge(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Json(req): Json<CreateKnowledgeRequest>,
) -> Result<(StatusCode, Json<Knowledge>)> {
    require_operator_role(&claims)?;
    let knowledge = KnowledgeService::create_knowledge(&state.db, req, &claims.sub).await?;
    Ok((StatusCode::CREATED, Json(knowledge)))
}

pub async fn update_knowledge(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(code): Path<String>,
    Json(req): Json<UpdateKnowledgeRequest>,
) -> Result<Json<Knowledge>> {
    require_operator_role(&claims)?;
    let knowledge = KnowledgeService::update_knowledge(&state.db, &code, req, &claims.sub).await?;
    Ok(Json(knowledge))
}

pub async fn delete_knowledge(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(code): Path<String>,
) -> Result<StatusCode> {
    require_operator_role(&claims)?;
    KnowledgeService::delete_knowledge(&state.db, &code).await?;
    Ok(StatusCode::NO_CONTENT)
}
