package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.TemporalUtils;
import dev.mathops.commons.log.Log;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.ESchema;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Login;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.logic.placement.PlacementStatus;
import dev.mathops.db.old.rawlogic.LogicUtils;
import dev.mathops.db.old.rawlogic.RawFfrTrnsLogic;
import dev.mathops.db.old.rawlogic.RawStmathplanLogic;
import dev.mathops.db.old.rawrecord.RawCourse;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawMpeCredit;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStudent;
import dev.mathops.db.old.schema.csubanner.ImplLiveCsuCredit;
import dev.mathops.db.old.schema.csubanner.ImplLiveTransferCredit;
import dev.mathops.db.rec.LiveCsuCredit;
import dev.mathops.db.rec.LiveTransferCredit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Business logic for the Math Plan.
 */
public class XMathPlanLogic {

    /** Object on which to synchronize member variable access. */
    private final Object synch;

    /** The database profile this module will use. */
    private final Profile profile;

    /** The cached courses. */
    private Map<String, RawCourse> courses = null;

    /**
     * Constructs a new {@code MathPlanLogic}.
     *
     * @param theProfile the database profile this module will use
     */
    public XMathPlanLogic(final Profile theProfile) {

        this.synch = new Object();
        this.profile = theProfile;
    }

    /**
     * Retrieves all completed courses on the student's record.
     *
     * @param studentId the student ID
     * @return the list of transfer credit entries; empty if none
     */
    public List<LiveCsuCredit> getCompletedCourses(final String studentId) {

        List<LiveCsuCredit> result;

        if (LogicUtils.isBannerDown()) {
            result = new ArrayList<>(0);
        } else {
            final Login liveLogin = this.profile.getLogin(ESchema.LIVE);

            try {
                final DbConnection conn = liveLogin.checkOutConnection();
                try {
                    result = ImplLiveCsuCredit.INSTANCE.query(conn, studentId);
                } finally {
                    liveLogin.checkInConnection(conn);
                }
            } catch (final Exception ex) {
                LogicUtils.indicateBannerDown();
                Log.warning(ex);
                result = new ArrayList<>(0);
            }
        }

        return result;
    }

    /**
     * Retrieves all transfer credit entries on the student's record.
     *
     * @param cache               the data cache
     * @param studentId           the student ID
     * @param reconcileLWithLocal if true, records that are found but don't exist in "ffr_trns" are inserted into
     *                            ffr_trns
     * @return the list of transfer credit entries; empty if none
     * @throws SQLException if there is an error accessing the database
     */
    public List<LiveTransferCredit> getStudentTransferCredit(final Cache cache, final String studentId,
                                                             final boolean reconcileLWithLocal) throws SQLException {

        List<LiveTransferCredit> result;

        if (studentId.startsWith("99")) {
            // The following will return test data - convert to "live" data.
            final List<RawFfrTrns> list = RawFfrTrnsLogic.queryByStudent(cache, studentId);
            final int size = list.size();
            result = new ArrayList<>(size);

            for (final RawFfrTrns tc : list) {
                final LiveTransferCredit ltc = new LiveTransferCredit(studentId, null, tc.course, null, null);
                result.add(ltc);
            }
        } else if (LogicUtils.isBannerDown()) {
            result = new ArrayList<>(0);
        } else {
            final Login liveLogin = this.profile.getLogin(ESchema.LIVE);
            final DbConnection bannerConn = liveLogin.checkOutConnection();
            try {
                result = ImplLiveTransferCredit.INSTANCE.query(bannerConn, studentId);
            } catch (final SQLException ex) {
                LogicUtils.indicateBannerDown();
                Log.warning(ex);
                result = new ArrayList<>(0);
            } finally {
                liveLogin.checkInConnection(bannerConn);
            }

            final Iterator<LiveTransferCredit> iter = result.iterator();
            while (iter.hasNext()) {
                final LiveTransferCredit row = iter.next();
                final String courseId = row.courseId;

                if (!courseId.startsWith("MATH1++")) {
                    if (courseId.startsWith("MATH") || courseId.startsWith("M ") || "STAT 100".equals(courseId)
                        || "STAT100".equals(courseId)) {
                        continue;
                    }
                }
                iter.remove();
            }

            if (reconcileLWithLocal) {
                final List<RawFfrTrns> existing = RawFfrTrnsLogic.queryByStudent(cache, studentId);

                for (final LiveTransferCredit live : result) {

                    RawFfrTrns currentRec = null;
                    for (final RawFfrTrns exist : existing) {
                        if (exist.course.equals(live.courseId)) {
                            currentRec = exist;
                            break;
                        }
                    }

                    String recGrade = live.grade;
                    if (recGrade.startsWith("T")) {
                        recGrade = recGrade.substring(1);
                        if (recGrade.length() > 2) {
                            recGrade = recGrade.substring(0, 2);
                        }
                    }

                    if (currentRec == null) {
                        Log.info("Adding ", live.courseId, " transfer credit for student ", studentId);
                        final RawFfrTrns toAdd = new RawFfrTrns(live.studentId, live.courseId, "T", LocalDate.now(),
                                null, recGrade);
                        RawFfrTrnsLogic.insert(cache, toAdd);
                    } else if (!Objects.equals(currentRec.grade, recGrade)) {
                        Log.info("Updating grade in ", live.courseId, " transfer credit for student ", studentId);
                        RawFfrTrnsLogic.updateGrade(cache, currentRec, recGrade);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Gets a map from the course numbers used in {@code RawCourse} objects to the corresponding full course objects.
     *
     * @return the map
     */
    public Map<String, RawCourse> getCourses() {

        synchronized (this.synch) {
            if (this.courses == null) {
                this.courses = new HashMap<>(100);

                // General AUCC-1B courses

                this.courses.put(MathPlanConstants.M_101, new RawCourse(
                        MathPlanConstants.M_101, MathPlanConstants.ZERO,
                        "Math in the Social Sciences (3 credits)",
                        MathPlanConstants.THREE, "N", "MATH 101", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.M_105, new RawCourse(
                        MathPlanConstants.M_105, MathPlanConstants.ZERO,
                        "Patterns of Phenomena (3 credits)",
                        MathPlanConstants.THREE, "N", "MATH 105", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.S_100, new RawCourse(
                        MathPlanConstants.S_100, MathPlanConstants.ZERO,
                        "Statistical Literacy (3 credits)",
                        MathPlanConstants.THREE, "N", "STAT 100", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.S_201, new RawCourse(
                        MathPlanConstants.S_201, MathPlanConstants.ZERO,
                        "General Statistics (3 credits)",
                        MathPlanConstants.THREE, "N", "STAT 201", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.S_204, new RawCourse(
                        MathPlanConstants.S_204, MathPlanConstants.ZERO,
                        "Statistics With Business Applications (3 credits)",
                        MathPlanConstants.THREE, "N", "STAT 204", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.F_200, new RawCourse(
                        MathPlanConstants.F_200, MathPlanConstants.ZERO,
                        "Personal Finance and Investing (3 credits)",
                        MathPlanConstants.THREE, "N", "FIN 200", null,
                        "N", "N"));

                // Precalculus

                this.courses.put(RawRecordConstants.M116,
                        new RawCourse(RawRecordConstants.M116, MathPlanConstants.ZERO,
                                "Precalculus Supplement for Success in Math (1 credit)",
                                MathPlanConstants.ONE, "N", "MATH 116", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M117,
                        new RawCourse(RawRecordConstants.M117, MathPlanConstants.FOUR,
                                "College Algebra in Context I (1 credit)",
                                MathPlanConstants.ONE, "Y", "MATH 117", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M118,
                        new RawCourse(RawRecordConstants.M118, MathPlanConstants.FOUR,
                                "College Algebra in Context II (1 credit)",
                                MathPlanConstants.ONE, "Y", "MATH 118", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M120,
                        new RawCourse(RawRecordConstants.M120, MathPlanConstants.ZERO,
                                "College Algebra (3 credit)",
                                MathPlanConstants.THREE, "Y", "MATH 120", null,
                                "N", "N"));
                this.courses.put(RawRecordConstants.M124,
                        new RawCourse(RawRecordConstants.M124, MathPlanConstants.FOUR,
                                "Logarithmic and Exponential Functions (1 credit)",
                                MathPlanConstants.ONE, "Y", "MATH 124", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M125,
                        new RawCourse(RawRecordConstants.M125, MathPlanConstants.FOUR,
                                "Numerical Trigonometry (1 credit)",
                                MathPlanConstants.ONE, "Y", "MATH 125", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M126,
                        new RawCourse(RawRecordConstants.M126, MathPlanConstants.FOUR,
                                "Analytic Trigonometry (1 credit)",
                                MathPlanConstants.ONE, "Y", "MATH 126", null,
                                "N", "Y"));
                this.courses.put(RawRecordConstants.M127,
                        new RawCourse(RawRecordConstants.M127, MathPlanConstants.ZERO,
                                "Precalculus (4 credit)",
                                MathPlanConstants.FOUR, "Y", "MATH 127", null,
                                "N", "N"));

                // Other Math courses

                this.courses.put(MathPlanConstants.M_141, new RawCourse(
                        MathPlanConstants.M_141, MathPlanConstants.ZERO,
                        "Calculus in Management Sciences (3 credits)",
                        MathPlanConstants.THREE, "N", "MATH 141", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.M_155, new RawCourse(
                        MathPlanConstants.M_155, MathPlanConstants.ZERO,
                        "Calculus for Biological Scientists I (4 credits)",
                        MathPlanConstants.FOUR, "N", "MATH 155", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.M_156, new RawCourse(
                        MathPlanConstants.M_156, MathPlanConstants.ZERO,
                        "Mathematics for Computational Science I (4 credits)",
                        MathPlanConstants.FOUR, "N", "MATH 156", null,
                        "N", "N"));
                this.courses.put(MathPlanConstants.M_160, new RawCourse(
                        MathPlanConstants.M_160, MathPlanConstants.ZERO,
                        "Calculus for Physical Scientists I (4 credits)",
                        MathPlanConstants.FOUR, "N", "MATH 160", null,
                        "N", "N"));
            }

            return Collections.unmodifiableMap(this.courses);
        }
    }

    /**
     * Stores a set of profile answers and updates the cached student plan based on the new profile responses.
     *
     * @param cache           the data cache
     * @param student         the student
     * @param pageId          the page ID
     * @param questions       the question numbers
     * @param answers         the answers
     * @param now             the date/time to consider "now"
     * @param loginSessionTag a unique tag for a login session
     * @throws SQLException if there is an error accessing the database
     */
    public void storeMathPlanResponses(final Cache cache, final RawStudent student, final String pageId,
                                       final List<Integer> questions, final List<String> answers,
                                       final ZonedDateTime now, final long loginSessionTag) throws SQLException {

        final LocalDateTime when = now.toLocalDateTime();
        final Integer finishTime = Integer.valueOf(TemporalUtils.minuteOfDay(when));

        final String aplnTermStr = student.aplnTerm == null ? null : student.aplnTerm.shortString;

        // Dummy record to test for existing
        RawStmathplan resp = new RawStmathplan(student.stuId, student.pidm, aplnTermStr, pageId, when.toLocalDate(),
                MathPlanConstants.ZERO, CoreConstants.EMPTY, finishTime, Long.valueOf(loginSessionTag));

        // Query for any existing answers with the same date and finish time
        final List<RawStmathplan> latest = RawStmathplanLogic.queryLatestByStudentPage(cache, student.stuId, pageId);
        final LocalDate today = now.toLocalDate();
        final Integer minutes = resp.finishTime;
        final Iterator<RawStmathplan> iter = latest.iterator();
        while (iter.hasNext()) {
            final RawStmathplan test = iter.next();
            if (today.equals(test.examDt) && minutes.equals(test.finishTime)) {
                continue;
            }
            iter.remove();
        }

        final int count = Math.min(questions.size(), answers.size());

        for (int i = 0; i < count; ++i) {
            final String ans = answers.get(i);
            final Integer questionNum = questions.get(i);

            resp = new RawStmathplan(student.stuId, student.pidm, aplnTermStr, pageId, when.toLocalDate(), questionNum,
                    ans, finishTime, Long.valueOf(loginSessionTag));

            // See if there is an existing answer at the same time
            RawStmathplan existing = null;
            for (final RawStmathplan test : latest) {
                if (test.surveyNbr.equals(questionNum)) {
                    existing = test;
                    break;
                }
            }

            if (ans == null) {
                // Old record had answer, new does not, so delete old record
                if (existing != null) {
                    RawStmathplanLogic.delete(cache, existing);
                }
            } else {
                RawStmathplanLogic.insert(cache, resp);
            }
        }
    }

    /**
     * Main method to gather and print test data.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();

        final DatabaseConfig config = DatabaseConfig.getDefault();
        final Profile profile = config.getCodeProfile(Contexts.BATCH_PATH);
        final Cache cache = new Cache(profile);

        try {
            final String stuId = "888888888";
            final StudentData studentData = cache.getStudent(stuId);
            final RawStudent stu = studentData.getStudentRecord();
            final String screenName = stu.getScreenName();

            final XMathPlanLogic logic = new XMathPlanLogic(profile);
            final ZonedDateTime now = ZonedDateTime.now();
            final XMathPlanStudentData data = new XMathPlanStudentData(cache, stu, logic, now, 12345L, false);

            final PlacementStatus mptStatus = data.placementStatus;
            final String canUseUnproctored = mptStatus.allowedToUseUnproctored ? "Y" : "N";
            final String isPlacementAttempted = mptStatus.placementAttempted ? "Y" : "N";
            final String attemptsUsedStr = Integer.toString(mptStatus.attemptsUsed);
            final String attemptsRemainingStr = Integer.toString(mptStatus.attemptsRemaining);
            final String isUnproctoredUsed = mptStatus.unproctoredUsed ? "Y" : "N";

            Log.info("Student ", stuId, " (", screenName, ") placement status: ");
            Log.info("    Available local proctored exams:  ", mptStatus.availableLocalProctoredIds);
            Log.info("    Available online proctored exams: ", mptStatus.availableOnlineProctoredIds);
            Log.info("    Available unproctored exams:      ", mptStatus.availableUnproctoredIds);
            Log.info("    Allowed to use unproctored:       ", canUseUnproctored);
            Log.info("    Why unproctored allowed:          ", mptStatus.whyUnproctoredAllowed);
            Log.info("    Why unproctored unavailable:      ", mptStatus.whyUnproctoredUnavailable);
            Log.info("    Has attempted placement:          ", isPlacementAttempted);
            Log.info("    Placement attempts used:          ", attemptsUsedStr);
            Log.info("    Placement attempts remaining:     ", attemptsRemainingStr);
            Log.info("    Unproctored attempt used:         ", isUnproctoredUsed);
            Log.info("    Why proctored unavailable:        ", mptStatus.whyProctoredUnavailable);
            Log.info("    Short why proctored unavailable:  ", mptStatus.shortWhyProctoredUnavailable);
            Log.info("    Unproctored date range:           ", mptStatus.unproctoredDateRanges);
            Log.info("    Placed out of:                    ", mptStatus.placedOutOf);
            Log.info("    Cleared for:                      ", mptStatus.clearedFor);
            Log.info("    Earned credit for:                ", mptStatus.earnedCreditFor);

            final String viewedExisting = data.viewedExisting ? "Y" : "N";
            final String checkedRecommendation = data.checkedOnlyRecommendation ? "Y" : "N";
            final Map<Integer, RawStmathplan> majorResponses = data.getMajorProfileResponses();
            final Map<Integer, RawStmathplan> intentions = data.getIntentions();
            final List<LiveCsuCredit> completed = data.getCompletedCourses();
            final List<LiveTransferCredit> transferCredit = data.getLiveTransferCredit();
            final List<RawMpeCredit> placementCred = data.getPlacementCredit();
            final List<Major> majors = data.getMajors();
            final Set<String> eligibleFor = data.getCanRegisterFor();
            final Set<String> eligibleForDoesNotHave = data.getCanRegisterForAndDoesNotHave();
            final String bOrBetter124 = data.hasBOrBetterIn124() ? "Y" : "N";
            final String bOrBetter126 = data.hasBOrBetterIn126() ? "Y" : "N";
            final double creditsOfCoreCompleted = data.getCreditsOfCoreCompleted();
            final String coreCompleted = Double.toString(creditsOfCoreCompleted);

            Log.info("Student ", stuId, " (", screenName, ") Math Plan status: ");
            Log.info("    Major Profile Responses:       ", majorResponses);
            Log.info("    Viewed existing:               ", viewedExisting);
            Log.info("    Checked 'only recommendation': ", checkedRecommendation);
            Log.info("    Stated Intentions:             ", intentions);
            Log.info("    Completed:                     ", completed);
            Log.info("    Transfer:                      ", transferCredit);
            Log.info("    Math Placement credit:         ", placementCred);
            Log.info("    Majors:                        ", majors);
            Log.info("    Recommended Eligibility:       ", data.recommendedEligibility);
            Log.info("    Credits of Core Completed:     ", coreCompleted);
            Log.info("    Next step:                     ", data.nextStep);
            Log.info("    Can register for:              ", eligibleFor);
            Log.info("    Can register for doesn't have: ", eligibleForDoesNotHave);
            Log.info("    B- or better in MATH 124:      ", bOrBetter124);
            Log.info("    B- or better in MATH 126:      ", bOrBetter126);

        } catch (final SQLException ex) {
            Log.warning(ex);
        }
    }
}
