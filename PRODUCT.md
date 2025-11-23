# English Learning System - Product Design

## System Overview

An English learning system that helps users learn vocabulary and knowledge through spaced repetition. The system uses the SM-2 algorithm to optimize learning schedules, presenting cards for review at optimal intervals. Knowledge content is managed by operators through CSV batch imports with validation and approval workflows.

## Key Features

- **Spaced Repetition Learning**: SM-2 algorithm for optimal review scheduling
- **Knowledge Management**: Predefined knowledge items organized by levels
- **Card Types**: Multiple learning patterns (e.g., word-to-definition, definition-to-word)
- **Progress Tracking**: Statistics and learning history
- **Batch Import**: CSV-based knowledge management with validation and approval

---

## User Roles

### Client (Learner)
End users who use the system to learn English vocabulary and knowledge through spaced repetition.

**Capabilities**:
- Review cards due for learning
- Submit quality ratings (0-5) for reviewed cards
- View learning statistics and progress
- Browse knowledge items and card types

### Operator (Content Manager)
Internal users who manage the knowledge content and system operations.

**Capabilities**:
- Export knowledge items to CSV
- Upload CSV files for batch import
- Review and approve/reject import changes
- Monitor import workflows
- Access all system data

---

## Client Operations

### Card Review Operation

**Flow**:
1. User requests cards due for review
2. System retrieves cards where `next_review_date <= today`
3. System renders card content using templates (front and back)
4. User reviews card and submits quality rating (0-5)
5. System applies SM-2 algorithm to calculate next review date
6. System updates card state and records review history
7. User sees updated card with new review schedule

**User Actions**:
- View due cards
- Review cards
- Submit quality rating

**System Behaviors**:
- Filter cards by due date
- Render card content from templates
- Calculate optimal next review date using SM-2
- Track learning progress

**Quality Rating Scale**:
- `0` = Again (complete blackout)
- `1` = Hard
- `2` = Good
- `3` = Easy
- `4` = Very easy
- `5` = Perfect

### Statistics Viewing Operation

**Flow**:
1. User requests learning statistics
2. System calculates statistics from user's cards:
   - Total cards
   - New cards (not yet reviewed)
   - Learning cards (in progress)
   - Due today
   - Breakdown by card type
3. User views progress dashboard

**User Actions**:
- View learning statistics

**System Behaviors**:
- Aggregate card statistics
- Calculate learning progress metrics
- Group by card types

### Card Initialization Workflow

A background job that creates cards for all knowledge-card type combinations after user signup.

**Workflow Steps**:
1. User signs up for an account
2. System triggers background workflow for card initialization
3. Workflow loads available knowledge items (potentially thousands)
4. Workflow loads all card types
5. Workflow creates cards in batches for each knowledge-card type combination:
   - Batch processing to avoid timeouts
   - Progress tracking for monitoring
   - Error handling and retry logic
6. Workflow creates cards with default SM-2 values
7. Workflow completes and notifies system
8. User account is ready for learning

**Note**: This happens automatically in the background. Users can start learning immediately once their account is ready.

---

## Operator Operations

### Knowledge Management Workflow

A multi-step workflow for managing knowledge content through CSV batch imports with validation and approval.

#### 1. Export CSV (Optional)

**Purpose**: Export current knowledge data for editing

**Flow**:
- Operator exports all knowledge to CSV file
- Exported CSV includes code column with all existing codes filled in
- Operator can modify the CSV file (add new rows, update existing rows, remove rows)

**CSV Format** (exported):
```csv
code,name,description,metadata:level,metadata:type
ST-0000001,Example 1,Description 1,A1,vocabulary
ST-0000002,Example 2,Description 2,A2,phrase
```

#### 2. Upload CSV & Start Workflow

**Purpose**: Begin the import process

**Flow**:
- Operator uploads modified CSV file
- CSV format:
  - **Existing items**: Code column is filled (e.g., `ST-0000001`)
  - **New items**: Code column is empty (codes will be generated after approval)
  - **Metadata fields**: Flat columns with `metadata:` prefix (e.g., `metadata:level`, `metadata:type`)
- System triggers workflow for knowledge import
- Workflow stores CSV data temporarily

**CSV Format** (for import):
```csv
code,name,description,metadata:level,metadata:type
ST-0000001,Updated Example 1,Updated Description 1,A1,vocabulary
,New Example 2,New Description 2,A2,phrase
ST-0000003,Another Updated,Another Description,B1,idiom
```

**Column Rules**:
- **Code column**: Optional
  - **Filled code**: Identifies existing item for update
  - **Empty code**: Identifies new item (code will be generated after approval)
- **Metadata columns**: Optional, unlimited
  - Format: `metadata:key_name`
  - Example: `metadata:level=A1` becomes `{"level": "A1"}` in database

#### 3. Validation

**Purpose**: Ensure data quality

**Automatic Checks**:
- Validates data legality (format, required fields)
- Parses metadata columns and converts to structured format
- Validates code format if provided
- Stores validation results

**Results**:
- Total rows processed
- Valid rows count
- Error count with details (row number, field, message)

#### 4. Comparison

**Purpose**: Identify changes between CSV and current data

**Automatic Analysis**:
- Compares CSV with existing database:
  - **For rows with code filled**: Look up by code
    - If exists: Determine if updated or unchanged
    - If doesn't exist: Error (invalid code)
  - **For rows with empty code**: Match by content
    - If match found: Treat as update
    - If no match: Treat as new item
  - **Deleted items**: Items in database but not in CSV

**Results**:
- New items count
- Updated items count
- Unchanged items count
- Deleted items count (if supported)

#### 5. Review & Approval (Human-in-the-loop)

**Purpose**: Operator reviews changes before applying

**Flow**:
- Operator reviews validation results and differences
- Operator approves or rejects changes via API
- Workflow waits for operator decision

**Review Information**:
- Validation errors (if any)
- Comparison summary:
  - New items list
  - Updated items list (showing changes)
  - Deleted items list (if applicable)

#### 6. Apply Changes (If Approved)

**Purpose**: Apply approved changes to system

**Automatic Processing** (if approved):
- Generates codes (ST-0000001, ST-0000002, etc.) for new items
- Applies all changes to database in batches:
  - Inserts new items with generated codes
  - Updates existing items
  - Handles deletions (if supported)
- Progress tracking for large batches
- Error handling and retry logic

**Results**:
- Summary of applied changes
- Generated codes for new items
- Completion timestamp

**If Rejected**:
- Workflow completes without changes
- No data is modified

#### Workflow Key Points

- Export is a simple operation (not a workflow)
- Import is a multi-step workflow with human approval
- Workflow handles long-running operations automatically
- Validation and comparison happen automatically
- Approval is a manual step (human-in-the-loop)
- Code generation happens after approval
- Batch processing ensures scalability for large imports

---

## Data Model

### Core Entities

#### Knowledge
Learning unit with common fields: code, name, description, and metadata.

**Fields**:
- `code`: Immutable identifier (e.g., `ST-0000001`)
- `name`: Knowledge item name
- `description`: Detailed description
- `metadata`: Flexible key-value data (optional)
  - Examples: `{"level": "A1", "type": "vocabulary"}`

**Code Format**: `{PREFIX}-{NUMBER}`
- PREFIX: 2-letter uppercase (ST for standard, CS for custom)
- NUMBER: 7-digit zero-padded (0000001-9999999)
- Examples: `ST-0000001`, `ST-0000002`

#### Knowledge Relationships
Knowledge items can be linked to other knowledge items (e.g., related words, synonyms, antonyms).

**Rules**:
- Many-to-many relationships
- No self-references (item cannot link to itself)
- Bidirectional navigation supported

#### Templates
Reusable templates for rendering card content.

**Fields**:
- `code`: Immutable identifier
- `name`: Unique template name
- `description`: Template description
- `format`: Template format (e.g., "qute")
- `content`: Template content

**Template Variables**:
- `{name}`: Knowledge name
- `{description}`: Knowledge description
- `{metadata.level}`: Metadata access via dot notation
- `{#for item in relatedKnowledge}...{/for}`: Iterate over related knowledge

**Role Assignment**:
- Templates are assigned to card types with roles
- Roles: "front", "back", or custom roles
- Same template can be reused in different roles

#### Card Types
Predefined card patterns that define learning experiences.

**Fields**:
- `code`: Immutable identifier
- `name`: Unique card type name (e.g., "word_to_definition")
- `description`: Card type description

**Template Assignment**:
- Each card type references multiple templates
- Templates assigned with specific roles (front/back)
- Flexible many-to-many relationship

#### Accounts
User accounts (simplified).

**Fields**:
- `id`: Primary key
- `username`: Unique username

**Roles**:
- End-user accounts (learners)
- Internal users (operators)

#### Account Cards
User's learning progress for each knowledge-card type combination.

**Fields**:
- `account_id`: Reference to account
- `knowledge_code`: Reference to knowledge
- `card_type_code`: Reference to card type
- `ease_factor`: SM-2 ease factor (default: 2.5)
- `interval_days`: Current interval in days
- `repetitions`: Number of successful reviews
- `next_review_date`: When card is due for review
- `last_reviewed_at`: Last review timestamp

**SM-2 Algorithm State**:
- Each card tracks its own learning progress
- Algorithm adjusts based on user's quality ratings
- Optimal review intervals calculated automatically

#### Review History
Historical record of all card reviews.

**Fields**:
- `account_card_id`: Reference to account card
- `quality`: Quality rating (0-5)
- `reviewed_at`: Review timestamp

**Purpose**:
- Analytics and progress tracking
- Learning pattern analysis
- Historical performance review

### Data Relationships

```
Account ──┬─→ Account Cards ──→ Review History
          │
Knowledge ─┴─→ Account Cards
          ↓
      Knowledge Rel (self-referential)

Card Types ──→ Card Type Template Rel ──→ Templates
             (with roles)

Card Types ──→ Account Cards
```

**Key Relationships**:
- One account → multiple account cards (one per knowledge-card type combination)
- One knowledge → multiple account cards (one per account per card type)
- One account card → multiple review history entries
- One knowledge ↔ multiple knowledge (self-referential, no self-references)
- One card type ↔ multiple templates (many-to-many with roles)

---

## API Design

### Design Principles

- **Resource-Oriented**: REST principles with clear resource hierarchies
- **Role-Based Access**: Single API with access control via JWT tokens
- **Consistent Naming**: Clear, predictable endpoint patterns
- **Unified Path**: No separate paths for different roles

### Authentication

All endpoints require JWT authentication:

```
Authorization: Bearer <token>
```

**JWT Token Structure**:
```json
{
  "sub": "account-id-or-user-id",
  "role": "client",  // or "operator"
  "iat": 1234567890,
  "exp": 1234567890
}
```

**Roles**:
- `client`: End-user learners (read/action access)
- `operator`: Internal operators (full CRUD access)

### Base Path

```
/api/v1
```

---

## API Endpoints

### Knowledge Endpoints

#### List Knowledge Items
```
GET /api/v1/knowledge
```

**Access**: Both `client` and `operator` roles

**Query Parameters**:
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20, max: 100): Items per page
- `filter` (optional): Filter expression

**Response**: `200 OK`
```json
{
  "content": [
    {
      "code": "ST-0000001",
      "name": "Example",
      "description": "Description",
      "metadata": {"level": "A1", "type": "vocabulary"}
    }
  ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

#### Get Knowledge Item
```
GET /api/v1/knowledge/{code}
```

**Access**: Both `client` and `operator` roles

**Response**: `200 OK`
```json
{
  "code": "ST-0000001",
  "name": "Example",
  "description": "Description",
  "metadata": {"level": "A1"}
}
```

#### Export Knowledge (CSV)
```
GET /api/v1/knowledge:export
```

**Access**: `operator` role only

**Response**: `200 OK`
- Content-Type: `text/csv`
- CSV file with all knowledge items

#### Upload Knowledge (CSV)
```
POST /api/v1/knowledge:upload
```

**Access**: `operator` role only

**Request**:
- Content-Type: `multipart/form-data`
- Body: CSV file

**Response**: `202 Accepted`
```json
{
  "workflowId": "uuid-string",
  "status": "RUNNING",
  "startedAt": "2024-01-01T12:00:00Z"
}
```

**Note**: Use workflow endpoints to monitor progress and approve/reject

---

### Workflow Endpoints

#### Get Workflow Status
```
GET /api/v1/workflows/{workflowId}/status
```

**Access**: Appropriate role based on workflow type

**Response**: `200 OK`
```json
{
  "workflowId": "uuid-string",
  "workflowType": "KnowledgeImportWorkflow",
  "status": "RUNNING",
  "progress": {
    "currentStep": "Validation",
    "completedSteps": ["Upload"],
    "totalSteps": 4
  },
  "queryResults": {
    "validationResults": {
      "total": 100,
      "valid": 95,
      "errors": 5
    }
  }
}
```

**Status Values**:
- `RUNNING`: Currently executing
- `COMPLETED`: Finished successfully
- `FAILED`: Failed with error
- `CANCELED`: Canceled by user

#### Send Workflow Signal
```
POST /api/v1/workflows/{workflowId}/signal
```

**Access**: Appropriate role based on workflow type

**Request Body**:
```json
{
  "signalName": "approval",
  "signalData": {
    "approved": true,
    "reason": "Optional reason"
  }
}
```

**Response**: `200 OK`
```json
{
  "workflowId": "uuid-string",
  "signalSent": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

#### List Workflows
```
GET /api/v1/workflows
```

**Access**: Appropriate role

**Query Parameters**:
- `workflowType` (optional): Filter by type
- `status` (optional): Filter by status
- `page` (optional, default: 0)
- `size` (optional, default: 20, max: 100)

**Response**: `200 OK`
```json
{
  "content": [
    {
      "workflowId": "uuid-string",
      "workflowType": "KnowledgeImportWorkflow",
      "status": "RUNNING",
      "startedAt": "2024-01-01T12:00:00Z"
    }
  ],
  "page": {...}
}
```

#### Cancel Workflow
```
POST /api/v1/workflows/{workflowId}/cancel
```

**Access**: Appropriate role

**Response**: `200 OK`
```json
{
  "workflowId": "uuid-string",
  "canceled": true,
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

### Card Type Endpoints

#### List Card Types
```
GET /api/v1/card-types
```

**Access**: Both `client` and `operator` roles

**Response**: `200 OK`
```json
{
  "content": [
    {
      "code": "ST-0000001",
      "name": "word_to_definition",
      "description": "Card type description"
    }
  ],
  "page": {...}
}
```

#### Get Card Type
```
GET /api/v1/card-types/{code}
```

**Access**: Both `client` and `operator` roles

**Response**: `200 OK`
```json
{
  "code": "ST-0000001",
  "name": "word_to_definition",
  "description": "Card type description"
}
```

---

### Account Card Endpoints (Client)

#### List My Cards
```
GET /api/v1/accounts/me/cards
```

**Access**: `client` role only

**Query Parameters**:
- `card_type_code` (optional): Filter by card type
- `status` (optional): Filter by status
  - `new`: Not yet reviewed
  - `learning`: In progress (repetitions > 0 and < 3)
  - `review`: Due today
  - `all`: No filter (default)
- `page` (optional, default: 0)
- `size` (optional, default: 20, max: 100)

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "knowledge": {...},
      "cardType": {...},
      "easeFactor": 2.5,
      "intervalDays": 1,
      "repetitions": 0,
      "nextReviewDate": "2024-01-01T00:00:00Z",
      "lastReviewedAt": null
    }
  ],
  "page": {...}
}
```

#### Get Cards Due for Review
```
GET /api/v1/accounts/me/cards:due
```

**Access**: `client` role only

**Query Parameters**:
- `card_type_code` (optional)
- `page` (optional, default: 0)
- `size` (optional, default: 20, max: 100)

**Response**: `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "knowledge": {...},
      "cardType": {...},
      "front": "Rendered front content",
      "back": "Rendered back content",
      "easeFactor": 2.5,
      "intervalDays": 1,
      "repetitions": 0,
      "nextReviewDate": "2024-01-01T00:00:00Z"
    }
  ],
  "page": {...}
}
```

#### Get Specific Card
```
GET /api/v1/accounts/me/cards/{cardId}
```

**Access**: `client` role only

**Response**: `200 OK`
```json
{
  "id": 1,
  "knowledge": {...},
  "cardType": {...},
  "easeFactor": 2.5,
  "intervalDays": 1,
  "repetitions": 0,
  "nextReviewDate": "2024-01-01T00:00:00Z",
  "lastReviewedAt": null
}
```

#### Review Card
```
POST /api/v1/accounts/me/cards/{cardId}:review
```

**Access**: `client` role only

**Request Body**:
```json
{
  "quality": 3
}
```

**Quality Values**: 0-5 (see rating scale above)

**Response**: `200 OK`
```json
{
  "id": 1,
  "knowledge": {...},
  "cardType": {...},
  "easeFactor": 2.6,
  "intervalDays": 2,
  "repetitions": 1,
  "nextReviewDate": "2024-01-03T00:00:00Z",
  "lastReviewedAt": "2024-01-01T12:00:00Z"
}
```

#### Initialize Cards
```
POST /api/v1/accounts/me/cards:initialize
```

**Access**: `client` role only

**Request Body** (optional):
```json
{
  "cardTypeCodes": ["ST-0000001", "ST-0000002"]
}
```

**Response**: `200 OK`
```json
{
  "created": 50,
  "skipped": 10
}
```

**Note**: Typically called automatically during signup

---

### Statistics Endpoints

#### Get My Statistics
```
GET /api/v1/accounts/me/stats
```

**Access**: `client` role only

**Response**: `200 OK`
```json
{
  "totalCards": 100,
  "newCards": 20,
  "learningCards": 30,
  "dueToday": 15,
  "byCardType": {
    "ST-0000001": 50,
    "ST-0000002": 50
  }
}
```

---

### Error Responses

All endpoints use standard HTTP status codes:

**Success Codes**:
- `200 OK`: Success
- `202 Accepted`: Async operation started

**Error Codes**:
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Missing/invalid authentication
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

**Error Format**:
```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Card not found",
    "details": {
      "resource": "card",
      "resourceId": "123"
    }
  }
}
```

---

## User Flows

### Learning Flow (Client)

1. **Sign Up**
   - User creates account
   - System automatically initializes cards in background
   - User can start learning when ready

2. **Daily Learning Session**
   - User requests cards due for review
   - System shows cards with rendered content (front/back)
   - User reviews each card and submits quality rating
   - System updates card state with SM-2 algorithm
   - User sees next review date

3. **Progress Tracking**
   - User views statistics dashboard
   - See total cards, new cards, learning progress
   - Breakdown by card type

### Content Management Flow (Operator)

1. **Export Current Data**
   - Operator exports knowledge to CSV
   - Receives file with all current codes filled in

2. **Edit Content**
   - Operator modifies CSV file
   - Add new items (leave code empty)
   - Update existing items (keep code filled)
   - Remove items (delete row)

3. **Upload & Review**
   - Operator uploads CSV file
   - System starts import workflow
   - Operator monitors validation progress
   - Operator reviews comparison results

4. **Approve or Reject**
   - Operator reviews proposed changes
   - Approve: System applies changes and generates codes
   - Reject: System discards changes

5. **Verify Results**
   - Operator checks workflow completion status
   - Review generated codes for new items
   - Verify changes were applied correctly

---

## SM-2 Algorithm

### Overview

The SM-2 algorithm is a spaced repetition algorithm that optimizes review intervals based on user performance.

### Quality Rating Impact

**Quality < 3 (Failed)**:
- Reset repetitions to 0
- Reset interval to 1 day
- Decrease ease factor by 0.2
- Card returns to learning phase

**Quality >= 3 (Passed)**:
- Increase repetitions by 1
- Calculate new interval based on ease factor
- Adjust ease factor based on quality rating
- Card progresses in learning

### Interval Calculation

- **First review**: 1 day
- **Second review**: 6 days
- **Subsequent reviews**: Previous interval × ease factor

### Ease Factor

- **Initial value**: 2.5
- **Minimum value**: 1.3
- **Adjustment**: Based on quality rating
  - Quality 5: Increase ease factor
  - Quality 4: Slight increase
  - Quality 3: No change
  - Quality 2: Slight decrease
  - Quality 1: Moderate decrease
  - Quality 0: Large decrease

### Learning States

- **New**: Never reviewed (repetitions = 0)
- **Learning**: In progress (repetitions > 0 and < 3)
- **Review**: Established (repetitions >= 3)

---

## Code System

### Code Format

All predefined entities use immutable codes:

**Pattern**: `{PREFIX}-{NUMBER}`

**Components**:
- PREFIX: 2-letter uppercase (ST or CS)
- NUMBER: 7-digit zero-padded (0000001-9999999)
- Total length: 10 characters

**Examples**:
- Standard items: `ST-0000001`, `ST-0000002`
- Custom items: `CS-0000001`, `CS-0000002`

### Code Prefixes

- **ST-**: Standard/predefined items
- **CS-**: Custom items (reserved for future use)

### Code Rules

1. **Global Uniqueness**: Codes are unique across all entity types
2. **Immutable**: Once assigned, codes never change
3. **Sequential**: Numbers assigned sequentially within each prefix
4. **Auto-Generated**: System generates codes automatically after approval

### Code Generation

- Happens during import workflow after approval
- Uses database sequences for uniqueness
- Sequential numbering per prefix
- Atomic and thread-safe

---

## Metadata System

### Overview

Knowledge items support flexible metadata using key-value pairs.

### CSV Format

Metadata columns use `metadata:` prefix:

```csv
code,name,description,metadata:level,metadata:type,metadata:difficulty
ST-0000001,Example,Description,A1,vocabulary,easy
```

### Database Storage

Metadata stored as JSONB:

```json
{
  "level": "A1",
  "type": "vocabulary",
  "difficulty": "easy"
}
```

### Template Access

Templates can access metadata using dot notation:

```
{metadata.level}
{metadata.type}
{metadata.difficulty}
```

### Flexible Structure

- Any number of metadata fields
- No predefined schema
- Dynamic and extensible
- Efficient querying with indexes

---

## Best Practices

### For Learners

1. **Review Daily**: Check for due cards daily for best results
2. **Be Honest**: Rate quality honestly for optimal scheduling
3. **Stay Consistent**: Regular practice yields better retention
4. **Track Progress**: Monitor statistics to stay motivated

### For Operators

1. **Use Export First**: Always export before making changes
2. **Keep Codes**: Don't modify existing codes in CSV
3. **Review Carefully**: Check validation and comparison results
4. **Test Small Batches**: Start with small imports to verify format
5. **Monitor Workflows**: Check workflow status for large imports

### Content Guidelines

1. **Clear Descriptions**: Write clear, concise descriptions
2. **Consistent Metadata**: Use consistent metadata keys
3. **Logical Levels**: Organize content by difficulty levels
4. **Related Items**: Link related knowledge items appropriately
5. **Quality Templates**: Create reusable, clear templates
