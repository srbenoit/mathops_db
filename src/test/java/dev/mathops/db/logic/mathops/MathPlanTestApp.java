package dev.mathops.db.logic.mathops;

import com.formdev.flatlaf.FlatLightLaf;
import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.ui.UIUtilities;
import dev.mathops.commons.ui.layout.StackedBorderLayout;
import dev.mathops.db.Cache;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.StudentData;
import dev.mathops.db.logic.mathplan.MathPlanLogic;
import dev.mathops.db.logic.mathplan.StudentMathPlan;
import dev.mathops.db.logic.mathplan.StudentStatus;
import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.majors.Majors;
import dev.mathops.db.logic.mathplan.majors.MajorsCurrent;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;
import dev.mathops.db.logic.placement.PlacementLogic;
import dev.mathops.db.logic.placement.PlacementStatus;
import dev.mathops.db.old.rawrecord.RawFfrTrns;
import dev.mathops.db.old.rawrecord.RawRecordConstants;
import dev.mathops.db.old.rawrecord.RawStcourse;
import dev.mathops.db.old.rawrecord.RawStudent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A test harness to exercise the Math Plan logic.  This harness may update status data for the '888888888' user such as
 * placement attempts and results, transfer credit, completed coursework, and application term.
 */
public final class MathPlanTestApp implements Runnable, ActionListener {

    /** An action command to update student data. */
    private static final String CMD_UPDATE_STUDENT = "UPD_STU";

    /** An action command to update major selections. */
    private static final String CMD_UPDATE_MAJORS = "UPD_MAJ";

    /** A zero-length array used to convert a list to an array. */
    private static final Major[] EMPTY_MAJOR_ARRAY = new Major[0];

    /** A fake major to indicate "None" declared. */
    private static final Major NO_MAJOR = new Major(new int[]{-1}, new String[]{"NONE"}, "(None)", CoreConstants.EMPTY,
            ERequirement.CORE_ONLY, IdealFirstTerm.CORE_ONLY);

    /** The database profile through which to access the database. */
    private final Profile profile;

    /** The frame. */
    private JFrame frame;

    /** The field that shows the application term. */
    private JTextField applicationTermField;

    /** The dropdown to choose a declared major. */
    private JComboBox<Major> declaredMajor;

    /** A checkbox to indicate an unproctored MPT was used. */
    private JCheckBox unproctoredMPT;

    /** A checkbox to indicate a proctored MPT was used. */
    private JCheckBox proctoredMPT;

    /** A checkbox to indicate student placed out of M 100C (into MATH 117). */
    private JCheckBox place100C;

    /** A checkbox to indicate student placed out of M 117. */
    private JCheckBox place117;

    /** A checkbox to indicate student placed out of M 118. */
    private JCheckBox place118;

    /** A checkbox to indicate student placed out of M 124. */
    private JCheckBox place124;

    /** A checkbox to indicate student placed out of M 125. */
    private JCheckBox place125;

    /** A checkbox to indicate student placed out of M 126. */
    private JCheckBox place126;

    /** A checkbox to indicate student has transfer credit for M 002. */
    private JCheckBox xfer002;

    /** A checkbox to indicate student has transfer credit for M 101. */
    private JCheckBox xfer101;

    /** A checkbox to indicate student has transfer credit for M 117. */
    private JCheckBox xfer117;

    /** A checkbox to indicate student has transfer credit for M 118. */
    private JCheckBox xfer118;

    /** A checkbox to indicate student has transfer credit for M 124. */
    private JCheckBox xfer124;

    /** A checkbox to indicate student has transfer credit for M 125. */
    private JCheckBox xfer120;

    /** A checkbox to indicate student has transfer credit for M 125. */
    private JCheckBox xfer125;

    /** A checkbox to indicate student has transfer credit for M 126. */
    private JCheckBox xfer126;

    /** A checkbox to indicate student has transfer credit for M 126. */
    private JCheckBox xfer127;

    /** A checkbox to indicate student has transfer credit for M 141. */
    private JCheckBox xfer141;

    /** A checkbox to indicate student has transfer credit for M 155. */
    private JCheckBox xfer155;

    /** A checkbox to indicate student has transfer credit for M 156. */
    private JCheckBox xfer156;

    /** A checkbox to indicate student has transfer credit for M 160. */
    private JCheckBox xfer160;

    /** A checkbox to indicate student has transfer credit for AUCC core. */
    private JCheckBox xferAUCC;

    /** A set of checkboxes to indicate selected majors. */
    private Map<Major, JCheckBox> majorCheckboxes;

    /**
     * Private constructor to prevent instantiation.
     */
    private MathPlanTestApp() {

        final DatabaseConfig config = DatabaseConfig.getDefault();
        this.profile = config.getCodeProfile(Contexts.BATCH_PATH);
    }

    /**
     * Constructs the UI in the AWT event thread.
     *
     * <p>
     * The UI presents one pane with test student status that can affect the math plan (placement attempts and credit,
     * transfer credit, completed courses, and application term).  It presents a second pane with majors to select. When
     * a "compute" action is invoked, the plan is generated and presented in a third pane.
     */
    public void run() {

        this.frame = new JFrame("Math Plan Test Harness");
        this.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final JPanel content = new JPanel(new StackedBorderLayout());

        // Status data pane

        final JPanel statusPanel = new JPanel(new StackedBorderLayout());
        statusPanel.setBorder(new TitledBorder("Test Student (888888888) Status"));
        content.add(statusPanel, StackedBorderLayout.NORTH);

        final List<Major> majors = MajorsCurrent.INSTANCE.getMajors();
        final Major[] currentMajors = majors.toArray(EMPTY_MAJOR_ARRAY);
        final int numMajors = currentMajors.length;

        // Adjust labels on the exploratory studies majors
        for (int i = 0; i < numMajors; ++i) {
            final Major test = currentMajors[i];
            final int q = test.questionNumbers[0];

            if (q == 9000) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: (General)");
            } else if (q == 9001) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Arts, Humanities & Design");
            } else if (q == 9002) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Health, Life & Food Sciences");
            } else if (q == 9003) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Education & Teaching");
            } else if (q == 9004) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Land, Plant & Animal Sciences");
            } else if (q == 9005) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Env. & Natural Resources");
            } else if (q == 9006) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Physical Sciences & Engineering");
            } else if (q == 9007) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Global & Social Sciences");
            } else if (q == 9008) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Organization, Mgt., & Enterprise");
            } else {
                final String name = test.programName;
                String newName = name;

                if (newName.contains("&amp;")) {
                    newName = newName.replace("&amp;", "&");
                } else if (newName.contains(", and ")) {
                    newName = newName.replace(", and ", " & ");
                } else if (newName.contains(" and ")) {
                    newName = newName.replace(" and ", " & ");
                }

                if (newName.contains("Management")) {
                    newName = newName.replace("Management", "Mgt.");
                }
                if (newName.contains("Environmental")) {
                    newName = newName.replace("Environmental", "Env.");
                }
                if (newName.contains("Environment")) {
                    newName = newName.replace("Environment", "Env.");
                }
                if (newName.contains("Economics")) {
                    newName = newName.replace("Economics", "Econ.");
                }

                if (!newName.equals(name)) {
                    currentMajors[i] = test.cloneWithNewName(newName);
                }
            }
        }

        final Major[] addNone = new Major[currentMajors.length + 1];
        addNone[0] = NO_MAJOR;
        System.arraycopy(currentMajors, 0, addNone, 1, currentMajors.length);

        this.applicationTermField = new JTextField(6);
        this.declaredMajor = new JComboBox<>(addNone);
        final JLabel lbl11a = new JLabel("Application Term:");
        final JLabel lbl11b = new JLabel("   Declared Major:");
        final JPanel flow11 = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 2));
        flow11.add(lbl11a);
        flow11.add(this.applicationTermField);
        flow11.add(lbl11b);
        flow11.add(this.declaredMajor);
        statusPanel.add(flow11, StackedBorderLayout.NORTH);

        final JLabel lbl12a = new JLabel("Math Placement:");
        final JLabel lbl12b = new JLabel(" ");
        this.unproctoredMPT = new JCheckBox("Unproctored Attempt");
        this.proctoredMPT = new JCheckBox("Proctored Attempt");
        this.place100C = new JCheckBox("100C");
        this.place117 = new JCheckBox("117");
        this.place118 = new JCheckBox("118");
        this.place124 = new JCheckBox("124");
        this.place125 = new JCheckBox("125");
        this.place126 = new JCheckBox("126");
        final JPanel flow12 = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 2));
        flow12.add(lbl12a);
        flow12.add(this.unproctoredMPT);
        flow12.add(this.proctoredMPT);
        flow12.add(lbl12b);
        flow12.add(this.place100C);
        flow12.add(this.place117);
        flow12.add(this.place118);
        flow12.add(this.place124);
        flow12.add(this.place125);
        flow12.add(this.place126);
        statusPanel.add(flow12, StackedBorderLayout.NORTH);

        final JLabel lbl13a = new JLabel("Transfer Credit:");
        this.xfer002 = new JCheckBox("002");
        this.xfer101 = new JCheckBox("101");
        this.xfer117 = new JCheckBox("117");
        this.xfer118 = new JCheckBox("118");
        this.xfer124 = new JCheckBox("124");
        this.xfer120 = new JCheckBox("120");
        this.xfer125 = new JCheckBox("125");
        this.xfer126 = new JCheckBox("126");
        this.xfer127 = new JCheckBox("127");
        this.xfer141 = new JCheckBox("141");
        this.xfer155 = new JCheckBox("155");
        this.xfer156 = new JCheckBox("156");
        this.xfer160 = new JCheckBox("160");
        this.xferAUCC = new JCheckBox("AUCC");
        final JPanel flow13 = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 2));
        flow13.add(lbl13a);
        flow13.add(this.xfer002);
        flow13.add(this.xfer101);
        flow13.add(this.xfer117);
        flow13.add(this.xfer118);
        flow13.add(this.xfer124);
        flow13.add(this.xfer120);
        flow13.add(this.xfer125);
        flow13.add(this.xfer126);
        flow13.add(this.xfer127);
        flow13.add(this.xfer141);
        flow13.add(this.xfer155);
        flow13.add(this.xfer156);
        flow13.add(this.xfer160);
        flow13.add(this.xferAUCC);
        statusPanel.add(flow13, StackedBorderLayout.NORTH);

        final JButton updateStuButton = new JButton("Update Test Student Information");
        updateStuButton.setActionCommand(CMD_UPDATE_STUDENT);
        updateStuButton.addActionListener(this);
        final JPanel flow14 = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 2));
        flow14.add(updateStuButton);
        statusPanel.add(flow14, StackedBorderLayout.SOUTH);

        // Majors pane

        final JPanel majorsPanel = new JPanel(new StackedBorderLayout());
        majorsPanel.setBorder(new TitledBorder("Majors of interest"));
        content.add(majorsPanel, StackedBorderLayout.NORTH);

        this.majorCheckboxes = new HashMap<>(numMajors);
        final int n1 = (numMajors + 3) / 4;
        final int n2 = n1 * 2;
        final int n3 = n1 * 3;
        final JPanel col1 = new JPanel(new StackedBorderLayout());
        final JPanel col2 = new JPanel(new StackedBorderLayout());
        final JPanel col3 = new JPanel(new StackedBorderLayout());
        final JPanel col4 = new JPanel(new StackedBorderLayout());
        majorsPanel.add(col1, StackedBorderLayout.WEST);
        majorsPanel.add(col2, StackedBorderLayout.WEST);
        majorsPanel.add(col3, StackedBorderLayout.WEST);
        majorsPanel.add(col4, StackedBorderLayout.WEST);

        for (int i = 0; i < n1; ++i) {
            final Major major = currentMajors[i];
            final JCheckBox check = new JCheckBox(major.programName);
            this.majorCheckboxes.put(major, check);
            col1.add(check, StackedBorderLayout.NORTH);
        }

        for (int i = n1; i < n2; ++i) {
            final Major major = currentMajors[i];
            final JCheckBox check = new JCheckBox(major.programName);
            this.majorCheckboxes.put(major, check);
            col2.add(check, StackedBorderLayout.NORTH);
        }

        for (int i = n2; i < n3; ++i) {
            final Major major = currentMajors[i];
            final JCheckBox check = new JCheckBox(major.programName);
            this.majorCheckboxes.put(major, check);
            col3.add(check, StackedBorderLayout.NORTH);
        }

        for (int i = n3; i < numMajors; ++i) {
            final Major major = currentMajors[i];
            final JCheckBox check = new JCheckBox(major.programName);
            this.majorCheckboxes.put(major, check);
            col4.add(check, StackedBorderLayout.NORTH);
        }

        final JButton updateMajorsButton = new JButton("Update Selected Majors");
        updateMajorsButton.setActionCommand(CMD_UPDATE_MAJORS);
        updateMajorsButton.addActionListener(this);
        final JPanel flow22 = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 2));
        flow22.add(updateMajorsButton);
        majorsPanel.add(flow22, StackedBorderLayout.SOUTH);

        // Math Plan pane

        final JPanel planPanel = new JPanel(new StackedBorderLayout());
        planPanel.setBorder(new TitledBorder("Generated Math Plan"));
        content.add(planPanel, StackedBorderLayout.CENTER);

        updateStatus();

        this.frame.setContentPane(content);
        UIUtilities.packAndCenter(this.frame);
    }

    /**
     * Queries the database and updates status displays.
     */
    private void updateStatus() {

        try {
            final Cache cache = new Cache(this.profile);

            final StudentData studentData = cache.getStudent(RawStudent.TEST_STUDENT_ID);
            final RawStudent student = studentData.getStudentRecord();
            final String appTermStr;
            final Major declared;

            if (student == null) {
                appTermStr = CoreConstants.EMPTY;
                declared = NO_MAJOR;
            } else {
                appTermStr = student.aplnTerm == null ? CoreConstants.EMPTY : student.aplnTerm.shortString;
                declared = Majors.getMajorByProgramCode(student.programCode);
            }

            this.applicationTermField.setText(appTermStr);
            this.declaredMajor.setSelectedItem(declared);
            boolean usedUnproctored = false;
            boolean usedProctored = false;
            boolean has100C = false;
            boolean has117 = false;
            boolean has118 = false;
            boolean has124 = false;
            boolean has125 = false;
            boolean has126 = false;

            if (student != null) {
                final ZonedDateTime now = ZonedDateTime.now();
                final PlacementLogic placement = new PlacementLogic(cache, RawStudent.TEST_STUDENT_ID,
                        student.aplnTerm, now);
                final PlacementStatus status = placement.status;

                usedUnproctored = status.unproctoredUsed;
                usedProctored = status.proctoredAttempted;

                has100C = placement.status.clearedFor.contains(RawRecordConstants.MATH117);
                has117 = placement.status.placedOutOf.contains(RawRecordConstants.MATH117)
                         || placement.status.earnedCreditFor.contains(RawRecordConstants.MATH117);
                has118 = placement.status.placedOutOf.contains(RawRecordConstants.MATH118)
                         || placement.status.earnedCreditFor.contains(RawRecordConstants.MATH118);
                has124 = placement.status.placedOutOf.contains(RawRecordConstants.MATH124)
                         || placement.status.earnedCreditFor.contains(RawRecordConstants.MATH124);
                has125 = placement.status.placedOutOf.contains(RawRecordConstants.MATH125)
                         || placement.status.earnedCreditFor.contains(RawRecordConstants.MATH125);
                has126 = placement.status.placedOutOf.contains(RawRecordConstants.MATH126)
                         || placement.status.earnedCreditFor.contains(RawRecordConstants.MATH126);
            }

            this.unproctoredMPT.setSelected(usedUnproctored);
            this.proctoredMPT.setSelected(usedProctored);
            this.place100C.setSelected(has100C);
            this.place117.setSelected(has117);
            this.place118.setSelected(has118);
            this.place124.setSelected(has124);
            this.place125.setSelected(has125);
            this.place126.setSelected(has126);

            final StudentMathPlan plan = MathPlanLogic.queryPlan(cache, RawStudent.TEST_STUDENT_ID);
            final StudentStatus stuStatus = plan.stuStatus;

            boolean x002 = false;
            boolean x101 = false;
            boolean x117 = false;
            boolean x118 = false;
            boolean x124 = false;
            boolean x120 = false;
            boolean x125 = false;
            boolean x126 = false;
            boolean x127 = false;
            boolean x141 = false;
            boolean x155 = false;
            boolean x156 = false;
            boolean x160 = false;
            boolean xCore = false;
            for (final RawFfrTrns ffr : stuStatus.transferCredit) {
                final String id = ffr.course.replace("MATH ", "M ").replace("MATH", "M ");
                if ("M 002".equals(id)) {
                    x002 = true;
                } else if ("M 101".equals(id) || "M 130".equals(id)) {
                    x101 = true;
                } else if ("M 117".equals(id)) {
                    x117 = true;
                } else if ("M 118".equals(id)) {
                    x118 = true;
                } else if ("M 124".equals(id)) {
                    x124 = true;
                } else if ("M 120".equals(id)) {
                    x120 = true;
                } else if ("M 125".equals(id)) {
                    x125 = true;
                } else if ("M 126".equals(id)) {
                    x126 = true;
                } else if ("M 127".equals(id)) {
                    x127 = true;
                } else if ("M 141".equals(id)) {
                    x141 = true;
                } else if ("M 155".equals(id)) {
                    x155 = true;
                } else if ("M 156".equals(id)) {
                    x156 = true;
                } else if ("M 160".equals(id)) {
                    x160 = true;
                } else if ("M 105".equals(id) || "M 1++1B".equals(id) || "M 2++1B".equals(id)) {
                    xCore = true;
                }
            }
            for (final RawStcourse reg : stuStatus.completedCourses) {
                final String id = reg.course.replace("MATH ", "M ").replace("MATH", "M ");
                if ("M 002".equals(id)) {
                    x002 = true;
                } else if ("M 101".equals(id) || "M 130".equals(id)) {
                    x101 = true;
                } else if ("M 117".equals(id)) {
                    x117 = true;
                } else if ("M 118".equals(id)) {
                    x118 = true;
                } else if ("M 124".equals(id)) {
                    x124 = true;
                } else if ("M 120".equals(id)) {
                    x120 = true;
                } else if ("M 125".equals(id)) {
                    x125 = true;
                } else if ("M 126".equals(id)) {
                    x126 = true;
                } else if ("M 127".equals(id)) {
                    x127 = true;
                } else if ("M 141".equals(id)) {
                    x141 = true;
                } else if ("M 155".equals(id)) {
                    x155 = true;
                } else if ("M 156".equals(id)) {
                    x156 = true;
                } else if ("M 160".equals(id)) {
                    x160 = true;
                } else if ("M 105".equals(id) || "M 1++1B".equals(id) || "M 2++1B".equals(id)) {
                    xCore = true;
                }
            }

            this.xfer002.setSelected(x002);
            this.xfer101.setSelected(x101);
            this.xfer117.setSelected(x117);
            this.xfer118.setSelected(x118);
            this.xfer124.setSelected(x124);
            this.xfer120.setSelected(x120);
            this.xfer125.setSelected(x125);
            this.xfer126.setSelected(x126);
            this.xfer127.setSelected(x127);
            this.xfer141.setSelected(x141);
            this.xfer155.setSelected(x155);
            this.xfer156.setSelected(x156);
            this.xfer160.setSelected(x160);
            this.xferAUCC.setSelected(xCore);

            // TODO: Populate selected majors

        } catch (final SQLException ex) {
            Log.warning(ex);
            final String[] msg = {"Failed to update student status:", ex.getLocalizedMessage()};
            JOptionPane.showMessageDialog(this.frame, msg, "Math Plan Test Harness", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when an action is invoked.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final String cmd = e.getActionCommand();

        if (CMD_UPDATE_STUDENT.equals(cmd)) {
            Log.info("Updating student data");
        } else if (CMD_UPDATE_MAJORS.equals(cmd)) {
            Log.info("Updating majors");
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        FlatLightLaf.setup();
        DbConnection.registerDrivers();
        SwingUtilities.invokeLater(new MathPlanTestApp());
    }
}
