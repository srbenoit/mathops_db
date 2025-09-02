package dev.mathops.db.logic;

import dev.mathops.db.schema.legacy.rec.RawSpecialStus;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A container for special categories to which a student belongs.
 */
public enum SpecialCategoriesLogic {
    ;

    /**
     * Gets all active special categories.
     *
     * @param specials all special student records for the student
     * @param today    the current date
     * @return the list of categories
     */
    public static List<RawSpecialStus> getActive(final Collection<RawSpecialStus> specials,
                                                 final ChronoLocalDate today) {

        final int size = specials.size();
        final List<RawSpecialStus> result = new ArrayList<>(size);

        for (final RawSpecialStus test : specials) {
            final LocalDate start = test.startDt;

            if (start == null || !start.isAfter(today)) {
                final LocalDate end = test.endDt;

                if (end == null || !end.isBefore(today)) {
                    result.add(test);
                }
            }
        }

        return result;
    }
}
