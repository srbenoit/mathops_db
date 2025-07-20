package dev.mathops.db.logic.mathops;

import dev.mathops.commons.CoreConstants;
import dev.mathops.commons.ui.UIUtilities;
import dev.mathops.commons.ui.layout.StackedBorderLayout;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;
import dev.mathops.db.logic.mathplan.majors.Major;
import dev.mathops.db.logic.mathplan.majors.MajorsCurrent;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    /** The database profile through which to access the database. */
    private final Profile profile;

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

        final JFrame frame = new JFrame("Math Plan Test Harness");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Environment & Natural Resources");
            } else if (q == 9006) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Physical Sciences & Engineering");
            } else if (q == 9007) {
                currentMajors[i] = test.cloneWithNewName("Exploratory Studies: Global & Social Sciences");
            } else if (q == 9008) {
                currentMajors[i] = test.cloneWithNewName(
                        "Exploratory Studies: Organization, Management, and Enterprise");
            } else {
                final String name = test.programName;
                if (name.contains("&amp;")) {
                    final String newName = name.replace("&amp;", "&");
                    currentMajors[i] = test.cloneWithNewName(newName);
                } else if (name.contains(" and ")) {
                    final String newName = name.replace(" and ", " & ");
                    currentMajors[i] = test.cloneWithNewName(newName);
                }
            }
        }

        final Major[] addNone = new Major[currentMajors.length + 1];
        addNone[0] = new Major(new int[]{-1}, new String[]{"NONE"}, "(None)", CoreConstants.EMPTY,
                ERequirement.CORE_ONLY, IdealFirstTerm.CORE_ONLY);
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

        frame.setContentPane(content);
        UIUtilities.packAndCenter(frame);
    }

    /**
     * Queries the database and updates status displays.
     */
    public void updateStatus() {

    }

    /**
     * Called when an action is invoked.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        final String cmd = e.getActionCommand();

    }

    /**
     * Main method to launch the application.
     *
     * @param args command-line arguments
     */
    public static void main(final String... args) {

        DbConnection.registerDrivers();
        SwingUtilities.invokeLater(new MathPlanTestApp());
    }
}
