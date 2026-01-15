-- ============================================================================
-- ASKADEMIC DATABASE SCHEMA FOR ORACLE APEX
-- ============================================================================
-- Project: Askademic (The Student Hub)
-- Purpose: Database class final project submission
-- Contains: 5 Tables, Constraints, Views, Sample CRUD operations
-- ============================================================================

-- ============================================================================
-- PART 1: DROP EXISTING TABLES (if re-running)
-- ============================================================================
-- Run these first if you need to recreate the schema

-- DROP TABLE announcements CASCADE CONSTRAINTS;
-- DROP TABLE answers CASCADE CONSTRAINTS;
-- DROP TABLE questions CASCADE CONSTRAINTS;
-- DROP TABLE course_students CASCADE CONSTRAINTS;
-- DROP TABLE courses CASCADE CONSTRAINTS;
-- DROP TABLE users CASCADE CONSTRAINTS;

-- DROP SEQUENCE users_seq;
-- DROP SEQUENCE courses_seq;
-- DROP SEQUENCE questions_seq;
-- DROP SEQUENCE answers_seq;
-- DROP SEQUENCE announcements_seq;

-- ============================================================================
-- PART 2: CREATE SEQUENCES (Oracle uses sequences instead of auto-increment)
-- ============================================================================

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE courses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE questions_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE answers_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE announcements_seq START WITH 1 INCREMENT BY 1;

-- ============================================================================
-- PART 3: CREATE TABLES WITH CONSTRAINTS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- TABLE 1: USERS
-- Stores all users (students and professors)
-- Constraints: PK, UNIQUE email, NOT NULL, CHECK for role values
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id          NUMBER PRIMARY KEY,
    firstname   VARCHAR2(100) NOT NULL,
    lastname    VARCHAR2(100) NOT NULL,
    email       VARCHAR2(255) NOT NULL UNIQUE,
    password    VARCHAR2(255) NOT NULL,
    role        VARCHAR2(20) NOT NULL,
    
    -- CHECK constraint: role must be either STUDENT or PROFESSOR
    CONSTRAINT chk_user_role CHECK (role IN ('STUDENT', 'PROFESSOR'))
);

-- -----------------------------------------------------------------------------
-- TABLE 2: COURSES
-- Stores course information
-- Constraints: PK, FK to users, UNIQUE course_code, NOT NULL
-- -----------------------------------------------------------------------------
CREATE TABLE courses (
    id              NUMBER PRIMARY KEY,
    name            VARCHAR2(200) NOT NULL,
    course_code     VARCHAR2(8) NOT NULL UNIQUE,
    description     VARCHAR2(1000),
    grading_info    VARCHAR2(2000),
    professor_id    NUMBER NOT NULL,
    
    -- FOREIGN KEY: professor must exist in users table
    CONSTRAINT fk_course_professor FOREIGN KEY (professor_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- TABLE 3: COURSE_STUDENTS (Junction Table for Many-to-Many)
-- Links students to courses they are enrolled in
-- Constraints: Composite PK, FKs to users and courses
-- -----------------------------------------------------------------------------
CREATE TABLE course_students (
    course_id   NUMBER NOT NULL,
    student_id  NUMBER NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Composite primary key
    CONSTRAINT pk_course_students PRIMARY KEY (course_id, student_id),
    
    -- Foreign keys
    CONSTRAINT fk_cs_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_cs_student FOREIGN KEY (student_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- TABLE 4: QUESTIONS
-- Stores questions asked in courses
-- Constraints: PK, FKs to users and courses, NOT NULL, DEFAULT values
-- -----------------------------------------------------------------------------
CREATE TABLE questions (
    id          NUMBER PRIMARY KEY,
    title       VARCHAR2(500) NOT NULL,
    content     CLOB NOT NULL,
    anonymous   NUMBER(1) DEFAULT 0 NOT NULL,  -- 0=false, 1=true
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    author_id   NUMBER NOT NULL,
    course_id   NUMBER NOT NULL,
    
    -- CHECK constraint: anonymous must be 0 or 1
    CONSTRAINT chk_question_anonymous CHECK (anonymous IN (0, 1)),
    
    -- Foreign keys
    CONSTRAINT fk_question_author FOREIGN KEY (author_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_question_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- TABLE 5: ANSWERS
-- Stores answers to questions
-- Constraints: PK, FKs to users and questions, NOT NULL, DEFAULT values
-- -----------------------------------------------------------------------------
CREATE TABLE answers (
    id          NUMBER PRIMARY KEY,
    content     CLOB NOT NULL,
    verified    NUMBER(1) DEFAULT 0 NOT NULL,  -- 0=false, 1=true
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    author_id   NUMBER NOT NULL,
    question_id NUMBER NOT NULL,
    
    -- CHECK constraint: verified must be 0 or 1
    CONSTRAINT chk_answer_verified CHECK (verified IN (0, 1)),
    
    -- Foreign keys
    CONSTRAINT fk_answer_author FOREIGN KEY (author_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) 
        REFERENCES questions(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- TABLE 6: ANNOUNCEMENTS
-- Stores course announcements by professors
-- Constraints: PK, FKs to users and courses, NOT NULL
-- -----------------------------------------------------------------------------
CREATE TABLE announcements (
    id          NUMBER PRIMARY KEY,
    title       VARCHAR2(500) NOT NULL,
    content     CLOB NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    author_id   NUMBER NOT NULL,
    course_id   NUMBER NOT NULL,
    
    -- Foreign keys
    CONSTRAINT fk_announcement_author FOREIGN KEY (author_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_announcement_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE
);

-- ============================================================================
-- PART 4: CREATE VIEWS
-- ============================================================================

-- -----------------------------------------------------------------------------
-- VIEW 1: UNANSWERED_QUESTIONS
-- Shows all questions that have no answers yet
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW unanswered_questions AS
SELECT 
    q.id AS question_id,
    q.title,
    q.content,
    q.created_at,
    CASE WHEN q.anonymous = 1 THEN 'Anonymous' 
         ELSE u.firstname || ' ' || u.lastname END AS author_name,
    c.name AS course_name,
    c.course_code
FROM questions q
JOIN users u ON q.author_id = u.id
JOIN courses c ON q.course_id = c.id
WHERE q.id NOT IN (SELECT DISTINCT question_id FROM answers)
ORDER BY q.created_at DESC;

-- -----------------------------------------------------------------------------
-- VIEW 2: QUESTION_STATISTICS
-- Shows statistics per course: total questions, answered, unanswered
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW question_statistics AS
SELECT 
    c.id AS course_id,
    c.name AS course_name,
    c.course_code,
    COUNT(q.id) AS total_questions,
    COUNT(CASE WHEN a.id IS NOT NULL THEN 1 END) AS answered_questions,
    COUNT(CASE WHEN a.id IS NULL THEN 1 END) AS unanswered_questions,
    COUNT(CASE WHEN a.verified = 1 THEN 1 END) AS verified_answers
FROM courses c
LEFT JOIN questions q ON c.id = q.course_id
LEFT JOIN answers a ON q.id = a.question_id
GROUP BY c.id, c.name, c.course_code
ORDER BY c.name;

-- -----------------------------------------------------------------------------
-- VIEW 3: COURSE_ENROLLMENT_SUMMARY
-- Shows enrollment count per course
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW course_enrollment_summary AS
SELECT 
    c.id AS course_id,
    c.name AS course_name,
    c.course_code,
    p.firstname || ' ' || p.lastname AS professor_name,
    COUNT(cs.student_id) AS enrolled_students
FROM courses c
JOIN users p ON c.professor_id = p.id
LEFT JOIN course_students cs ON c.id = cs.course_id
GROUP BY c.id, c.name, c.course_code, p.firstname, p.lastname
ORDER BY enrolled_students DESC;

-- ============================================================================
-- PART 5: INSERT SAMPLE DATA
-- ============================================================================

-- Insert Users (2 professors, 3 students)
INSERT INTO users (id, firstname, lastname, email, password, role)
VALUES (users_seq.NEXTVAL, 'John', 'Smith', 'prof.smith@university.edu', 'hashed_password_1', 'PROFESSOR');

INSERT INTO users (id, firstname, lastname, email, password, role)
VALUES (users_seq.NEXTVAL, 'Maria', 'Garcia', 'prof.garcia@university.edu', 'hashed_password_2', 'PROFESSOR');

INSERT INTO users (id, firstname, lastname, email, password, role)
VALUES (users_seq.NEXTVAL, 'Alice', 'Johnson', 'alice.j@student.edu', 'hashed_password_3', 'STUDENT');

INSERT INTO users (id, firstname, lastname, email, password, role)
VALUES (users_seq.NEXTVAL, 'Bob', 'Williams', 'bob.w@student.edu', 'hashed_password_4', 'STUDENT');

INSERT INTO users (id, firstname, lastname, email, password, role)
VALUES (users_seq.NEXTVAL, 'Carol', 'Davis', 'carol.d@student.edu', 'hashed_password_5', 'STUDENT');

-- Insert Courses
INSERT INTO courses (id, name, course_code, description, professor_id)
VALUES (courses_seq.NEXTVAL, 'Introduction to Databases', 'CS101', 
        'Learn SQL, normalization, and database design principles.', 1);

INSERT INTO courses (id, name, course_code, description, professor_id)
VALUES (courses_seq.NEXTVAL, 'Advanced Programming', 'CS201', 
        'Object-oriented programming with Java and design patterns.', 1);

INSERT INTO courses (id, name, course_code, description, professor_id)
VALUES (courses_seq.NEXTVAL, 'Web Development', 'CS301', 
        'Full-stack development with React and Spring Boot.', 2);

-- Enroll Students in Courses
INSERT INTO course_students (course_id, student_id) VALUES (1, 3);
INSERT INTO course_students (course_id, student_id) VALUES (1, 4);
INSERT INTO course_students (course_id, student_id) VALUES (1, 5);
INSERT INTO course_students (course_id, student_id) VALUES (2, 3);
INSERT INTO course_students (course_id, student_id) VALUES (2, 4);
INSERT INTO course_students (course_id, student_id) VALUES (3, 5);

-- Insert Questions
INSERT INTO questions (id, title, content, anonymous, author_id, course_id)
VALUES (questions_seq.NEXTVAL, 'What is normalization?', 
        'Can someone explain the difference between 1NF, 2NF, and 3NF?', 0, 3, 1);

INSERT INTO questions (id, title, content, anonymous, author_id, course_id)
VALUES (questions_seq.NEXTVAL, 'How to create a foreign key?', 
        'I am trying to link two tables but getting an error.', 0, 4, 1);

INSERT INTO questions (id, title, content, anonymous, author_id, course_id)
VALUES (questions_seq.NEXTVAL, 'Inheritance in Java', 
        'How does class inheritance work with interfaces?', 1, 3, 2);

-- Insert Answers
INSERT INTO answers (id, content, verified, author_id, question_id)
VALUES (answers_seq.NEXTVAL, 
        'Normalization is the process of organizing data to reduce redundancy. 1NF removes repeating groups, 2NF removes partial dependencies, 3NF removes transitive dependencies.', 
        1, 1, 1);

INSERT INTO answers (id, content, verified, author_id, question_id)
VALUES (answers_seq.NEXTVAL, 
        'Use FOREIGN KEY constraint with REFERENCES keyword. Make sure the referenced column exists and has the same data type.', 
        1, 1, 2);

-- Insert Announcements
INSERT INTO announcements (id, title, content, author_id, course_id)
VALUES (announcements_seq.NEXTVAL, 'Midterm Exam Date', 
        'The midterm exam will be held on December 15th at 10:00 AM.', 1, 1);

INSERT INTO announcements (id, title, content, author_id, course_id)
VALUES (announcements_seq.NEXTVAL, 'Project Deadline Extended', 
        'Due to popular request, the project deadline has been extended to December 20th.', 2, 3);

COMMIT;

-- ============================================================================
-- PART 6: UPDATE EXAMPLES
-- ============================================================================

-- Update a user's name
UPDATE users 
SET firstname = 'Jonathan', lastname = 'Smith Jr.' 
WHERE id = 1;

-- Verify an answer
UPDATE answers 
SET verified = 1 
WHERE id = 2;

-- Update course description
UPDATE courses 
SET description = 'Comprehensive SQL and database design course with hands-on projects.' 
WHERE course_code = 'CS101';

COMMIT;

-- ============================================================================
-- PART 7: DELETE EXAMPLES
-- ============================================================================

-- Delete an announcement (soft scenario - not actually running this)
-- DELETE FROM announcements WHERE id = 2;

-- Unenroll a student from a course
-- DELETE FROM course_students WHERE course_id = 2 AND student_id = 4;

-- Delete a question (will cascade delete its answers)
-- DELETE FROM questions WHERE id = 3;

-- COMMIT;

-- ============================================================================
-- PART 8: SELECT QUERIES (to verify data)
-- ============================================================================

-- Show all users
SELECT * FROM users;

-- Show all courses with professor names
SELECT c.*, u.firstname || ' ' || u.lastname AS professor_name
FROM courses c
JOIN users u ON c.professor_id = u.id;

-- Show unanswered questions (using the view)
SELECT * FROM unanswered_questions;

-- Show course statistics (using the view)
SELECT * FROM question_statistics;

-- Show enrollment summary (using the view)
SELECT * FROM course_enrollment_summary;

-- ============================================================================
-- END OF SCRIPT
-- ============================================================================
