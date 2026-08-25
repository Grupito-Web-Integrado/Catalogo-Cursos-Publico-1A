-- =========================================================
-- COURSE CATALOG
-- PostgreSQL
-- =========================================================

-- =========================================================
-- EXTENSIONS
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";


-- =========================================================
-- COURSE CATEGORIES
-- =========================================================

CREATE TABLE course_categories (

    id UUID PRIMARY KEY
        DEFAULT gen_random_uuid(),

    name VARCHAR(150) NOT NULL,

    description TEXT,

    status VARCHAR(30) NOT NULL
        DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL
        DEFAULT 0,

    deleted BOOLEAN NOT NULL
        DEFAULT FALSE,

    CONSTRAINT uk_course_categories_name
        UNIQUE (name),

    CONSTRAINT ck_course_categories_status
        CHECK (
            status IN (
                'ACTIVE',
                'INACTIVE'
            )
        )
);


-- =========================================================
-- COURSES
-- =========================================================

CREATE TABLE courses (

    id UUID PRIMARY KEY
        DEFAULT gen_random_uuid(),

    category_id UUID NOT NULL,

    code VARCHAR(50) NOT NULL,

    name VARCHAR(200) NOT NULL,

    description TEXT,

    modality VARCHAR(30) NOT NULL,

    price NUMERIC(12, 2) NOT NULL
        DEFAULT 0,

    currency VARCHAR(3) NOT NULL
        DEFAULT 'PEN',

    start_date DATE,

    end_date DATE,

    start_time TIME,

    duration_hours NUMERIC(5, 2),

    capacity INTEGER NOT NULL
        DEFAULT 0,

    available_slots INTEGER NOT NULL
        DEFAULT 0,

    status VARCHAR(30) NOT NULL
        DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL
        DEFAULT 0,

    deleted BOOLEAN NOT NULL
        DEFAULT FALSE,

    CONSTRAINT uk_courses_code
        UNIQUE (code),

    CONSTRAINT fk_courses_category
        FOREIGN KEY (category_id)
        REFERENCES course_categories(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT ck_courses_modality
        CHECK (
            modality IN (
                'ONLINE',
                'PRESENTIAL',
                'HYBRID'
            )
        ),

    CONSTRAINT ck_courses_price
        CHECK (
            price >= 0
        ),

    CONSTRAINT ck_courses_capacity
        CHECK (
            capacity >= 0
        ),

    CONSTRAINT ck_courses_available_slots
        CHECK (
            available_slots >= 0
            AND available_slots <= capacity
        ),

    CONSTRAINT ck_courses_duration
        CHECK (
            duration_hours IS NULL
            OR duration_hours > 0
        ),

    CONSTRAINT ck_courses_dates
        CHECK (
            end_date IS NULL
            OR start_date IS NULL
            OR end_date >= start_date
        ),

    CONSTRAINT ck_courses_status
        CHECK (
            status IN (
                'DRAFT',
                'PUBLISHED',
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED',
                'INACTIVE'
            )
        )
);


-- =========================================================
-- COURSE SCHEDULES
-- =========================================================

CREATE TABLE course_schedules (

    id UUID PRIMARY KEY
        DEFAULT gen_random_uuid(),

    course_id UUID NOT NULL,

    day_of_week VARCHAR(15) NOT NULL,

    start_time TIME NOT NULL,

    end_time TIME NOT NULL,

    room VARCHAR(100),

    created_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL
        DEFAULT 0,

    deleted BOOLEAN NOT NULL
        DEFAULT FALSE,

    CONSTRAINT fk_course_schedules_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT ck_course_schedules_day
        CHECK (
            day_of_week IN (
                'MONDAY',
                'TUESDAY',
                'WEDNESDAY',
                'THURSDAY',
                'FRIDAY',
                'SATURDAY',
                'SUNDAY'
            )
        ),

    CONSTRAINT ck_course_schedules_time
        CHECK (
            end_time > start_time
        )
);


-- =========================================================
-- COURSE LOCATIONS
-- =========================================================

CREATE TABLE course_locations (

    id UUID PRIMARY KEY
        DEFAULT gen_random_uuid(),

    course_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    address VARCHAR(300),

    city VARCHAR(100),

    reference VARCHAR(300),

    capacity INTEGER,

    created_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    version BIGINT NOT NULL
        DEFAULT 0,

    deleted BOOLEAN NOT NULL
        DEFAULT FALSE,

    CONSTRAINT fk_course_locations_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT uk_course_locations_course
        UNIQUE (course_id),

    CONSTRAINT ck_course_locations_capacity
        CHECK (
            capacity IS NULL
            OR capacity >= 0
        )
);


-- =========================================================
-- INDEXES
-- =========================================================

-- Categories
CREATE INDEX idx_course_categories_status
    ON course_categories(status);

CREATE INDEX idx_course_categories_deleted
    ON course_categories(deleted);


-- Courses
CREATE INDEX idx_courses_category_id
    ON courses(category_id);

CREATE INDEX idx_courses_status
    ON courses(status);

CREATE INDEX idx_courses_modality
    ON courses(modality);

CREATE INDEX idx_courses_start_date
    ON courses(start_date);

CREATE INDEX idx_courses_end_date
    ON courses(end_date);

CREATE INDEX idx_courses_deleted
    ON courses(deleted);


-- Course schedules
CREATE INDEX idx_course_schedules_course_id
    ON course_schedules(course_id);

CREATE INDEX idx_course_schedules_day
    ON course_schedules(day_of_week);

CREATE INDEX idx_course_schedules_start_time
    ON course_schedules(start_time);

CREATE INDEX idx_course_schedules_deleted
    ON course_schedules(deleted);


-- Course locations
CREATE INDEX idx_course_locations_city
    ON course_locations(city);

CREATE INDEX idx_course_locations_deleted
    ON course_locations(deleted);


-- =========================================================
-- COMMENTS
-- =========================================================

COMMENT ON TABLE courses
    IS 'Cursos del catálogo académico';

COMMENT ON TABLE course_categories
    IS 'Categorías utilizadas para clasificar los cursos';

COMMENT ON TABLE course_schedules
    IS 'Horarios asociados a los cursos';

COMMENT ON TABLE course_locations
    IS 'Ubicación física asociada a un curso';
