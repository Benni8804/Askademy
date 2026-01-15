# Database Normalization Analysis
## Askademic (The Student Hub) - Database Design

**Project:** Askademic - Academic Q&A Platform  
**Date:** December 2024

---

## 1. Database Schema Overview

The Askademic database consists of **6 relations** (tables):

| Table | Purpose | Primary Key |
|-------|---------|-------------|
| `users` | Stores user accounts (students, professors) | id |
| `courses` | Stores course information | id |
| `course_students` | Junction table for student enrollment | (course_id, student_id) |
| `questions` | Stores questions asked in courses | id |
| `answers` | Stores answers to questions | id |
| `announcements` | Stores course announcements | id |

---

## 2. Initial Normal Form Analysis

### 2.1 First Normal Form (1NF) ✓

**Definition:** A relation is in 1NF if all attributes contain only atomic (indivisible) values and there are no repeating groups.

**Our schema satisfies 1NF because:**

- ✓ All columns contain single, atomic values
- ✓ No multi-valued attributes (e.g., no comma-separated lists)
- ✓ No repeating groups (e.g., no `course1`, `course2`, `course3` columns)
- ✓ Each row is unique (enforced by PRIMARY KEY)

**Example:** Instead of storing enrolled courses as a list in the `users` table, we use a separate `course_students` junction table.

### 2.2 Second Normal Form (2NF) ✓

**Definition:** A relation is in 2NF if it is in 1NF and every non-key attribute is fully functionally dependent on the entire primary key (no partial dependencies).

**Our schema satisfies 2NF because:**

- ✓ All tables with single-column primary keys automatically satisfy 2NF
- ✓ The `course_students` table has a composite key (course_id, student_id) with only one additional attribute (`enrolled_at`) that depends on the entire key

**Example:** In `course_students`, the `enrolled_at` timestamp depends on BOTH the course AND the student (when did THIS student enroll in THIS course), not just one of them.

### 2.3 Third Normal Form (3NF) ✓

**Definition:** A relation is in 3NF if it is in 2NF and no non-key attribute transitively depends on the primary key.

**Our schema satisfies 3NF because:**

- ✓ No transitive dependencies exist
- ✓ All non-key attributes depend directly on the primary key

**Analysis of each table:**

| Table | Non-Key Attributes | Dependencies | 3NF? |
|-------|-------------------|--------------|------|
| `users` | firstname, lastname, email, password, role | All depend directly on `id` | ✓ |
| `courses` | name, course_code, description, professor_id | All depend directly on `id` | ✓ |
| `questions` | title, content, anonymous, created_at, author_id, course_id | All depend directly on `id` | ✓ |
| `answers` | content, verified, created_at, author_id, question_id | All depend directly on `id` | ✓ |
| `announcements` | title, content, created_at, author_id, course_id | All depend directly on `id` | ✓ |

---

## 3. Potential Denormalization Issues (Avoided)

### 3.1 What We Avoided

**Bad Design (Not Normalized):**
```sql
-- WRONG: Storing professor name directly in courses table
CREATE TABLE courses_bad (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(200),
    professor_name VARCHAR2(200),  -- Redundant! Creates update anomaly
    professor_email VARCHAR2(255)  -- Redundant! Creates update anomaly
);
```

**Problem:** If Professor Smith changes their name, we'd need to update EVERY course they teach.

**Our Design (Normalized):**
```sql
-- CORRECT: Reference professor by ID
CREATE TABLE courses (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(200),
    professor_id NUMBER REFERENCES users(id)  -- Single source of truth
);
```

**Benefit:** Professor's name is stored once in `users` table. Updates propagate automatically via JOINs.

### 3.2 Junction Table for Many-to-Many

**Bad Design:**
```sql
-- WRONG: Storing enrolled students as comma-separated list
CREATE TABLE courses_bad (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(200),
    enrolled_students VARCHAR2(4000)  -- "1,3,5,7" - Violates 1NF!
);
```

**Our Design:**
```sql
-- CORRECT: Separate junction table
CREATE TABLE course_students (
    course_id NUMBER,
    student_id NUMBER,
    PRIMARY KEY (course_id, student_id)
);
```

---

## 4. Functional Dependencies

### 4.1 Users Table
```
id → firstname, lastname, email, password, role
email → id (email is UNIQUE, so this is also a candidate key)
```

### 4.2 Courses Table
```
id → name, course_code, description, grading_info, professor_id
course_code → id (course_code is UNIQUE, alternative key)
```

### 4.3 Questions Table
```
id → title, content, anonymous, created_at, author_id, course_id
```

### 4.4 Answers Table
```
id → content, verified, created_at, author_id, question_id
```

### 4.5 Course_Students Table
```
(course_id, student_id) → enrolled_at
```

---

## 5. Conclusion

The Askademic database schema is in **Third Normal Form (3NF)** because:

1. **1NF:** All attributes are atomic, no repeating groups
2. **2NF:** No partial dependencies on composite keys
3. **3NF:** No transitive dependencies

**Benefits of this normalization:**
- ✓ **No data redundancy** - Information stored in one place only
- ✓ **No update anomalies** - Changes in one place propagate correctly
- ✓ **No insertion anomalies** - Can add courses without questions
- ✓ **No deletion anomalies** - Deleting a question doesn't lose user data

The schema was designed with normalization principles from the start, eliminating the need for restructuring.

---

## 6. Entity-Relationship Summary

```
USERS ──────────────┬──────────────── COURSES
  │                 │ (professor)         │
  │                 │                     │
  │    ┌────────────┴──────────────┐     │
  │    │     COURSE_STUDENTS       │     │
  │    │     (enrollment)          │     │
  │    └───────────────────────────┘     │
  │                                       │
  ├───────────── QUESTIONS ──────────────┤
  │                  │                    │
  │                  │                    │
  └───────────── ANSWERS                  │
                                          │
         ANNOUNCEMENTS ───────────────────┘
```

**Cardinalities:**
- Users (1) → (M) Courses (as professor)
- Users (M) ↔ (M) Courses (as students, via course_students)
- Courses (1) → (M) Questions
- Questions (1) → (M) Answers
- Courses (1) → (M) Announcements
