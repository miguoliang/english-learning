use axum::{
    extract::{Path, Query, State},
    http::StatusCode,
    Json,
};
use std::sync::Arc;

use crate::{
    auth::{require_operator_role, Claims},
    error::{AppError, Result},
    models::{ChangeRequest, CreateKnowledgeRequest, Knowledge, Page, UpdateKnowledgeRequest},
    services::{ChangeRequestService, KnowledgeService},
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
) -> Result<(StatusCode, Json<ChangeRequest>)> {
    require_operator_role(&claims)?;
    
    let submitter_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let payload = serde_json::to_value(req)
        .map_err(|e| AppError::InternalServerError(format!("Serialization error: {}", e)))?;

    let request = ChangeRequestService::create_request(
        &state.db,
        "CREATE",
        None,
        payload,
        submitter_id,
    )
    .await?;

    Ok((StatusCode::ACCEPTED, Json(request)))
}

pub async fn update_knowledge(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(code): Path<String>,
    Json(req): Json<UpdateKnowledgeRequest>,
) -> Result<(StatusCode, Json<ChangeRequest>)> {
    require_operator_role(&claims)?;

    let submitter_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let payload = serde_json::to_value(req)
        .map_err(|e| AppError::InternalServerError(format!("Serialization error: {}", e)))?;

    let request = ChangeRequestService::create_request(
        &state.db,
        "UPDATE",
        Some(code),
        payload,
        submitter_id,
    )
    .await?;

    Ok((StatusCode::ACCEPTED, Json(request)))
}

pub async fn delete_knowledge(
    State(state): State<Arc<AppState>>,
    claims: Claims,
    Path(code): Path<String>,
) -> Result<(StatusCode, Json<ChangeRequest>)> {
    require_operator_role(&claims)?;

    let submitter_id = claims.sub.parse::<i64>()
        .map_err(|_| AppError::BadRequest("Invalid account ID".to_string()))?;

    let request = ChangeRequestService::create_request(
        &state.db,
        "DELETE",
        Some(code),
        serde_json::Value::Null,
        submitter_id,
    )
    .await?;

    Ok((StatusCode::ACCEPTED, Json(request)))
}
