package dev.mathops.db.logic.mathplan;

import dev.mathops.db.Cache;
import dev.mathops.db.old.rawlogic.RawFfrTrnsLogic;
import dev.mathops.db.old.rawlogic.RawStcourseLogic;
import dev.mathops.db.old.rawlogic.RawStmpeLogic;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.old.rawrecord.RawStmathplan;
import dev.mathops.db.old.rawrecord.RawStmpe;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * A container for status of Math Placement relative to a Math Plan.
 */
public class XMathPlanPlacementStatus {

    /** True if student has completed the Math Plan and plan indicates placement is needed. */
    public final boolean isPlacementNeeded;

    /** True if placement requirement has been satisfied (even if Math Plan indicates placement is not needed). */
    public final boolean isPlacementComplete;

    /**
     * If placement requirement has been satisfied, how.  One of the following:
     * <ul>
     *     <li>Math Placement Tool Completed</li>
     *     <li>Placement requirement satisfied by test score</li>
     *     <li>Placement requirement satisfied by transfer credit</li>
     *     <li>Placement requirement satisfied by course credit</li>
     * </ul>
     */
    public final XEHowSatisfiedPlacement howSatisfied;

    /**
     * Constructs a new {@code MathPlanPlacementStatus}
     *
     * @param placementNeeded    true if student has completed the Math Plan and plan indicates placement is needed
     * @param placementSatisfied true if placement requirement has been satisfied (even if Math Plan indicates placement
     *                           is not needed)
     * @param how                f placement requirement has been satisfied, how.  One of the following
     */
    public XMathPlanPlacementStatus(final boolean placementNeeded, final boolean placementSatisfied,
                                    final XEHowSatisfiedPlacement how) {

        this.isPlacementNeeded = placementNeeded;
        this.isPlacementComplete = placementSatisfied;
        this.howSatisfied = how;
    }

    /**
     * Tests whether a student has completed (or needs to complete) the Math Placement process.
     *
     * @param cache     the data cache
     * @param studentId the student ID
     * @return the student's math placement status
     * @throws SQLException if there is an error accessing the database
     */
    public static XMathPlanPlacementStatus getMathPlacementStatus(final Cache cache, final String studentId)
            throws SQLException {

        boolean planSaysPlacementNeeded;
        boolean satisfiedByPlacement = false;
        boolean satisfiedByTransfer = false;
        boolean satisfiedByCourse = false;

        final Map<Integer, RawStmathplan> planResponses = XMathPlanStudentData.getMathPlanResponses(cache, studentId,
                MathPlanConstants.PLAN_PROFILE);

        // See of their latest recommendation was "3 cr. of Core Math*"
        final RawStmathplan rec = planResponses.get(dev.mathops.db.logic.mathplan.MathPlanConstants.TWO);

        if (rec == null || rec.stuAnswer == null) {
            planSaysPlacementNeeded = true;
        } else if (rec.stuAnswer.startsWith("(none)")) {
            planSaysPlacementNeeded = false;
        } else if (rec.stuAnswer.startsWith("3 cr. of Core")) {
            planSaysPlacementNeeded = false;
        } else if (rec.stuAnswer.startsWith("2 cr. of Core")) {
            planSaysPlacementNeeded = false;
        } else {
            planSaysPlacementNeeded = !rec.stuAnswer.startsWith("1 cr. of Core");
        }

        final List<RawStmpe> attempts = RawStmpeLogic.queryLegalByStudent(cache, studentId);
        if (!attempts.isEmpty()) {
            satisfiedByPlacement = true;
            planSaysPlacementNeeded = false;
        }

        if (!satisfiedByPlacement) {
            final List<RawFfrTrns> xfers = RawFfrTrnsLogic.queryByStudent(cache, studentId);
            for (final RawFfrTrns xfer : xfers) {
                if (RawRecordConstants.M117.equals(xfer.course)
                    || RawRecordConstants.M118.equals(xfer.course)
                    || RawRecordConstants.M124.equals(xfer.course)
                    || RawRecordConstants.M125.equals(xfer.course)
                    || RawRecordConstants.M126.equals(xfer.course)
                    || "M 160".equals(xfer.course)
                    || "M 155".equals(xfer.course)
                    || "M 141".equals(xfer.course)
                    || "M 120".equals(xfer.course)
                    || "M 127".equals(xfer.course)
                    || "M 161".equals(xfer.course)
                    || RawRecordConstants.M002.equals(xfer.course)) {
                    // M 002 is a community college course that clears prereqs for 117
                    satisfiedByTransfer = true;
                    planSaysPlacementNeeded = false;
                    break;
                }
            }
        }

        if (!satisfiedByTransfer) {
            final List<RawStcourse> regs = RawStcourseLogic.queryByStudent(cache, studentId, false, false);
            for (final RawStcourse reg : regs) {
                if ("Y".equals(reg.completed)) {
                    if (RawRecordConstants.M117.equals(reg.course)
                        || RawRecordConstants.M118.equals(reg.course)
                        || RawRecordConstants.M124.equals(reg.course)
                        || RawRecordConstants.M125.equals(reg.course)
                        || RawRecordConstants.M126.equals(reg.course)
                        || RawRecordConstants.MATH117.equals(reg.course)
                        || RawRecordConstants.MATH118.equals(reg.course)
                        || RawRecordConstants.MATH124.equals(reg.course)
                        || RawRecordConstants.MATH125.equals(reg.course)
                        || RawRecordConstants.MATH126.equals(reg.course)) {
                        satisfiedByCourse = true;
                        planSaysPlacementNeeded = false;
                        break;
                    }
                }
            }
        }

        final XMathPlanPlacementStatus result;

        if (satisfiedByPlacement) {
            result = new XMathPlanPlacementStatus(planSaysPlacementNeeded, true,
                    XEHowSatisfiedPlacement.MATH_PLACEMENT_COMPLETED);
        } else if (satisfiedByTransfer) {
            result = new XMathPlanPlacementStatus(planSaysPlacementNeeded, true, XEHowSatisfiedPlacement.TRANSFER_CREDIT);
        } else if (satisfiedByCourse) {
            result = new XMathPlanPlacementStatus(planSaysPlacementNeeded, true, XEHowSatisfiedPlacement.COURSE_CREDIT);
        } else {
            result = new XMathPlanPlacementStatus(planSaysPlacementNeeded, false, null);
        }

        return result;
    }
}
