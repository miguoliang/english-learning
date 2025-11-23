# Knowledge Learning System - Product Design

## Vision

An intelligent knowledge learning platform that helps users master any subject matter through scientifically-optimized spaced repetition. The system adapts to each learner's performance, presenting content at optimal intervals to maximize retention and minimize study time.

## What We Want to Achieve

### For Learners
- **Effective Learning**: Use spaced repetition science to optimize memory retention
- **Personalized Experience**: Adapt review schedules based on individual performance
- **Progress Visibility**: Clear insights into learning progress and statistics
- **Flexible Learning**: Multiple card types and learning patterns to suit different preferences

### For Content Managers
- **Efficient Management**: Batch import/export capabilities for managing large content sets
- **Quality Control**: Validation and approval workflows to ensure content accuracy
- **Scalability**: Handle thousands of knowledge items efficiently
- **Flexibility**: Support for rich metadata and relationships between content items

---

## Core Concepts

### Spaced Repetition Learning
The system uses the SM-2 algorithm to determine when each card should be reviewed. Cards are shown at optimal intervals based on:
- How well the user remembered the content (quality rating)
- How many times the card has been successfully reviewed
- The card's ease factor (how easy it is for the user)

**How it works**: Users review cards and rate their performance. The system calculates the next review date, showing cards more frequently if they're struggling and less frequently as they master content.

### Knowledge Items
The fundamental learning units - any piece of knowledge that can be learned. Examples include vocabulary words, facts, concepts, formulas, definitions, or any subject matter content. Each knowledge item:
- Has a unique identifier
- Contains name and description
- Can have flexible metadata (difficulty level, type, category, subject, etc.)
- Can relate to other knowledge items (related concepts, prerequisites, similar items)

### Card Types
Different ways to present the same knowledge. Examples:
- Term → Definition (show term, guess meaning)
- Definition → Term (show meaning, guess term)
- Question → Answer
- Fill in the blank
- Multiple choice
- Concept → Explanation

Each card type provides a different learning experience, helping users master content from multiple angles. Card types can be customized for any subject matter.

### Templates
How card content is displayed. Templates define:
- What information appears on the front of the card
- What information appears on the back
- How related knowledge is shown
- The visual presentation format

Templates are reusable and can be shared across different card types.

### Account Cards
Each user has a personal learning card for every combination of knowledge item and card type. These cards track:
- **Learning Progress**: How many times reviewed, current mastery level
- **Review Schedule**: When the card is next due, current interval
- **Performance History**: Quality ratings over time

Cards are automatically created when users sign up, giving them immediate access to all learning content.

---

## How It Works

### Learning Experience

**Daily Practice**:
1. User opens the system and sees cards due for review
2. System presents cards one at a time with front content
3. User thinks about the answer, then reveals the back
4. User rates their performance (0-5 scale)
5. System calculates next review date and updates the card
6. Process repeats for all due cards

**Quality Rating Scale**:
- 0 = Complete blackout (didn't remember at all)
- 1 = Hard (remembered with difficulty)
- 2 = Good (remembered correctly)
- 3 = Easy (remembered easily)
- 4 = Very easy (too easy)
- 5 = Perfect (knew it immediately)

**Progress Tracking**:
Users can view statistics showing:
- Total cards in their collection
- New cards (never reviewed)
- Cards in learning phase
- Cards due today
- Breakdown by card type

### Content Management

**Export & Edit**:
Operators export all knowledge content to a CSV file for editing. The exported file includes all current content with identifiers, allowing operators to:
- Add new knowledge items
- Update existing items
- Remove items
- Modify metadata

**Import & Approval**:
Operators upload modified CSV files. The system:
1. **Validates** the data format and content
2. **Compares** changes with existing content to identify what's new, updated, or removed
3. **Presents** a summary for operator review
4. **Waits** for operator approval
5. **Applies** changes only after approval, generating identifiers for new items

This workflow ensures quality control and prevents accidental data loss.

**Card Initialization**:
When users sign up, the system automatically creates learning cards for all available knowledge items and card types. This happens in the background, so users can start learning immediately once their account is ready.

---

## SM-2 Algorithm

### Purpose
The SM-2 algorithm optimizes review intervals to maximize learning efficiency. It adapts to each user's performance, showing cards more frequently when they struggle and less frequently as they master content.

### How It Works

**When User Performs Well (Quality ≥ 3)**:
- Card progresses in learning
- Review interval increases
- Next review scheduled further in the future
- Ease factor adjusts upward

**When User Struggles (Quality < 3)**:
- Card returns to learning phase
- Review interval resets to 1 day
- Ease factor decreases
- Card will be shown again soon

**Review Intervals**:
- First review: 1 day
- Second review: 6 days
- Subsequent reviews: Interval increases based on ease factor and performance

**Learning States**:
- **New**: Never reviewed
- **Learning**: In progress (reviewed 1-2 times successfully)
- **Review**: Established (reviewed 3+ times successfully)

The algorithm ensures users spend time on cards they're still learning while efficiently maintaining cards they've mastered.

---

## User Roles

### Learners (Clients)
End users who use the system to learn any subject matter. They:
- Review cards due for learning
- Rate their performance on each card
- Track their learning progress
- Browse available knowledge and card types

### Content Managers (Operators)
Internal users who manage the learning content. They:
- Export knowledge content for editing
- Import updated content via CSV
- Review and approve content changes
- Monitor system operations

---

## Key Features

### Spaced Repetition
- Scientifically-optimized review scheduling
- Adaptive intervals based on performance
- Efficient learning with minimal time investment

### Flexible Content
- Multiple card types for varied learning experiences
- Rich metadata for organization and filtering
- Relationships between knowledge items

### Progress Tracking
- Real-time statistics and progress metrics
- Historical review data
- Breakdown by card type and difficulty

### Content Management
- Batch import/export capabilities
- Validation and approval workflows
- Support for large content sets

---

## User Flows

### Learning Journey

1. **Getting Started**
   - User creates account
   - System prepares learning cards automatically
   - User can start learning immediately

2. **Daily Learning**
   - User opens the system
   - Reviews cards due today
   - Rates performance on each card
   - System schedules next reviews
   - User tracks progress

3. **Long-term Progress**
   - User monitors statistics over time
   - Sees improvement in learning metrics
   - Adjusts study habits based on progress

### Content Management Journey

1. **Preparing Changes**
   - Operator exports current content
   - Edits content in CSV file
   - Adds, updates, or removes items

2. **Importing Changes**
   - Operator uploads modified CSV
   - System validates and compares changes
   - Operator reviews proposed changes

3. **Approving Changes**
   - Operator approves or rejects changes
   - System applies approved changes
   - New items receive identifiers
   - Changes become available to learners

---

## Best Practices

### For Learners

1. **Consistency**: Review daily for best results
2. **Honesty**: Rate performance accurately for optimal scheduling
3. **Patience**: Trust the algorithm - it adapts to your pace
4. **Engagement**: Use statistics to stay motivated

### For Content Managers

1. **Export First**: Always export before making changes
2. **Review Carefully**: Check validation results before approving
3. **Test Small**: Start with small batches to verify format
4. **Monitor**: Watch workflow progress for large imports
5. **Quality**: Ensure content is clear and well-organized

### Content Guidelines

1. **Clarity**: Write clear, concise descriptions
2. **Consistency**: Use consistent metadata and organization
3. **Organization**: Group content by difficulty and type
4. **Relationships**: Link related knowledge items appropriately
5. **Templates**: Create clear, reusable card templates

---

## Product Principles

### User-Centric
Everything is designed around the learner's experience and success.

### Science-Based
Uses proven spaced repetition algorithms to optimize learning.

### Flexible
Supports multiple learning patterns and content organization methods.

### Scalable
Handles large content sets and many users efficiently.

### Quality-Focused
Validation and approval workflows ensure content accuracy.

### Transparent
Users understand their progress and how the system works.
