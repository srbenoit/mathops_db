-- postgres_term_data_load.sql
-- (this script is designed to be run under the 'math' database owner)
-- /opt/postgresql/bin/psql -d math -U math

-- ------------------------------------------------------------------------------------------------
-- Before executing this script, ensure that all actions in 'postgres_database_create.sql' and
-- 'postgres_term_schema_create.sql' have been completed.
-- ------------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------------
-- TABLE: standards_course_grading_system
-- ------------------------------------------------------------------------------------------------

INSERT INTO term_202510.standards_course_grading_system (grading_system_id,nbr_standards,min_standards,
  max_unmastered_essential, homework_pts, on_time_mastery_pts, late_mastery_pts, a_min_score, b_min_score,
  c_min_score, d_min_score, u_min_score, min_standards_for_inc) VALUES (
  'S_24_6', 24, 18, 0, 0, 5, 4, 108, 96, 85, 72, 12, 15);

INSERT INTO term_202560.standards_course_grading_system (grading_system_id,nbr_standards,min_standards,
  max_unmastered_essential, homework_pts, on_time_mastery_pts, late_mastery_pts, a_min_score, b_min_score,
  c_min_score, d_min_score, u_min_score, min_standards_for_inc) VALUES (
  'S_24_6', 24, 18, 0, 0, 5, 4, 108, 96, 85, 72, 12, 15);

INSERT INTO term_202590.standards_course_grading_system (grading_system_id,nbr_standards,min_standards,
  max_unmastered_essential, homework_pts, on_time_mastery_pts, late_mastery_pts, a_min_score, b_min_score,
  c_min_score, d_min_score, u_min_score, min_standards_for_inc) VALUES (
  'S_24_6', 24, 18, 0, 0, 5, 4, 108, 96, 85, 72, 12, 15);

-- ------------------------------------------------------------------------------------------------
-- TABLE: standards_course_section
-- ------------------------------------------------------------------------------------------------

INSERT INTO term_202510.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 125', '003', '22202', '2025-01-21', '2025-03-16', '2025-01-21', '2025-03-14', 'HALF1', 'S_24_6',
  'FC', 'RF', '199766', 'Will Bromley', 'Chemistry', 'B 302', 34);

INSERT INTO term_202510.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 125', '004', '22203', '2025-01-21', '2025-03-16', '2025-01-21', '2025-03-14', 'HALF1', 'S_24_6',
  'FC', 'RF', '199769', 'Will Bromley', 'Clark', 'C 361', 34);

INSERT INTO term_202510.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 125', '005', '23397', '2025-03-24', '2025-05-18', '2025-03-24', '2025-05-09', 'HALF2', 'S_24_6',
  'FC', 'RF', '200606', 'Will Bromley', 'Anatomy/Zoology', 'E 112', 20);

INSERT INTO term_202510.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 126', '003', '22204', '2025-03-24', '2025-05-11', '2025-03-24', '2025-05-09', 'HALF2', 'S_24_6',
  'FC', 'RF', '199772', 'Will Bromley', 'Chemistry', 'B 302', 34);

INSERT INTO term_202560.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 117', '999', '99917', '2025-05-19', '2025-08-10', '2025-05-19', '2025-08-08', 'FULL', 'S_24_6',
  'FC', 'RO', '999999', 'Steve Benoit', 'Weber', '112', 62);

INSERT INTO term_202560.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 118', '999', '99918', '2025-05-19', '2025-08-10', '2025-05-19', '2025-08-08', 'FULL', 'S_24_6',
  'FC', 'RO', '999999', 'Steve Benoit', 'Weber', '112', 62);

INSERT INTO term_202560.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 124', '999', '99924', '2025-05-19', '2025-08-10', '2025-05-19', '2025-08-08', 'FULL', 'S_24_6',
  'FC', 'RO', '999999', 'Steve Benoit', 'Weber', '112', 62);

INSERT INTO term_202560.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 125', '999', '99925', '2025-05-19', '2025-08-10', '2025-05-19', '2025-08-08', 'FULL', 'S_24_6',
  'FC', 'RO', '999999', 'Steve Benoit', 'Weber', '112', 62);

INSERT INTO term_202560.standards_course_section (course_id, section_nbr, crn, aries_start_date, aries_end_date,
  first_class_date, last_class_date, subterm, grading_system_id, campus, delivery_mode, canvas_id, instructor,
   building_name, room_nbr, weekdays) VALUES (
  'MATH 126', '999', '99926', '2025-05-19', '2025-08-10', '2025-05-19', '2025-08-08', 'FULL', 'S_24_6',
  'FC', 'RO', '999999', 'Steve Benoit', 'Weber', '112', 62);
