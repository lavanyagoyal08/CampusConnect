-- ============================================================================
-- CampusConnect — Smart Campus Activities Management System
-- Database Schema & Demonstration Seed Data
-- Tagline: "Your campus. Your people. Your possibilities."
-- Technology: MySQL 8.0+ / Java Swing JDBC
-- ============================================================================

CREATE DATABASE IF NOT EXISTS campusconnect
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE campusconnect;

-- Disable foreign key checks during schema creation/reset
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS saved_events;
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS clubs;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS admins;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- TABLE 1: admins
-- Admin profiles, email, PBKDF2 password hash, salt, parameters, and roles.
-- ============================================================================
CREATE TABLE admins (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    salt            VARCHAR(255) NOT NULL,
    hash_iterations INT NOT NULL DEFAULT 100000,
    admin_level     VARCHAR(50) NOT NULL DEFAULT 'SUPER',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_email (email)
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE 2: students
-- Student profiles, email, PBKDF2 hash, salt, parameters, department, year,
-- delimited interests field (for Opportunity Match), and timestamps.
-- ============================================================================
CREATE TABLE students (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    salt            VARCHAR(255) NOT NULL,
    hash_iterations INT NOT NULL DEFAULT 100000,
    department      VARCHAR(100) NOT NULL,
    year_of_study   INT NOT NULL,
    interests       TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_email (email),
    INDEX idx_student_dept (department)
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE 3: clubs
-- Campus clubs, descriptions, categories, contact emails, and active state.
-- Categories: Technology, Cultural, Sports, Entrepreneurship, Design, Social Impact
-- ============================================================================
CREATE TABLE clubs (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    category        VARCHAR(100) NOT NULL,
    description     TEXT,
    contact_email   VARCHAR(100),
    contact_info    VARCHAR(150),
    active          TINYINT(1) NOT NULL DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_club_category (category),
    INDEX idx_club_name (name)
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE 4: events
-- Campus events, workshops, hackathons, competitions, volunteering, etc.
-- Capacity tracking, deadlines, eligibility, opportunity type, and statuses.
-- ============================================================================
CREATE TABLE events (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    category          VARCHAR(100) NOT NULL,
    opportunity_type  VARCHAR(100) NOT NULL,
    event_date        DATE NOT NULL,
    venue             VARCHAR(150) NOT NULL,
    organizer         VARCHAR(150) NOT NULL,
    reg_deadline      DATETIME,
    eligibility       VARCHAR(150) DEFAULT 'All Students',
    capacity          INT NOT NULL DEFAULT 50,
    available_seats   INT NOT NULL DEFAULT 50,
    registration_open TINYINT(1) NOT NULL DEFAULT 1,
    active            TINYINT(1) NOT NULL DEFAULT 1,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_date (event_date),
    INDEX idx_event_category (category),
    INDEX idx_event_type (opportunity_type)
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE 5: registrations
-- Event registration records, participation statuses, and admin attendance verification.
-- Only admin-verified attendance (ATTENDED) awards Campus Passport stamps.
-- ============================================================================
CREATE TABLE registrations (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    student_id        INT NOT NULL,
    event_id          INT NOT NULL,
    reg_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status            VARCHAR(50) NOT NULL DEFAULT 'REGISTERED',   -- REGISTERED | CANCELLED
    attendance_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',      -- PENDING | ATTENDED | NOT_ATTENDED
    verified_at       TIMESTAMP NULL,
    verified_by       VARCHAR(100) NULL,
    UNIQUE KEY uk_student_event (student_id, event_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_reg_student (student_id),
    INDEX idx_reg_event (event_id),
    INDEX idx_reg_attendance (attendance_status)
) ENGINE=InnoDB;

-- ============================================================================
-- TABLE 6: saved_events
-- Bookmarked / saved events per student for quick reference.
-- ============================================================================
CREATE TABLE saved_events (
    student_id        INT NOT NULL,
    event_id          INT NOT NULL,
    saved_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, event_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- DEMONSTRATION SEED DATA
-- Passwords hashed using PBKDF2WithHmacSHA256 (100,000 iterations, 256-bit)
-- Default Admin: admin@campusconnect.edu / admin123
-- Sample Student: alex@campusconnect.edu / student123
-- ============================================================================

-- 1. Default Administrator
INSERT INTO admins (name, email, password_hash, salt, hash_iterations, admin_level) VALUES
('Campus Administrator', 'admin@campusconnect.edu', '2Yj7wIHQX2z/GHuGmSyk4URI7Wy/tkxIBBp7PD2Bv80=', '3VGy2xGX+PUTO8rfA/34nQ==', 100000, 'SUPER');

-- 2. Demonstration Students
INSERT INTO students (name, email, password_hash, salt, hash_iterations, department, year_of_study, interests) VALUES
('Alex Rivera', 'alex@campusconnect.edu', '+ndR/zg398lxATwTGtjMcijOygybQz0gAZkzUHpQysM=', 'P3wi/fdtedFEBEVF6/KIjQ==', 100000, 'Computer Science', 3, 'Programming,Artificial Intelligence,Design'),
('Priya Sharma', 'priya@campusconnect.edu', '+ndR/zg398lxATwTGtjMcijOygybQz0gAZkzUHpQysM=', 'P3wi/fdtedFEBEVF6/KIjQ==', 100000, 'Electronics & Comm', 2, 'Robotics,Volunteering,Social Impact'),
('Marcus Chen', 'marcus@campusconnect.edu', '+ndR/zg398lxATwTGtjMcijOygybQz0gAZkzUHpQysM=', 'P3wi/fdtedFEBEVF6/KIjQ==', 100000, 'Mechanical Engineering', 4, 'Entrepreneurship,Public Speaking,Sports');

-- 3. Demonstration Clubs
INSERT INTO clubs (name, category, description, contact_email, contact_info, active) VALUES
('ACM Student Chapter', 'Technology', 'Premier computing society focusing on competitive programming, open source projects, and technical hackathons.', 'acm@campusconnect.edu', 'CS Building Lab 3', 1),
('AI & Robotics Innovators', 'Technology', 'Hands-on robotics, deep learning research, computer vision workshops, and national drone competitions.', 'robotics@campusconnect.edu', 'Mechatronics Wing Rm 102', 1),
('Google Developer Student Club', 'Technology', 'University-based community group for students interested in Google developer technologies, mobile apps, and cloud.', 'gdsc@campusconnect.edu', 'Innovation Hub 204', 1),
('Kala Cultural Arts Collective', 'Cultural', 'Fostering traditional music, theatre, classical dance, and organizing the annual campus cultural festival.', 'cultural@campusconnect.edu', 'Student Center Amphitheatre', 1),
('Campus Red Cross & Seva Guild', 'Social Impact', 'Community outreach, blood donation drives, flood relief volunteering, and teaching underprivileged kids.', 'seva@campusconnect.edu', 'Health & Wellness Office', 1),
('E-Cell Entrepreneurship Cell', 'Entrepreneurship', 'Empowering aspiring campus founders with angel investor bootcamps, startup pitch competitions, and mentorship.', 'ecell@campusconnect.edu', 'Incubation Center Suite A', 1),
('Design & Creative UX Society', 'Design', 'UI/UX design, generative art, Figma masterclasses, brand identity consulting, and digital illustration jams.', 'design@campusconnect.edu', 'Media Studio 3', 1),
('Varsity Sports Association', 'Sports', 'Organizing inter-college tournaments in football, basketball, cricket, badminton, and annual athletics meet.', 'sports@campusconnect.edu', 'Campus Sports Complex', 1);

-- 4. Demonstration Events & Opportunities
INSERT INTO events (title, description, category, opportunity_type, event_date, venue, organizer, reg_deadline, eligibility, capacity, available_seats, registration_open, active) VALUES
('Campus HackAI 2026', 'A 36-hour flagship campus hackathon building real-world AI applications, agentic tools, and smart campus assistants. Mentorship from industry engineers.', 'Technology', 'Hackathon', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'Main Auditorium & Innovation Lab', 'ACM Student Chapter', DATE_ADD(NOW(), INTERVAL 5 DAY), 'All Engineering & BCA Students', 100, 98, 1, 1),

('Figma to Flutter UI Bootcamp', 'Hands-on interactive masterclass covering design systems, component auto-layout in Figma, and translating prototypes into Flutter mobile apps.', 'Design', 'Workshop', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'Computer Center Lab 2', 'Design & Creative UX Society', DATE_ADD(NOW(), INTERVAL 8 DAY), 'Open to all years', 40, 39, 1, 1),

('Campus PitchFest & Startup Showcase', 'Pitch your startup idea to venture capitalists, angel investors, and alumni founders. Seed funding grants up to Rs. 1,00,000 for top 3 teams.', 'Entrepreneurship', 'Competition', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'Incubation Hall Alpha', 'E-Cell', DATE_ADD(NOW(), INTERVAL 12 DAY), 'Open to all enrolled students', 30, 30, 1, 1),

('Green Campus Tree Plantation & Cleanup', 'Weekend community service drive planting 200 saplings across south campus and organizing zero-waste segregation workshops.', 'Social Impact', 'Volunteering', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'South Campus Grounds', 'Campus Red Cross & Seva Guild', DATE_ADD(NOW(), INTERVAL 2 DAY), 'All campus members', 60, 59, 1, 1),

('Aura 2026: Annual Inter-College Battle of the Bands', 'Live acoustic and rock music competition featuring 15 college bands from across the state. Food stalls, lights, and voting.', 'Cultural', 'Cultural', DATE_ADD(CURDATE(), INTERVAL 18 DAY), 'Open Air Theatre', 'Kala Cultural Arts Collective', DATE_ADD(NOW(), INTERVAL 15 DAY), 'All Students & Faculty', 250, 250, 1, 1),

('Competitive Programming Intensive with Java', 'Master graph algorithms, dynamic programming, and complexity optimization for technical product company interviews.', 'Technology', 'Skill Development', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'Online / CS Seminar Hall', 'ACM Student Chapter', DATE_ADD(NOW(), INTERVAL 4 DAY), 'Year 2, 3, 4 Students', 80, 80, 1, 1),

('Full-Stack Web Development Crash Course', 'Learn modern REST APIs, database persistence, and reactive frontends from industry experts.', 'Technology', 'Workshop', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Tech Seminar Hall 1', 'GDSC', DATE_SUB(NOW(), INTERVAL 12 DAY), 'All Years', 50, 48, 0, 1),

('Clean Water Community Outreach', 'Field volunteering program delivering water filtration units and testing tap water quality in neighboring rural schools.', 'Social Impact', 'Volunteering', DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'Suburban Community Center', 'Campus Red Cross & Seva Guild', DATE_SUB(NOW(), INTERVAL 22 DAY), 'All Years', 40, 38, 0, 1);

-- 5. Seed Registrations & Verified Attendance (giving Alex verified stamps for Campus Passport)
-- Event 7 attended and verified by Admin -> Earns "First Event" & "Tech Explorer" stamp
INSERT INTO registrations (student_id, event_id, status, attendance_status, verified_at, verified_by) VALUES
(1, 7, 'REGISTERED', 'ATTENDED', DATE_SUB(NOW(), INTERVAL 9 DAY), 'Campus Administrator'),
(1, 8, 'REGISTERED', 'ATTENDED', DATE_SUB(NOW(), INTERVAL 19 DAY), 'Campus Administrator'),
(1, 1, 'REGISTERED', 'PENDING', NULL, NULL),
(1, 2, 'REGISTERED', 'PENDING', NULL, NULL),
(1, 4, 'REGISTERED', 'PENDING', NULL, NULL);

-- 6. Saved Events
INSERT INTO saved_events (student_id, event_id) VALUES
(1, 1),
(1, 3);
