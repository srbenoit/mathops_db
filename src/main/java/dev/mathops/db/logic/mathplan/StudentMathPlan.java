package dev.mathops.db.logic.mathplan;

import dev.mathops.commons.log.Log;
import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.types.ENextStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Math Plan for a single student.
 */
public class StudentMathPlan {

    /** The student status (completed courses, transfer credit, placement credit). */
    public final StudentStatus stuStatus;

    /** The student's majors of interest. */
    public final List<Major> majorsOfInterest;

    /** the collective requirements the student should satisfy based on their selected majors. */
    public final Requirements requirements;

    /**
     * A student's "recommended trajectory" through Mathematics courses to try to satisfy the requirements for any of
     * their selected majors.
     */
    public final RecommendedTrajectory trajectory;

    /** The student's recommended next steps. */
    public final NextSteps nextSteps;

    /**
     * Constructs a new {@code StudentMathPlan} from a collection of major requirements.  This constructor is used when
     * a Math Plan is being created for the first time.
     *
     * @param theMajorsOfInterest the student's majors of interest
     * @param theStuStatus        the student's current status (completed courses, transfer credit, placement credit)
     * @param theRequirements     the collective requirements the student should satisfy based on their selected majors
     */
    StudentMathPlan(final Collection<Major> theMajorsOfInterest, final StudentStatus theStuStatus,
                    final Requirements theRequirements) {

        this.majorsOfInterest = new ArrayList<>(theMajorsOfInterest);
        this.stuStatus = theStuStatus;
        this.requirements = theRequirements;

        if (theMajorsOfInterest == null || theMajorsOfInterest.isEmpty()) {
            this.trajectory = null;
            this.nextSteps = new NextSteps(ENextStep.UNDETERMINED);
        } else if (theRequirements.coreOnly) {
            this.trajectory = null;
            this.nextSteps = new NextSteps(ENextStep.MSG_PLACEMENT_NOT_NEEDED);
        } else {
            this.trajectory = new RecommendedTrajectory(theStuStatus, theRequirements);
            this.nextSteps = new NextSteps(theStuStatus, this.trajectory);
        }
    }
}