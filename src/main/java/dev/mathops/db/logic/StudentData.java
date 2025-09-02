package dev.mathops.db.logic;

import dev.mathops.commons.CoreConstants;
import dev.mathops.db.Cache;
import dev.mathops.db.schema.legacy.impl.RawAdminHoldLogic;
import dev.mathops.db.schema.legacy.impl.RawChallengeFeeLogic;
import dev.mathops.db.schema.legacy.impl.RawDisciplineLogic;
import dev.mathops.db.schema.legacy.impl.RawExceptStuLogic;
import dev.mathops.db.schema.legacy.impl.RawFfrTrnsLogic;
import dev.mathops.db.schema.legacy.impl.RawMpeCreditLogic;
import dev.mathops.db.schema.legacy.impl.RawMpecrDeniedLogic;
import dev.mathops.db.schema.legacy.impl.RawPaceAppealsLogic;
import dev.mathops.db.schema.legacy.impl.RawPacingStructureLogic;
import dev.mathops.db.schema.legacy.impl.RawPendingExamLogic;
import dev.mathops.db.schema.legacy.impl.RawPlcFeeLogic;
import dev.mathops.db.schema.legacy.impl.RawSpecialStusLogic;
import dev.mathops.db.schema.legacy.impl.RawStchallengeLogic;
import dev.mathops.db.schema.legacy.impl.RawStcourseLogic;
import dev.mathops.db.schema.legacy.impl.RawStcunitLogic;
import dev.mathops.db.schema.legacy.impl.RawStcuobjectiveLogic;
import dev.mathops.db.schema.legacy.impl.RawStetextLogic;
import dev.mathops.db.schema.legacy.impl.RawStexamLogic;
import dev.mathops.db.schema.legacy.impl.RawSthomeworkLogic;
import dev.mathops.db.schema.legacy.impl.RawSthwqaLogic;
import dev.mathops.db.schema.legacy.impl.RawStmathplanLogic;
import dev.mathops.db.schema.legacy.impl.RawStmilestoneLogic;
import dev.mathops.db.schema.legacy.impl.RawStmpeLogic;
import dev.mathops.db.schema.legacy.impl.RawStmsgLogic;
import dev.mathops.db.schema.legacy.impl.RawStqaLogic;
import dev.mathops.db.schema.legacy.impl.RawStresourceLogic;
import dev.mathops.db.schema.legacy.impl.RawStsurveyqaLogic;
import dev.mathops.db.schema.legacy.impl.RawSttermLogic;
import dev.mathops.db.schema.legacy.impl.RawStudentLogic;
import dev.mathops.db.schema.legacy.impl.RawStvisitLogic;
import dev.mathops.db.schema.legacy.impl.RawUsersLogic;
import dev.mathops.db.schema.legacy.rec.RawAdminHold;
import dev.mathops.db.schema.legacy.rec.RawChallengeFee;
import dev.mathops.db.schema.legacy.rec.RawDiscipline;
import dev.mathops.db.schema.legacy.rec.RawExceptStu;
import dev.mathops.db.schema.legacy.rec.RawFfrTrns;
import dev.mathops.db.schema.legacy.rec.RawMpeCredit;
import dev.mathops.db.schema.legacy.rec.RawMpecrDenied;
import dev.mathops.db.schema.legacy.rec.RawPaceAppeals;
import dev.mathops.db.schema.legacy.rec.RawPacingStructure;
import dev.mathops.db.schema.legacy.rec.RawPendingExam;
import dev.mathops.db.schema.legacy.rec.RawPlcFee;
import dev.mathops.db.schema.RawRecordConstants;
import dev.mathops.db.schema.legacy.rec.RawSpecialStus;
import dev.mathops.db.schema.legacy.rec.RawStchallenge;
import dev.mathops.db.schema.legacy.rec.RawStcourse;
import dev.mathops.db.schema.legacy.rec.RawStcunit;
import dev.mathops.db.schema.legacy.rec.RawStcuobjective;
import dev.mathops.db.schema.legacy.rec.RawStetext;
import dev.mathops.db.schema.legacy.rec.RawStexam;
import dev.mathops.db.schema.legacy.rec.RawSthomework;
import dev.mathops.db.schema.legacy.rec.RawSthwqa;
import dev.mathops.db.schema.legacy.rec.RawStmathplan;
import dev.mathops.db.schema.legacy.rec.RawStmilestone;
import dev.mathops.db.schema.legacy.rec.RawStmpe;
import dev.mathops.db.schema.legacy.rec.RawStmsg;
import dev.mathops.db.schema.legacy.rec.RawStqa;
import dev.mathops.db.schema.legacy.rec.RawStresource;
import dev.mathops.db.schema.legacy.rec.RawStsurveyqa;
import dev.mathops.db.schema.legacy.rec.RawStterm;
import dev.mathops.db.schema.legacy.rec.RawStudent;
import dev.mathops.db.schema.legacy.rec.RawStvisit;
import dev.mathops.db.schema.legacy.rec.RawUsers;
import dev.mathops.db.schema.term.rec.MasteryAttemptRec;
import dev.mathops.db.schema.term.rec.StuCourseMasteryRec;
import dev.mathops.db.schema.term.rec.StuStandardMilestoneRec;
import dev.mathops.db.schema.term.rec.StuUnitMasteryRec;
import dev.mathops.db.schema.main.rec.TermRec;
import dev.mathops.db.schema.main.impl.MasteryAttemptLogic;
import dev.mathops.db.schema.main.impl.StuCourseMasteryLogic;
import dev.mathops.db.schema.main.impl.StuStandardMilestoneLogic;
import dev.mathops.db.schema.main.impl.StuUnitMasteryLogic;
import dev.mathops.db.field.TermKey;

import java.sql.SQLException;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data container for all data associated with a single student, with specific data loaded lazily as needed.
 */
public final class StudentData {

    /** The cache. */
    private final Cache cache;

    /** The student ID. */
    private final String studentId;

    /** The setting for doing live refreshes on queries. */
    private final ELiveRefreshes liveRefreshes;

    /** The student record. */
    private RawStudent studentRecord = null;

    /** The student's pacing structure (as indicated in the student record). */
    private RawPacingStructure pacingStructure = null;

    /** The list of all holds on the student's account. */
    private List<RawAdminHold> holds = null;

    /** The list of all disciplinary actions on the student's account. */
    private List<RawDiscipline> disciplinaryActions = null;

    /** The list of all "visiting student" configurations for the student. */
    private List<RawExceptStu> visitingRegistrations = null;

    /** The list of all transfer credit records on the student account. */
    private List<RawFfrTrns> transferCredit = null;

    /** The list of all resources on loan to the student. */
    private List<RawStresource> resourcesOnLoan = null;

    /** The list of all placement attempts on record for this student. */
    private List<RawStmsg> messagesSent = null;

    /** The list of student visits to the center. */
    private List<RawStvisit> centerVisits = null;

    /** The list of all pending exams on record for this student. */
    private List<RawPendingExam> pendingExams = null;

    /** The list of all special student configurations for this student. */
    private List<RawSpecialStus> specialCategories = null;

    /** The list of all Math Plan responses on record for this student. */
    private List<RawStmathplan> mathPlanResponses = null;

    /** The list of all placement attempts on record for this student. */
    private List<RawStmpe> placementAttempts = null;

    /** The list of all placement credit on record for this student. */
    private List<RawMpeCredit> placementCredit = null;

    /** The list of all placement credit that was denied for this student. */
    private List<RawMpecrDenied> placementDenied = null;

    /** The placement fee assessed to the student. */
    private RawPlcFee placementFee = null;

    /** The list of challenge exams this student has taken. */
    private List<RawStchallenge> challengeExams = null;

    /** The challenge fees assessed to the student. */
    private List<RawChallengeFee> challengeFees = null;

    /** The list of all survey responses. */
    private List<RawStsurveyqa> surveyResponses = null;

    /** The list of all registrations for this student. */
    private List<RawStcourse> registrations = null;

    /** The student term configurations. */
    private List<RawStterm> studentTerm = null;

    /** The list of all exam attempts on record for this student. */
    private List<RawStexam> studentExams = null;

    /** The list of all exam attempt answers on record for this student. */
    private List<RawStqa> studentExamAnswers = null;

    /** The list of all homework attempts on record for this student. */
    private List<RawSthomework> studentHomeworks = null;

    /** The list of all homework attempt answers on record for this student. */
    private List<RawSthwqa> studentHomeworkAnswers = null;

    /** The list of all course unit status objects  for this student. */
    private List<RawStcunit> studentCourseUnits = null;

    /** The list of all course unit objective status objects for this student. */
    private List<RawStcuobjective> studentCourseObjectives = null;

    /** The list of all mastery attempts on the student's record. */
    private List<MasteryAttemptRec> masteryAttempts = null;

    /** The list of student course mastery records for the student. */
    private List<StuCourseMasteryRec> studentCourseMastery = null;

    /** The list of student unit mastery records for the student. */
    private List<StuUnitMasteryRec> studentUnitMastery = null;

    /** The list of all milestone overrides that apply to the student this term. */
    private List<RawStmilestone> studentMilestones = null;

    /** The list of all overrides to standards-based milestones for this student this term. */
    private List<StuStandardMilestoneRec> studentStandardMilestones = null;

    /** The list of all deadline appeals on record for this student. */
    private List<RawPaceAppeals> deadlineAppeals = null;

    /** The list of all student e-texts. */
    private List<RawStetext> studentETexts = null;

    /** The list of user's exams taken by the student. */
    private List<RawUsers> usersExams = null;

    /**
     * Constructs a new {@code StudentData}.
     *
     * @param theCache         the associated cache
     * @param theStudentId     the student ID
     * @param theLiveRefreshes true if live student data should be queried;
     */
    public StudentData(final Cache theCache, final String theStudentId, final ELiveRefreshes theLiveRefreshes) {

        if (theCache == null) {
            throw new IllegalArgumentException("Cache may not be null");
        }
        if (theStudentId == null) {
            throw new IllegalArgumentException("Student ID may not be null");
        }
        if (theLiveRefreshes == null) {
            throw new IllegalArgumentException("Live refreshes setting may not be null");
        }

        this.cache = theCache;
        this.studentId = theStudentId;
        this.liveRefreshes = theLiveRefreshes;
    }

    /**
     * Constructs a new {@code StudentData}.
     *
     * @param theCache         the cache
     * @param theStudentRecord the student record
     */
    public StudentData(final Cache theCache, final RawStudent theStudentRecord) {

        if (theCache == null) {
            throw new IllegalArgumentException("Cache may not be null");
        }
        if (theStudentRecord == null) {
            throw new IllegalArgumentException("Student record may not be null");
        }

        this.cache = theCache;
        this.studentId = theStudentRecord.stuId;
        this.studentRecord = theStudentRecord;
        this.liveRefreshes = ELiveRefreshes.NONE;
    }

    /**
     * Gets the cache.
     *
     * @return the cache
     */
    public Cache getCache() {

        return this.cache;
    }

    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public String getStudentId() {

        return this.studentId;
    }

    /**
     * Gets the student record, querying for it if it has not already been loaded.
     *
     * @return the student record; null only if the student record does not exist
     * @throws SQLException if there is an error accessing the database
     */
    public RawStudent getStudentRecord() throws SQLException {

        if (this.studentRecord == null) {
            if ("GUEST".equals(this.studentId)) {
                this.studentRecord = RawStudentLogic.makeFakeStudent("GUEST", CoreConstants.EMPTY, "Guest");
            } else if ("AACTUTOR".equals(this.studentId) || "ETEXT".equals(this.studentId)) {
                this.studentRecord = RawStudentLogic.makeFakeStudent(this.studentId, CoreConstants.EMPTY, "Tutor");
            } else if ("BOOKSTORE".equals(this.studentId)) {
                this.studentRecord = RawStudentLogic.makeFakeStudent(this.studentId, CoreConstants.EMPTY,
                        "Bookstore Staff");
            } else if (this.liveRefreshes == ELiveRefreshes.ALL) {
                this.studentRecord = RawStudentLogic.query(this.cache, this.studentId, true);
            } else {
                this.studentRecord = RawStudentLogic.query(this.cache, this.studentId, false);

                if (this.liveRefreshes == ELiveRefreshes.IF_MISSING) {
                    this.studentRecord = RawStudentLogic.query(this.cache, this.studentId, true);
                }
            }
        }

        return this.studentRecord;
    }

    /**
     * Forgets the student record, forcing a re-query on next access.
     */
    public void forgetStudentRecord() {

        this.studentRecord = null;
    }

    /**
     * Gets the student's pacing structure.
     *
     * @return the student record; null only if the student record does not exist
     * @throws SQLException if there is an error accessing the database
     */
    public RawPacingStructure getPacingStructure() throws SQLException {

        if (this.pacingStructure == null) {
            final RawStudent student = getStudentRecord();

            if (student != null && student.pacingStructure != null) {
                this.pacingStructure = RawPacingStructureLogic.query(this.cache, student.pacingStructure);
            }
        }

        return this.pacingStructure;
    }

    /**
     * Forgets the pacing structure, forcing a re-query on next access.
     */
    public void forgetPacingStructure() {

        this.pacingStructure = null;
    }

    /**
     * Gets the list of all holds on the student's account.
     *
     * @return the list of holds
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawAdminHold> getHolds() throws SQLException {

        if (this.holds == null) {
            this.holds = RawAdminHoldLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.holds;
    }

    /**
     * Retrieves a single hold from teh student's account.
     *
     * @param holdId the hold ID
     * @return the hold, if found; null if not
     * @throws SQLException if there is an error accessing the database
     */
    public RawAdminHold getHold(final String holdId) throws SQLException {

        final List<RawAdminHold> all = getHolds();
        RawAdminHold result = null;

        for (final RawAdminHold test : all) {
            if (test.holdId.equals(holdId)) {
                result = test;
                break;
            }
        }

        return result;
    }

    /**
     * Forgets administrative holds, forcing a re-query on next access.
     */
    public void forgetHolds() {

        this.holds = null;
    }

    /**
     * Gets the list of all disciplinary actions on the student's account.
     *
     * @return the list of disciplinary actions
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawDiscipline> getDisciplinaryActions() throws SQLException {

        if (this.disciplinaryActions == null) {
            this.disciplinaryActions = RawDisciplineLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.disciplinaryActions;
    }

    /**
     * Forgets disciplinary actions, forcing a re-query on next access.
     */
    public void forgetDisciplinaryActions() {

        this.disciplinaryActions = null;
    }

    /**
     * Gets the list of all visiting student registrations for the student.
     *
     * @return the list of visiting student registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawExceptStu> getVisitingRegistrations() throws SQLException {

        if (this.visitingRegistrations == null) {
            this.visitingRegistrations = RawExceptStuLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.visitingRegistrations;
    }

    /**
     * Gets the list of all transfer credit for the student.
     *
     * @return the list of transfer credit
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawFfrTrns> getTransferCredit() throws SQLException {

        if (this.transferCredit == null) {
            this.transferCredit = RawFfrTrnsLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.transferCredit;
    }

    /**
     * Gets the list of all resources on loan to the student.
     *
     * @return the list of resources on loan
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStresource> getResourcesOnLoan() throws SQLException {

        if (this.resourcesOnLoan == null) {
            this.resourcesOnLoan = RawStresourceLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.resourcesOnLoan;
    }

    /**
     * Gets the list of all messages sent to the student.
     *
     * @return the list of messages sent
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmsg> getMessagesSent() throws SQLException {

        if (this.messagesSent == null) {
            this.messagesSent = RawStmsgLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.messagesSent;
    }

    /**
     * Gets the list of all center visits by the student.
     *
     * @return the list of center visits
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStvisit> getCenterVisits() throws SQLException {

        if (this.centerVisits == null) {
            this.centerVisits = RawStvisitLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.centerVisits;
    }

    /**
     * Gets the list of all pending exams on record for this student.
     *
     * @return the list of pending exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawPendingExam> getPendingExams() throws SQLException {

        if (this.pendingExams == null) {
            this.pendingExams = RawPendingExamLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.pendingExams;
    }

    /**
     * Forgets pending exams, forcing a re-query on next access.
     */
    public void forgetPendingExams() {

        this.pendingExams = null;
    }

    /**
     * Gets the list of all special student categories to which the student belongs.
     *
     * @return the list of special student categories
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSpecialStus> getSpecialCategories() throws SQLException {

        if (this.specialCategories == null) {
            this.specialCategories = RawSpecialStusLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.specialCategories;
    }

    /**
     * Gets the list of all special student categories to which the student belongs that are active as of a specified
     * date.
     *
     * @param today the date for which to return active special categories
     * @return the list of special student categories
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSpecialStus> getActiveSpecialCategories(final ChronoLocalDate today) throws SQLException {

        final List<RawSpecialStus> all = getSpecialCategories();
        final List<RawSpecialStus> active = new ArrayList<>(5);

        for (final RawSpecialStus test : all) {
            if (test.startDt != null && test.startDt.isAfter(today)) {
                continue;
            }
            if (test.endDt != null && test.endDt.isBefore(today)) {
                continue;
            }
            active.add(test);
        }

        return active;
    }

    /**
     * Tests whether the student is a member of any one of a list of special categories as of a specified date.
     *
     * @param today      the date for which to return active special categories
     * @param categories the category (or categories) for which to test
     * @return {@code true} if the student is a member of {@code category} as of {@code today}
     * @throws SQLException if there is an error accessing the database
     */
    public boolean isSpecialCategory(final ChronoLocalDate today, final String... categories) throws SQLException {

        boolean isMember = false;

        if (categories != null && categories.length > 0) {
            final List<RawSpecialStus> all = getSpecialCategories();

            for (final RawSpecialStus test : all) {
                for (final String cat : categories) {
                    if (test.stuType.equals(cat)) {
                        isMember = test.startDt == null || !test.startDt.isAfter(today);
                        if (test.endDt != null && test.endDt.isBefore(today)) {
                            isMember = false;
                        }
                        break;
                    }
                }
                if (isMember) {
                    break;
                }
            }
        }

        return isMember;
    }

    /**
     * Forgets special categories to which the student belongs, forcing a re-query on next access.
     */
    public void forgetSpecialCategories() {

        this.specialCategories = null;
    }

    /**
     * Gets the list of all Math Plan responses on record for the student.
     *
     * @return the list of Math Plan responses
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmathplan> getMathPlanResponses() throws SQLException {

        if (this.mathPlanResponses == null) {
            this.mathPlanResponses = RawStmathplanLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.mathPlanResponses;
    }

    /**
     * Gets the most recent responses by the student for a specified page in the Math Plan.
     *
     * @param pageId the page ID
     * @return a map from survey question number to the latest response
     * @throws SQLException if there is an error accessing the database
     */
    public Map<Integer, RawStmathplan> getLatestMathPlanResponsesByPage(final String pageId) throws SQLException {

        final List<RawStmathplan> all = getMathPlanResponses();

        final Map<Integer, RawStmathplan> map = new HashMap<>(10);

        for (final RawStmathplan test : all) {
            if (pageId.equals(test.version)) {
                final RawStmathplan existing = map.get(test.surveyNbr);

                if (existing == null) {
                    map.put(test.surveyNbr, test);
                } else if (test.examDt.isBefore(existing.examDt)) {
                    map.put(test.surveyNbr, test);
                } else if (test.examDt.equals(existing.examDt)
                           && test.finishTime.intValue() < existing.finishTime.intValue()) {
                    map.put(test.surveyNbr, test);
                }
            }
        }

        return map;
    }

    /**
     * Forgets math plan responses, forcing a re-query on next access.
     */
    public void forgetMathPlanResponses() {

        this.mathPlanResponses = null;
    }

    /**
     * Gets the list of all placement attempts on record for the student.
     *
     * @return the list of placement attempts
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmpe> getPlacementAttempts() throws SQLException {

        if (this.placementAttempts == null) {
            this.placementAttempts = RawStmpeLogic.queryByStudent(this.cache, this.studentId);
            this.placementAttempts.sort(new RawStmpe.FinishDateTimeComparator());
        }

        return this.placementAttempts;
    }

    /**
     * Gets the list of all placement attempts on record for the student.
     *
     * @return the list of placement attempts
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmpe> getLegalPlacementAttempts() throws SQLException {

        final List<RawStmpe> all = getPlacementAttempts();
        final List<RawStmpe> result = new ArrayList<>(all.size());

        for (final RawStmpe test : all) {
            final String placed = test.placed;

            if ("Y".equals(placed) || "N".equals(placed)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Counts placement attempts for a particular version.
     *
     * @param version the version of the exam
     * @return A 2-integer array, where [0] contains the number of legal online attempts, and [1] contains the number of
     *         legal proctored attempts; or {@code null} on any error
     * @throws SQLException if there is an error accessing the database
     */
    public int[] countLegalAttempts(final String version) throws SQLException {

        final int[] result;

        if (this.studentId.startsWith("99")) {
            result = RawStmpeLogic.countTestStudentLegalAttempts(this.studentId, version);
        } else {
            int numOnline = 0;
            int numProctored = 0;

            final List<RawStmpe> all = getLegalPlacementAttempts();
            for (final RawStmpe test : all) {
                if (test.version.equals(version)) {
                    final String placed = test.placed;
                    if ("Y".equals(placed) || "N".equals(placed)) {
                        final String howValidated = test.howValidated;

                        if ("P".equals(howValidated) || "C".equals(howValidated)) {
                            ++numProctored;
                        } else {
                            ++numOnline;
                        }
                    }
                }
            }

            result = new int[]{numOnline, numProctored};
        }

        return result;
    }

    /**
     * Forgets placement attempts, forcing a re-query on next access.
     */
    public void forgetPlacementAttempts() {

        this.placementAttempts = null;
    }

    /**
     * Gets the list of all placement credit earned by the student.
     *
     * @return the list of placement credit
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawMpeCredit> getPlacementCredit() throws SQLException {

        if (this.placementCredit == null) {
            this.placementCredit = RawMpeCreditLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.placementCredit;
    }

    /**
     * Forgets placement credit, forcing a re-query on next access.
     */
    public void forgetPlacementCredit() {

        this.placementCredit = null;
    }

    /**
     * Gets the list of all placement that was denied to the student.
     *
     * @return the list of placement denied
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawMpecrDenied> getPlacementDenied() throws SQLException {

        if (this.placementDenied == null) {
            this.placementDenied = RawMpecrDeniedLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.placementDenied;
    }

    /**
     * Forgets placement denied, forcing a re-query on next access.
     */
    public void forgetPlacementDenied() {

        this.placementDenied = null;
    }

    /**
     * Gets the list of all placement attempts on record for the student.
     *
     * @return the list of placement attempts
     * @throws SQLException if there is an error accessing the database
     */
    public RawPlcFee getPlacementFee() throws SQLException {

        // FIXME: If there is no placement fee, this will trigger a new query each time.  Store a flag indicating the
        //  null result is real?

        if (this.placementFee == null) {
            this.placementFee = RawPlcFeeLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.placementFee;
    }

    /**
     * Gets the list of all challenge exams taken by the student.
     *
     * @return the list of challenge exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStchallenge> getChallengeExams() throws SQLException {

        if (this.challengeExams == null) {
            this.challengeExams = RawStchallengeLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.challengeExams;
    }

    /**
     * Gets the list of all challenge exams taken by the student for a course.
     *
     * @param course the course ID
     * @return the list of challenge exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStchallenge> getChallengeExamsByCourse(final String course) throws SQLException {

        final List<RawStchallenge> all = getChallengeExams();
        final List<RawStchallenge> match = new ArrayList<>(2);

        for (final RawStchallenge test : all) {
            if (test.course.equals(course)) {
                match.add(test);
            }
        }

        return match;
    }

    /**
     * Counts all legal challenge attempts for a given course ID (there normally should be 0 or 1).
     *
     * @param course the course ID for which to query
     * @return the number of legal attempts
     * @throws SQLException if there is an error performing the query
     */
    public int countLegalChallengeAttempts(final String course) throws SQLException {

        int result = 0;

        final String stuId = getStudentId();

        if (stuId.startsWith("99")) {

            final char ch5 = stuId.charAt(4);
            final char ch6 = stuId.charAt(5);

            if (ch5 == 'C' && ch6 == 'H') {
                final String test = stuId.substring(6);

                result = switch (test) {
                    case "019" -> RawRecordConstants.M117.equals(course) ? 1 : 0;
                    case "026" -> RawRecordConstants.M118.equals(course) ? 1 : 0;
                    case "036" -> RawRecordConstants.M124.equals(course) ? 1 : 0;
                    case "046" -> RawRecordConstants.M125.equals(course) ? 1 : 0;
                    case "056" -> RawRecordConstants.M126.equals(course) ? 1 : 0;
                    default -> 0;
                };
            }
        } else {
            final List<RawStchallenge> all = getChallengeExamsByCourse(course);

            for (final RawStchallenge test : all) {
                final String passed = test.passed;
                if ("Y".equals(passed) || "N".equals(passed)) {
                    ++result;
                }
            }
        }

        return result;
    }

    /**
     * Forgets challenge exams, forcing a re-query on next access.
     */
    public void forgetChallengeExams() {

        this.challengeExams = null;
    }

    /**
     * Gets the list of all placement attempts on record for the student.
     *
     * @return the list of placement attempts
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawChallengeFee> getChallengeFees() throws SQLException {

        if (this.challengeFees == null) {
            this.challengeFees = RawChallengeFeeLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.challengeFees;
    }

    /**
     * Gets the list of all survey responses the student provided on all surveys.
     *
     * @return the list of survey responses
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStsurveyqa> getSurveyResponses() throws SQLException {

        if (this.surveyResponses == null) {
            this.surveyResponses = RawStsurveyqaLogic.queryLatestByStudent(this.cache, this.studentId);
        }

        return this.surveyResponses;
    }

    /**
     * Gets the list of all survey responses the student provided for a specified survey version.
     *
     * @param version the version
     * @return the list of survey responses
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStsurveyqa> getSurveyResponsesByVersion(final String version) throws SQLException {

        final List<RawStsurveyqa> all = getSurveyResponses();
        final List<RawStsurveyqa> result = new ArrayList<>(10);

        for (final RawStsurveyqa test : all) {
            if (test.version.equals(version)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Forgets survey responses, forcing a re-query on next access.
     */
    public void forgetSurveyResponses() {

        this.surveyResponses = null;
    }

    /**
     * Gets the list of all registrations for this student (this will include MATH 120 registrations).
     *
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getRegistrations() throws SQLException {

        if (this.registrations == null) {
            this.registrations = RawStcourseLogic.queryByStudent(this.cache, this.studentId, true, true);

            for (final RawStcourse test : this.registrations) {
                if (RawRecordConstants.M117.equals(test.course)
                    && ("801".equals(test.sect) || "809".equals(test.sect))
                    && (test.prereqSatis == null || "N".equals(test.prereqSatis))) {
                    test.prereqSatis = "P";
                }
            }
        }

        return this.registrations;
    }

    /**
     * Gets the list of active registrations for the student in a specified term.  This excludes those with open status
     * "D".  It includes registrations in "OT" sections (challenge credit).
     *
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getNonDroppedRegistrations() throws SQLException {

        final List<RawStcourse> all = getRegistrations();

        final List<RawStcourse> active = new ArrayList<>(6);
        for (final RawStcourse test : all) {
            if ("D".equals(test.openStatus)) {
                continue;
            }

            if (RawRecordConstants.isOneCreditCourse(test.course)) {
                active.add(test);
            }
        }

        return active;
    }

    /**
     * Gets the list of active registrations for the student in a specified term.  This excludes those with open status
     * "D" or "G".  It includes registrations in "OT" sections (challenge credit).
     *
     * @param term           the term key
     * @param includeForfeit true to include courses marked with open_status='G'
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getActiveRegistrations(final TermKey term, final boolean includeForfeit)
            throws SQLException {

        final List<RawStcourse> all = getRegistrations();

        final List<RawStcourse> active = new ArrayList<>(6);
        for (final RawStcourse test : all) {
            if (test.termKey.equals(term)) {
                final String openStatus = test.openStatus;
                if ("D".equals(openStatus)) {
                    continue;
                }

                if (RawRecordConstants.isOneCreditCourse(test.course) && (includeForfeit || !"G".equals(openStatus))) {
                    active.add(test);
                }
            }
        }

        return active;
    }

    /**
     * Gets the list of registrations for the student in a specified term that have not been dropped and that are not
     * "OT" sections (challenge credit).
     *
     * @param term the term key
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getNonChallengeRegistrations(final TermKey term) throws SQLException {

        final List<RawStcourse> all = getRegistrations();

        final List<RawStcourse> active = new ArrayList<>(6);
        for (final RawStcourse test : all) {
            if (test.termKey.equals(term)) {
                if ("D".equals(test.openStatus) || "OT".equals(test.instrnType)) {
                    continue;
                }

                if (RawRecordConstants.isOneCreditCourse(test.course)) {
                    active.add(test);
                }
            }
        }

        return active;
    }

    /**
     * Gets the list of all registrations marked as completed.
     *
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getCompletedRegistrations() throws SQLException {

        final List<RawStcourse> all = getRegistrations();

        final List<RawStcourse> completed = new ArrayList<>(6);
        for (final RawStcourse test : all) {
            if ("D".equals(test.openStatus)) {
                continue;
            }
            if ("Y".equals(test.completed) && RawRecordConstants.isOneCreditCourse(test.course)) {
                completed.add(test);
            }
        }

        return completed;
    }

    /**
     * Gets the list of all registrations before a given term.
     *
     * @param term the term
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getRegistrationsBeforeTerm(final TermKey term) throws SQLException {

        final List<RawStcourse> all = getRegistrations();

        final List<RawStcourse> before = new ArrayList<>(6);
        for (final RawStcourse test : all) {
            if (test.termKey.compareTo(term) < 0 && RawRecordConstants.isOneCreditCourse(test.course)) {
                before.add(test);
            }
        }

        return before;
    }

    /**
     * Gets the list of all registrations in the active term that participate in the pace.
     *
     * @return the list of registrations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getPacedRegistrations() throws SQLException {

        final List<RawStcourse> all = getRegistrations();
        final List<RawStcourse> paced = new ArrayList<>(5);

        for (final RawStcourse test : all) {
            if ("OT".equals(test.instrnType)) {
                continue;
            }
            final String openStatus = test.openStatus;
            if ("D".equals(openStatus) || "G".equals(openStatus)) {
                continue;
            }
            if ("Y".equals(test.iInProgress) && "N".equals(test.iCounted)) {
                continue;
            }

            // TODO: We really should check the section or rule set to see if these are "paced" courses,
            //  but for now all courses are "paced".

            if (RawRecordConstants.isOneCreditCourse(test.course)) {
                paced.add(test);
            }
        }

        return paced;
    }

    /**
     * Retrieve the registration for a student in a course in the active term, if any. This will return OT rows, but not
     * Dropped or "ignored" rows.
     *
     * @param courseId the ID of the course
     * @return the course registration, if one exists; {@code null} if not or if an error occurs
     * @throws SQLException if there is an error performing the query
     */
    public RawStcourse getActiveRegistration(final String courseId) throws SQLException {

        final List<RawStcourse> all = getRegistrations();
        RawStcourse result = null;

        final TermRec active = this.cache.getSystemData().getActiveTerm();
        if (active != null) {
            for (final RawStcourse test : all) {
                if ("D".equals(test.openStatus) || "G".equals(test.openStatus)) {
                    continue;
                }
                if (active.term.equals(test.termKey) && test.course.equals(courseId)) {
                    result = test;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Retrieves all student registration records that represent credit by exam.
     *
     * @return the list of matching exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcourse> getCreditByExam() throws SQLException {

        final List<RawStcourse> all = getRegistrations();
        final List<RawStcourse> result = new ArrayList<>(5);

        // FIXME: Why "F" and "M" below?

        for (final RawStcourse test : all) {
            if ("OT".equals(test.instrnType)) {
                final String placed = test.examPlaced;
                if ("F".equals(placed) || "M".equals(placed) && RawRecordConstants.isOneCreditCourse(test.course)) {
                    result.add(test);
                }
            }
        }

        return result;
    }

    /**
     * Forgets student registrations, forcing a re-query on next access.
     */
    public void forgetRegistrations() {

        this.registrations = null;
    }

    /**
     * Gets the student term configurations.
     *
     * @return the student term configurations
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStterm> getStudentTerms() throws SQLException {

        if (this.studentTerm == null) {
            this.studentTerm = RawSttermLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentTerm;
    }

    /**
     * Gets the student term configuration for a specific term.
     *
     * @param term the term key of the term whose record to retrieve
     * @return the student term configurations
     * @throws SQLException if there is an error accessing the database
     */
    public RawStterm getStudentTerm(final TermKey term) throws SQLException {

        RawStterm result = null;

        final List<RawStterm> all = getStudentTerms();
        for (final RawStterm test : all) {
            if (test.termKey.equals(term)) {
                result = test;
                break;
            }
        }

        return result;
    }

    /**
     * Forgets student term configurations, forcing a re-query on next access.
     */
    public void forgetStudentTerm() {

        this.studentTerm = null;
    }

    /**
     * Gets the list of all records of exams taken by the student.
     *
     * @return the student exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStexam> getStudentExams() throws SQLException {

        if (this.studentExams == null) {
            this.studentExams = RawStexamLogic.queryByStudent(this.cache, this.studentId, true);
            this.studentExams.sort(new RawStexam.FinishDateTimeComparator());
        }

        return this.studentExams;
    }

    /**
     * Gets the list of all records of exams taken by the student in a specified course.
     *
     * @param course the course ID
     * @return the student exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStexam> getStudentExamsForCourse(final String course) throws SQLException {

        final List<RawStexam> all = getStudentExams();
        final List<RawStexam> result = new ArrayList<>(all.size());

        for (final RawStexam test : all) {
            if (test.course.equals(course)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Gets the list of all records of exams taken by the student in a specified course, potentially restricting results
     * to only passed exams and/or only exams among one or more types.
     *
     * @param course     the course ID
     * @param passedOnly true to only return passed exams
     * @param examTypes  a list of zero or more exam types to return - if none are supplied, all exam types are
     *                   returned
     * @return the student exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStexam> getStudentExamsByCourseType(final String course, final boolean passedOnly,
                                                       final String... examTypes) throws SQLException {

        final List<RawStexam> all = getStudentExams();
        final List<RawStexam> result = new ArrayList<>(10);

        for (final RawStexam test : all) {
            if (test.course.equals(course)) {
                if (passedOnly && !"Y".equals(test.passed)) {
                    continue;
                }

                if (examTypes == null || examTypes.length == 0) {
                    result.add(test);
                } else {
                    for (final String type : examTypes) {
                        if (test.examType.equals(type)) {
                            result.add(test);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the list of all records of exams taken by the student in a specified course and unit, potentially
     * restricting results to only passed exams and/or only exams among one or more types.
     *
     * @param course     the course ID
     * @param unit       the unit
     * @param passedOnly true to only return passed exams
     * @param examTypes  a list of zero or more exam types to return - if none are supplied, all exam types are
     *                   returned
     * @return the student exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStexam> getStudentExamsByCourseUnitType(final String course, final Integer unit,
                                                           final boolean passedOnly, final String... examTypes)
            throws SQLException {

        final List<RawStexam> all = getStudentExams();
        final List<RawStexam> result = new ArrayList<>(10);

        for (final RawStexam test : all) {
            if (test.course.equals(course) && test.unit.equals(unit)) {
                if (passedOnly && !"Y".equals(test.passed)) {
                    continue;
                }

                if (examTypes == null || examTypes.length == 0) {
                    result.add(test);
                } else {
                    for (final String type : examTypes) {
                        if (test.examType.equals(type)) {
                            result.add(test);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Retrieves all exams for a student for a specified exam version.
     *
     * @param version    the exam version
     * @param passedOnly {@code true} to return only exams with passed = 'Y'
     * @return the list of matching exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStexam> getStudentExamsByVersion(final String version, final boolean passedOnly)
            throws SQLException {

        final List<RawStexam> all = getStudentExams();
        final List<RawStexam> result = new ArrayList<>(10);

        for (final RawStexam test : all) {
            if (test.version.equals(version)) {
                if (!passedOnly || "Y".equals(test.passed)) {
                    result.add(test);
                }
            }
        }

        return result;
    }

    /**
     * Scans all student exams in a course and unit and returns the first passing attempt.
     *
     * @param course the course ID
     * @param unit   the unit
     * @param type   the exam type
     * @return the first passing exam of the specified type in the specified course and unit; null if no passing attempt
     *         was found
     * @throws SQLException if there is an error accessing the database
     */
    public RawStexam getFirstPassingStudentExam(final String course, final Integer unit, final String type)
            throws SQLException {

        final List<RawStexam> all = getStudentExams();
        RawStexam result = null;

        for (final RawStexam test : all) {
            if (test.course.equals(course) && test.unit.equals(unit) && test.examType.equals(type)
                && "Y".equals(test.passed)) {

                if (result == null || result.getFinishDateTime().isAfter(test.getFinishDateTime())) {
                    result = test;
                }
            }
        }

        return result;
    }

    /**
     * Returns the number of unit exam attempts taken by a student in a course and unit since the last passing review
     * exam.
     *
     * @param course the course
     * @param unit   the unit
     * @return the number of unit exam attempts that were found since the most recent passing review exam
     * @throws SQLException if there is an error accessing the database
     */
    public int countUnitSinceLastPassedReview(final String course, final Integer unit) throws SQLException {

        // We rely on the following to generate a list sorted by finish date/time...
        final List<RawStexam> all = getStudentExamsByCourseUnitType(course, unit, false, RawStexamLogic.ALL_EXAM_TYPES);

        int tries = 0;

        for (int i = all.size() - 1; i >= 0; --i) {
            final RawStexam test = all.get(i);

            if ("U".equals(test.examType)) {
                ++tries;
            }
            if ("R".equals(test.examType) && "Y".equals(test.passed)) {
                break;
            }
        }

        return tries;
    }

    /**
     * Forgets student exams. forcing a re-query on next access.
     */
    public void forgetStudentExams() {

        this.studentExams = null;
    }

    /**
     * Gets the list of all answers for exams taken by the student.
     *
     * @return the student exam answers
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStqa> getStudentExamAnswers() throws SQLException {

        if (this.studentExamAnswers == null) {
            this.studentExamAnswers = RawStqaLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentExamAnswers;
    }

    /**
     * Forgets student exam answers. forcing a re-query on next access.
     */
    public void forgetStudentExamAnswers() {

        this.studentExamAnswers = null;
    }

    /**
     * Gets the list of all records of homeworks taken by the student.
     *
     * @return the student homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSthomework> getStudentHomework() throws SQLException {

        if (this.studentHomeworks == null) {
            this.studentHomeworks = RawSthomeworkLogic.queryByStudent(this.cache, this.studentId, true);
        }

        return this.studentHomeworks;
    }

    /**
     * Gets the list of all records of homeworks taken by the student in a specified course.
     *
     * @param course     the course ID
     * @param passedOnly true to only return records marked as "passed"
     * @param hwTypes    an optional list of types to return (if null or empty, all types are returned)
     * @return the student homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSthomework> getStudentHomeworkForCourse(final String course, final boolean passedOnly,
                                                           final String... hwTypes) throws SQLException {

        final List<RawSthomework> all = getStudentHomework();
        final List<RawSthomework> result = new ArrayList<>(all.size());

        for (final RawSthomework test : all) {
            if (test.course.equals(course)) {

                if (passedOnly && !"Y".equals(test.passed)) {
                    continue;
                }

                if (hwTypes == null || hwTypes.length == 0) {
                    result.add(test);
                } else {
                    for (final String type : hwTypes) {
                        if (test.hwType.equals(type)) {
                            result.add(test);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the list of all records of homeworks taken by the student in a specified course and unit.
     *
     * @param course     the course ID
     * @param unit       the unit
     * @param passedOnly true to only return records marked as "passed"
     * @param hwTypes    an optional list of types to return (if null or empty, all types are returned)
     * @return the student homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSthomework> getStudentHomeworkForCourseUnit(final String course, final Integer unit,
                                                               final boolean passedOnly, final String... hwTypes)
            throws SQLException {

        final List<RawSthomework> all = getStudentHomework();
        final int size = all.size();
        final List<RawSthomework> result = new ArrayList<>(size);

        for (final RawSthomework test : all) {
            if (test.course.equals(course) && test.unit.equals(unit)) {

                if (passedOnly && !"Y".equals(test.passed)) {
                    continue;
                }

                if (hwTypes == null || hwTypes.length == 0) {
                    result.add(test);
                } else {
                    for (final String type : hwTypes) {
                        if (test.hwType.equals(type)) {
                            result.add(test);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets the list of all records of homeworks taken by the student in a specified course, unit, and objective.
     *
     * @param course    the course ID
     * @param unit      the unit
     * @param objective the objective
     * @return the student homeworks
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSthomework> getStudentHomeworkForCourseUnitObjective(final String course, final Integer unit,
                                                                        final Integer objective) throws SQLException {

        final List<RawSthomework> all = getStudentHomework();
        final int size = all.size();
        final List<RawSthomework> result = new ArrayList<>(size);

        for (final RawSthomework test : all) {
            if (test.course.equals(course) && test.unit.equals(unit) && test.objective.equals(objective)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Forgets student homeworks. forcing a re-query on next access.
     */
    public void forgetStudentHomeworks() {

        this.studentHomeworks = null;
    }

    /**
     * Gets the list of all answers for homework taken by the student.
     *
     * @return the student homework answers
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawSthwqa> getStudentHomeworkAnswers() throws SQLException {

        if (this.studentHomeworkAnswers == null) {
            this.studentHomeworkAnswers = RawSthwqaLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentHomeworkAnswers;
    }

    /**
     * Forgets student homework answers. forcing a re-query on next access.
     */
    public void forgetStudentHomeworkAnswers() {

        this.studentHomeworkAnswers = null;
    }

    /**
     * Gets the list of all student course unit status objects.
     *
     * @return the student course unit status objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcunit> getStudentCourseUnits() throws SQLException {

        if (this.studentCourseUnits == null) {
            this.studentCourseUnits = RawStcunitLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentCourseUnits;
    }

    /**
     * Gets the list of all student course objective status objects.
     *
     * @return the student course objective status objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStcuobjective> getStudentCourseObjectives() throws SQLException {

        if (this.studentCourseObjectives == null) {
            this.studentCourseObjectives = RawStcuobjectiveLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentCourseObjectives;
    }

    /**
     * Gets the list of all student course objective status objects.
     *
     * @return the student course objective status objects
     * @throws SQLException if there is an error accessing the database
     */
    public RawStcuobjective getStudentCourseObjective(final String course, final Integer unit, final Integer objective)
            throws SQLException {

        final List<RawStcuobjective> all = getStudentCourseObjectives();
        RawStcuobjective result = null;

        for (final RawStcuobjective test : all) {
            if (test.course.equals(course) && test.unit.equals(unit) && test.objective.equals(objective)) {
                result = test;
                break;
            }
        }

        return result;
    }

    /**
     * Tests whether the instructional lecture for a unit and objective has been viewed by a student.
     *
     * @param courseId  the ID of the course
     * @param unit      the unit number
     * @param objective the objective number
     * @return {@code true} of the lecture has been viewed; {@code false} if not
     * @throws SQLException if there is an error accessing the database
     */
    public boolean hasLectureBeenViewed(final String courseId, final Integer unit, final Integer objective)
            throws SQLException {

        final RawStcuobjective row = getStudentCourseObjective(courseId, unit, objective);

        return row != null && row.lectureViewedDt != null;
    }

    /**
     * Forgets the list of student course objectives, forcing a re-query on next access.
     */
    public void forgetStudentCourseObjectives() {

        this.studentCourseObjectives = null;
    }

    /**
     * Gets the list of all mastery attempts by the student.
     *
     * @return the mastery attempts
     * @throws SQLException if there is an error accessing the database
     */
    public List<MasteryAttemptRec> getMasteryAttempts() throws SQLException {

        if (this.masteryAttempts == null) {
            this.masteryAttempts = MasteryAttemptLogic.INSTANCE.queryByStudent(this.cache, this.studentId);
        }

        return this.masteryAttempts;
    }

    /**
     * Gets the list of all mastery attempts by the student on a specified exam ID.
     *
     * @param examId the exam ID
     * @return the mastery attempts
     * @throws SQLException if there is an error accessing the database
     */
    public List<MasteryAttemptRec> getMasteryAttemptsByExamId(final String examId) throws SQLException {

        final List<MasteryAttemptRec> all = getMasteryAttempts();
        final List<MasteryAttemptRec> result = new ArrayList<>(10);

        for (final MasteryAttemptRec test : all) {
            if (test.examId.equals(examId)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Forgets student mastery attempts. forcing a re-query on next access.
     */
    public void forgetMasteryAttempts() {

        this.masteryAttempts = null;
    }

    /**
     * Gets the list of all course mastery status objects for the student.
     *
     * @return the course mastery status objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<StuCourseMasteryRec> getStudentCourseMastery() throws SQLException {

        if (this.studentCourseMastery == null) {
            this.studentCourseMastery = StuCourseMasteryLogic.INSTANCE.queryByStudent(this.cache,
                    this.studentId);
        }

        return this.studentCourseMastery;
    }

    /**
     * Gets the list of all unit mastery status objects for the student.
     *
     * @return the unit mastery status objects
     * @throws SQLException if there is an error accessing the database
     */
    public List<StuUnitMasteryRec> getStudentUnitMastery() throws SQLException {

        if (this.studentUnitMastery == null) {
            this.studentUnitMastery = StuUnitMasteryLogic.INSTANCE.queryByStudent(this.cache,
                    this.studentId);
        }

        return this.studentUnitMastery;
    }

    /**
     * Gets the list of all student course milestone overrides.
     *
     * @return the student course milestone overrides
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmilestone> getStudentMilestones() throws SQLException {

        if (this.studentMilestones == null) {
            this.studentMilestones = RawStmilestoneLogic.queryByStudent(this.cache, this.studentId);
            Collections.sort(this.studentMilestones);
        }

        return this.studentMilestones;
    }

    /**
     * Gets the list of all student course milestone overrides in a specified term with a specified pace and pace
     * track.
     *
     * @param term      the term whose milestones to retrieve
     * @param paceTrack the pace track whose milestones to retrieve
     * @return the course milestones
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStmilestone> getStudentMilestones(final TermKey term, final String paceTrack) throws SQLException {

        final List<RawStmilestone> all = getStudentMilestones();
        final int size = all.size();
        final List<RawStmilestone> result = new ArrayList<>(size);

        for (final RawStmilestone test : all) {
            if (test.termKey.equals(term) && test.paceTrack.equals(paceTrack)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Forgets the list of student course milestone overrides, forcing a re-query on next access.
     */
    public void forgetStudentMilestones() {

        this.studentMilestones = null;
    }

    /**
     * Gets the list of all student standard milestone overrides in the current term.
     *
     * @return the student standard milestone overrides
     * @throws SQLException if there is an error accessing the database
     */
    public List<StuStandardMilestoneRec> getStudentStandardMilestones() throws SQLException {

        if (this.studentStandardMilestones == null) {
            this.studentStandardMilestones = StuStandardMilestoneLogic.INSTANCE.queryByStudent(this.cache,
                    this.studentId);
        }

        return this.studentStandardMilestones;
    }

    /**
     * Gets the list of all student standard milestone overrides in the current term for a specified pace track and
     * pace.
     *
     * @param paceTrack the pace track
     * @param pace      the pace
     * @return the student standard milestone overrides
     * @throws SQLException if there is an error accessing the database
     */
    public List<StuStandardMilestoneRec> getStudentStandardMilestonesForPaceAndTrack(final String paceTrack,
                                                                                     final Integer pace)
            throws SQLException {

        final List<StuStandardMilestoneRec> all = getStudentStandardMilestones();
        final List<StuStandardMilestoneRec> result = new ArrayList<>(10);

        for (final StuStandardMilestoneRec test : all) {
            if (test.paceTrack.equals(paceTrack) && test.pace.equals(pace)) {
                result.add(test);
            }
        }

        return result;
    }

    /**
     * Gets the list of all deadline appeals by the student.
     *
     * @return the deadline appeals
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawPaceAppeals> getDeadlineAppeals() throws SQLException {

        if (this.deadlineAppeals == null) {
            this.deadlineAppeals = RawPaceAppealsLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.deadlineAppeals;
    }

    /**
     * Forgets the list of deadline appeals, forcing a re-query on next access.
     */
    public void forgetDeadlineAppeals() {

        this.deadlineAppeals = null;
    }

    /**
     * Gets the list of all deadline appeals by the student.
     *
     * @return the deadline appeals
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawStetext> getStudentETexts() throws SQLException {

        if (this.studentETexts == null) {
            this.studentETexts = RawStetextLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.studentETexts;
    }

    /**
     * Forgets the list of student e-texts, forcing a re-query on next access.
     */
    public void forgetStudentETexts() {

        this.studentETexts = null;
    }

    /**
     * Gets the list of all user's exams completed by the student.
     *
     * @return the user's exams
     * @throws SQLException if there is an error accessing the database
     */
    public List<RawUsers> getUsersExams() throws SQLException {

        if (this.usersExams == null) {
            this.usersExams = RawUsersLogic.queryByStudent(this.cache, this.studentId);
        }

        return this.usersExams;
    }

    /**
     * Forgets student user's exam attempts. forcing a re-query on next access.
     */
    public void forgetUsersExams() {

        this.usersExams = null;
    }
}
