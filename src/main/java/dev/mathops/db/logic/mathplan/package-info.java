/**
 * Classes to represent and gather data for use by the Math Plan.
 *
 * <p>
 * The "stmathplan" table stores student responses to the Math Plan.  Each visit to the site can generate new responses,
 * so the exam_dt, finish_time, and session ID stored in those table rows can identify the most recent responses for a
 * student.
 *
 * <pre>
 * stu_id               char(9)                                 no
 * pidm                 integer                                 no
 * apln_term            char(4)                                 yes
 * version              char(5)                                 no
 *     WLCM1: The profile ID used to store the "majors of interest" response.
 *     WLCM2: The profile ID used to store the short representation of recommendations.
 *     WLCM3: The profile ID used to store the "this is only a recommendation" acknowledgement.
 *     WLCM4: The profile ID used to record that the student has looked at their "existing work".
 *     WLCM5: The profile ID used to store "intentions" responses.
 *     WLCM6: The profile ID used to record when student accesses review materials.
 *     WLCM7: The profile ID used to record when student checks their placement results.
 * exam_dt              date                                    no
 * survey_nbr           smallint                                no
 *     For WLCM1, this is a code (1000 or greater) matching a selected major - one row per selected major
 *     For WLCM2, this is a code (1, 2, 3, or 4)
 *     For WLCM3, this is "1"
 *     For WLCM4, this is "1"
 *     For WLCM5, this is a code (1 or 2)
 *     For WLCM6, this is "1"
 *     For WLCM7, this is "1"
 * stu_answer           char(50)                                yes
 *     For WLCM1, this is "Y"
 *     For WLCM2, this is "(none)" or a list of courses like "117*, 118, 124"
 *     For WLCM3, this is "Y"
 *     For WLCM4, this is "Y"
 *     For WLCM5, this is "Y" or "N"
 *                "1 = Y" means the math plan is completed and placement is not needed
 *                "2 = Y" means the math plan is completed and placement is needed
 *     For WLCM6, this is "Y"
 *     For WLCM7, this is "Y"
 * finish_time          integer                                 no
 * session              bigint                                  yes
 * </pre>
 */
package dev.mathops.db.logic.mathplan;
