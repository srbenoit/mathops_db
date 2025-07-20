package dev.mathops.db.logic.mathops;

import dev.mathops.commons.ui.UIUtilities;
import dev.mathops.commons.ui.layout.StackedBorderLayout;
import dev.mathops.db.Contexts;
import dev.mathops.db.DbConnection;
import dev.mathops.db.cfg.DatabaseConfig;
import dev.mathops.db.cfg.Profile;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * A test harness to exercise the Math Plan logic.  This harness may update status data for the '888888888' user such as
 * placement attempts and results, transfer credit, completed coursework, and application term.
 */
public final class MathPlanTestApp implements Runnable {

    /** The database profile through which to access the database. */
    private final Profile profile;

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

        // Majors pane
        final JPanel majorsPanel = new JPanel(new StackedBorderLayout());
        majorsPanel.setBorder(new TitledBorder("Majors of interest"));
        content.add(majorsPanel, StackedBorderLayout.NORTH);

        // Math Plan pane
        final JPanel planPanel = new JPanel(new StackedBorderLayout());
        planPanel.setBorder(new TitledBorder("Generated Math Plan"));
        content.add(planPanel, StackedBorderLayout.NORTH);

        frame.setContentPane(content);
        UIUtilities.packAndCenter(frame);
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
