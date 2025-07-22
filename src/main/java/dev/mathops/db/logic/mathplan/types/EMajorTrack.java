package dev.mathops.db.logic.mathplan.types;

/**
 * Major Tracks, based on those defined by Exploratory Studies.
 */
public enum EMajorTrack {

    /** A major track. */
    ARTS_HUMANITIES_DESIGN("AHD", "Arts, Humanities and Design"),

    /** A major track. */
    ENVIRONMENTAL_NATURAL_RESOURCES("ENR", "Environmental and Natural Resources"),

    /** A major track. */
    GLOBAL_SOCIAL_SCIENCES("GSS", "Global and Social Sciences"),

    /** A major track. */
    HEALTH_LIFE_FOOD_SCIENCES("HLF", "Health, Life and Food Sciences"),

    /** A major track. */
    LAND_PLANT_ANIMAL_SCIENCES("LPA", "Land, Plant and Animal Sciences"),

    /** A major track. */
    ORGANIZATION_MANAGEMENT_ENTERPRISE("OME", "Organization, Management and Enterprise"),

    /** A major track. */
    MATH_PHYSICAL_SCIENCES_ENGINEERING("MSE", "Math, Physical Sciences and Engineering"),

    /** A major track. */
    EDUCATION_TEACHING("ET", "Education and Teaching");

    /** A short string key representing the track. */
    public final String key;

    /** A text label for the course. */
    public final String label;

    /**
     * Constructs a new {@code EMajorTrack}.
     *
     * @param theKey   a short string key to uniquely identify the track
     * @param theLabel a text label for the track
     */
    EMajorTrack(final String theKey, final String theLabel) {

        this.key = theKey;
        this.label = theLabel;
    }
}
