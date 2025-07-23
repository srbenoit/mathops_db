package dev.mathops.db.logic.mathplan.majors;

import dev.mathops.db.logic.mathplan.MathPlanConstants;
import dev.mathops.db.logic.mathplan.types.EMajorTrack;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;

import java.util.List;

/**
 * An immutable record of all "deactivated" majors with historic program information.  Students may not select these in
 * the Math Plan, but if they have one of these majors declared, the data can be used to assess the student's Math
 * requirements.
 *
 * <p>
 * This class also stores "Fake" program codes for which we have no data, but which students may have
 */
public final class MajorsDeactivated extends MajorsBase {

    /** The single instance. */
    public static final MajorsDeactivated INSTANCE = new MajorsDeactivated();

    /** An empty track array. */
    private static final EMajorTrack[] EMPTY_TRACK_ARRAY = {};

    /**
     * Constructs a new {@code MajorsDeactivated}.
     */
    private MajorsDeactivated() {

        super(150);

        final List<Major> majors = innerGetMajors();

        // *** Last reviewed July 16, 2025, from the 2025-2026 Catalog. ***

        // ================================
        // College of Agricultural Sciences
        // ================================

        // *** Major in Agricultural Business

        // FAKE: AGBU-DD-BS, Agricultural Business - Dual Degree
        final Major mAGBU = new Major(
                new int[]{-1},
                new String[]{"AGBU-DD-BS"},
                EMPTY_TRACK_ARRAY,
                "Agricultural Business",
                MathPlanConstants.PGMS + "agricultural-business/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mAGBU);

        // *** Major in Environmental and Natural Resource Economics

        // FAKE: ENRE-DD-BS, Environmental and Natural Resource Economics - Dual degree
        final Major mENRE = new Major(
                new int[]{-1},
                new String[]{"ENRE-DD-BS"},
                EMPTY_TRACK_ARRAY,
                "Environmental and Natural Resource Economics",
                MathPlanConstants.PGMS + "environmental-and-natural-resource-economics/",
                ERequirement.M_117_118_124_141,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mENRE);

        // *** Major in Environmental Horticulture

        // 1041: ENHR-LNBZ-BS, Environmental Horticulture - Landscape Business (DEACTIVATED)
        final Major mENHR = new Major(
                new int[]{1041},
                new String[]{"ENHR-LNBZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Environmental Horticulture (Landscape Business)",
                MathPlanConstants.PGMS + "environmental-horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mENHR);

        // *** Major in Horticulture

        // 1061: HORT-FLOZ-BS, Horticulture - Floriculture (DEACTIVATED)
        final Major mHORT1 = new Major(
                new int[]{1061},
                new String[]{"HORT-FLOZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Horticulture (Floriculture)",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHORT1);

        // 1065: HORT-HTHZ-BS, Horticulture - Horticultural Therapy (DEACTIVATED)
        final Major mHORT2 = new Major(
                new int[]{1065},
                new String[]{"HORT-HTHZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Horticulture (Horticultural Therapy)",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHORT2);

        // FAKE: HORT-DHBZ-BS, Horticulture - ???

        final Major mHORT3 = new Major(
                new int[]{-1},
                new String[]{"HORT-DHBZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Horticulture",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHORT3);

        // *** Major in Soil and Crop Sciences

        // 1081: SOCR-APMZ-BS, Soil and Crop Sciences - Agronomic Production Management (DEACTIVATED)
        final Major mSOCR1 = new Major(
                new int[]{1081},
                new String[]{"SOCR-APMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (Agronomic Production Management)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR1);

        // 1082: SOCR-APIZ-BS, Soil and Crop Sciences - Applied Information Technology (DEACTIVATED)
        final Major mSOCR2 = new Major(
                new int[]{1082},
                new String[]{"SOCR-APIZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (Applied Information Technology)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR2);

        // 1083: SOCR-ISCZ-BS, Soil and Crop Sciences - International Soil and Crop Sciences (DEACTIVATED)
        final Major mSOCR3 = new Major(
                new int[]{1083},
                new String[]{"SOCR-ISCZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (International Soil and Crop Sciences)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR3);

        // 1084: SOCR-PBGZ-BS, Soil and Crop Sciences - Plant Biotechnology, Genetics and Breeding (DEACTIVATED)
        final Major mSOCR4 = new Major(
                new int[]{1084},
                new String[]{"SOCR-PBGZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (Plant Biotechnology, Genetics and Breeding)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR4);

        // 1085: SOCR-SOEZ-BS, Soil and Crop Sciences - Soil Ecology (DEACTIVATED)
        final Major mSOCR5 = new Major(
                new int[]{1085},
                new String[]{"SOCR-SOEZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (Soil Ecology)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR5);

        // 1086: SOCR-SRNZ-BS, Soil and Crop Sciences - Soil Restoration and Conservation (DEACTIVATED)
        final Major mSOCR6 = new Major(
                new int[]{1086},
                new String[]{"SOCR-SRNZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences (Soil Restoration and Conservation)",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR6);

        // FAKE: SOCR-DSAZ-BS, Soil and Crop Sciences - ???
        final Major mSOCR7 = new Major(
                new int[]{-1},
                new String[]{"SOCR-DSAZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR7);

        // ===================
        // College of Business
        // ===================

        // *** Major in Business Administration
        // 2007: BUSA-OIMZ-BS, Business Administration - Organization and Innovation Management (DEACTIVATED)
        final Major mBUSA1 = new Major(
                new int[]{2007},
                new String[]{"BUSA-OIMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Business Administration (Organization and Innovation Management)",
                MathPlanConstants.PGMS + "business-administration/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_41);
        majors.add(mBUSA1);

        // FAKE: BUSA-DACZ-BS, Business Administration
        // FAKE: BUSA-OIMZ-BS, Business Administration
        final Major mBUSA2 = new Major(
                new int[]{-1, -1},
                new String[]{"BUSA-DACZ-BS", "BUSA-OIMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Business Administration",
                MathPlanConstants.PGMS + "business-administration/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_41);
        majors.add(mBUSA2);

        // ======================
        // College of Engineering
        // ======================

        // *** Major in Computer Engineering
        // 3034: CPEG-VICZ-BS, Computer Engineering - VLSI and Integrated Circuits (DEACTIVATED)

        final Major mCPEG = new Major(
                new int[]{3034},
                new String[]{"CPEG-VICZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Computer Engineering (VLSI and Integrated Circuits)",
                MathPlanConstants.PGMS + "computer-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCPEG);

        // *** Major in Engineering Science

        // 3050: EGSC-EGPZ-BS, Engineering Science (DEACTIVATED)
        final Major mEGSC = new Major(
                new int[]{3050},
                new String[]{"EGSC-BS"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGSC);

        // 3051: EGSC-EGPZ-BS, Engineering Science - Engineering Physics (DEACTIVATED)
        final Major mEGSC1 = new Major(
                new int[]{3051},
                new String[]{"EGSC-EGPZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Engineering Physics)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGSC1);

        // 3052: EGSC-SPEZ-BS, Engineering Science - Space Engineering (DEACTIVATED)
        final Major mEGSC2 = new Major(
                new int[]{3052},
                new String[]{"EGSC-SPEZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Space Engineering)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGSC2);

        // 3053: EGSC-TCEZ-BS, Engineering Science - Teacher Education (DEACTIVATED)
        final Major mEGSC3 = new Major(
                new int[]{3053},
                new String[]{"EGSC-TCEZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Teacher Education)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGSC3);

        // *** Dual-Degree programs in Engineering Science

        // 3060: EGIS-DUAL, Engineering Science Dual Degree (DEACTIVATED)
        final Major mEGIS = new Major(
                new int[]{3060},
                new String[]{"EGIS-DUAL"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Dual Degree)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGIS);

        // 3061: ILES-BA, Engineering Science Dual Degree - With Interdisciplinary Liberal Arts (DEACTIVATED)
        final Major mEGIS2 = new Major(
                new int[]{3061},
                new String[]{"ILES-BA"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Dual Degree - With Interdisciplinary Liberal Arts)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGIS2);

        // 3062: EGIS-BS, Engineering Science Dual Degree - With International Studies (DEACTIVATED)
        final Major mEGIS3 = new Major(
                new int[]{3062},
                new String[]{"ILES-BA"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science (Dual Degree - With International Studies)",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGIS3);

        // ====================================
        // College of Health and Human Sciences
        // ====================================

        // *** Major in Family and Consumer Sciences

        // 4030: FACS-BS, Family and Consumer Sciences (DEACTIVATED)
        final Major mFACS1 = new Major(
                new int[]{4030},
                new String[]{"FACS-BS"},
                EMPTY_TRACK_ARRAY,
                "Family and Consumer Sciences",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS1);

        // 4031: FACS-FACZ-BS, Family and Consumer Sciences/Family and Consumer Sciences (DEACTIVATED)
        final Major mFACS2 = new Major(
                new int[]{4031},
                new String[]{"FACS-FACZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Family and Consumer Sciences (Family and Consumer Sciences)",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS2);

        // 4032: FACS-FCSZ-BS, Family and Consumer Sciences - Family and Consumer Sciences Education (DEACTIVATED)
        final Major mFACS3 = new Major(
                new int[]{4032},
                new String[]{"FACS-FCSZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Family and Consumer Sciences (Family and Consumer Sciences Education)",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS3);

        // 4033: FACS-IDSZ-BS, Family and Consumer Sciences - Interdisciplinary (DEACTIVATED)
        final Major mFACS4 = new Major(
                new int[]{4033},
                new String[]{"FACS-IDSZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Family and Consumer Sciences (Interdisciplinary)",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS4);

        // *** Major in Fermentation Science and Technology

        final Major mFMST = new Major(
                new int[]{4040},
                new String[]{"FMST-BS"},
                EMPTY_TRACK_ARRAY,
                "Fermentation and Food Science",
                MathPlanConstants.PGMS + "fermentation-and-food-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mFMST);

        // *** Major in Health and Exercise Science

        // 4052: HAES-SPMZ-BS, Health and Exercise Science - Sports Medicine (DEACTIVATED)
        final Major mHAES = new Major(
                new int[]{4052},
                new String[]{"HAES-SPMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Health and Exercise Science (Sports Medicine)",
                MathPlanConstants.PGMS + "health-and-exercise-science/",
                ERequirement.M_118_124_125,
                IdealFirstTerm.IDEAL_182425);
        majors.add(mHAES);

        // *** Major in Hospitality Management

        final Major mHSMG = new Major(
                new int[]{4060},
                new String[]{"HSMG-BS"},
                EMPTY_TRACK_ARRAY,
                "Hospitality and Event Management",
                MathPlanConstants.PGMS + "hospitality-event-management/",
                ERequirement.M_117_101,
                IdealFirstTerm.IDEAL_1701);
        majors.add(mHSMG);

        // *** Major in Human Development and Family Studies

        // 4073: HDFS-PHPZ-BS, Human Development and Family Studies - Leadership and Entrepreneurial Prof. (DEACTIVATED)
        final Major mHDFS1 = new Major(
                new int[]{4073},
                new String[]{"HDFS-LEPZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Human Development &amp; Family Studies (Leadership and Entrepreneurial Professions)",
                MathPlanConstants.PGMS + "human-development-and-family-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHDFS1);

        // 4075: HDFS-PISZ-BS, Human Development and Family Studies - Prevention and Intervention Sciences (DEACTIVATED)
        final Major mHDFS2 = new Major(
                new int[]{4075},
                new String[]{"HDFS-PISZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Human Development &amp; Family Studies (Prevention and Intervention Sciences)",
                MathPlanConstants.PGMS + "human-development-and-family-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHDFS2);

        // FAKE: HDFS-DECZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DHDZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DPHZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DPIZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DLAZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DLEZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-LEPZ-BS, Human Development and Family Studies - ???
        final Major mHDFS3 = new Major(
                new int[]{-1, -1, -1, -1, -1, -1, -1},
                new String[]{"HDFS-DECZ-BS", "HDFS-DHDZ-BS", "HDFS-DPHZ-BS", "HDFS-DPIZ-BS", "HDFS-DLAZ-BS",
                        "HDFS-DLEZ-BS", "HDFS-LEPZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Human Development &amp; Family Studies",
                MathPlanConstants.PGMS + "human-development-and-family-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHDFS3);

        // *** Major in Interior Architecture and Design
        // 4080: INTD-BS, Interior Architecture and Design (DEACTIVATED)

        final Major mINTD = new Major(
                new int[]{4080, -1},
                new String[]{"INTD-BS", "IAD0"},
                EMPTY_TRACK_ARRAY,
                "Interior Design",
                MathPlanConstants.PGMS + "interior-architecture-and-design/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mINTD);

        // *** Major in Nutrition and Food Science

        // 4091: NAFS-DNMZ-BS, Nutrition and Food Science - Dietetics and Nutrition Management (DEACTIVATED)
        final Major mNAFS1 = new Major(
                new int[]{4090},
                new String[]{"NAFS-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS1);

        // 4091: NAFS-DNMZ-BS, Nutrition and Food Science - Dietetics and Nutrition Management (DEACTIVATED)
        final Major mNAFS2 = new Major(
                new int[]{4091},
                new String[]{"NAFS-DNMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Dietetics and Nutrition Management)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS2);

        // 4092: NAFS-FSNZ-BS, Nutrition and Food Science - Food Safety and Nutrition (DEACTIVATED)
        final Major mNAFS3 = new Major(
                new int[]{4092},
                new String[]{"NAFS-FSNZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Food Safety and Nutrition)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS3);

        // 4093: NAFS-NFTZ-BS, Nutrition and Food Science - Nutrition and Fitness (DEACTIVATED)
        final Major mNAFS4 = new Major(
                new int[]{4093},
                new String[]{"NAFS-NFTZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Nutrition and Fitness)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS4);

        // 4094: NAFS-NUSZ-BS, Nutrition and Food Science - Nutritional Sciences (DEACTIVATED)
        final Major mNAFS5 = new Major(
                new int[]{4094},
                new String[]{"NAFS-NUSZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS5);

        // 4095: NAFS-FSYZ-BS, Nutrition and Food Science - Food Systems (DEACTIVATED)
        final Major mNAFS6 = new Major(
                new int[]{4095},
                new String[]{"NAFS-FSYZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Food Systems)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS6);

        // 4096: NAFS-FSCZ-BS, Nutrition and Food Science - Food Science (DEACTIVATED)
        final Major mNAFS7 = new Major(
                new int[]{4096},
                new String[]{"NAFS-FSCZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Food Science)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS7);

        // 4097: NAFS-PHNZ-BS, Nutrition and Food Science - Pre-Health Nutrition (DEACTIVATED)
        final Major mNAFS8 = new Major(
                new int[]{4097},
                new String[]{"NAFS-PHNZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science (Pre-Health Nutrition)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS8);

        // FAKE: NAFS-DNRZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-GLTZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-NRTZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-CPSY-BS, Nutrition and Food Science - ???
        final Major mNAFS9 = new Major(
                new int[]{-1, -1, -1, -1},
                new String[]{"NAFS-DNRZ-BS", "NAFS-GLTZ-BS", "NAFS-NRTZ-BS", "NAFS-CPSY-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS9);

        // =======================
        // College of Liberal Arts
        // =======================

        // *** Major in Anthropology
        // FAKE: ANTH-DD-BA, Anthropology - Dual Degree

        final Major mANTH = new Major(
                new int[]{-1},
                new String[]{"ANTH-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Anthropology",
                MathPlanConstants.PGMS + "anthropology/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mANTH);

        // *** Major in Art (B.A. and B.F.A combined)
        // 5011: ARTI-AREZ-BA - Art B.A. - Art Education (Deactivated)

        final Major mARTI = new Major(
                new int[]{5011},
                new String[]{"ARTI-ARTZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING},
                "Art (Art Education)",
                MathPlanConstants.PGMS + "art-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mARTI);

        // *** Major in Communication Studies

        // 5041: CMST-TCLZ-BA, Communication Studies - Speech Teacher Licensure (DEACTIVATED)
        final Major mCMST1 = new Major(
                new int[]{5041},
                new String[]{"CMST-TCLZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Communication Studies (Speech Teacher Licensure)",
                MathPlanConstants.PGMS + "communication-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mCMST1);

        // FAKE: CMST-DD-BA, Communication Studies - Dual Degree
        final Major mCMST2 = new Major(
                new int[]{-1},
                new String[]{"CMST-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Communication Studies",
                MathPlanConstants.PGMS + "communication-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mCMST2);

        // *** Major in Dance

        // FAKE: DNC0, Pre-Dance
        // FAKE: DANC-DEDZ-BF, Dance - ???
        final Major mDNCE = new Major(
                new int[]{-1, 01},
                new String[]{"DNC0", "DANC-DEDZ-BF"},
                EMPTY_TRACK_ARRAY,
                "Dance",
                MathPlanConstants.PGMS + "dance-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mDNCE);

        // *** Major in Economics

        // FAKE: ECON-DD-BA, Economics - Dual degree
        final Major mECON = new Major(
                new int[]{-1},
                new String[]{"ECON-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Economics",
                MathPlanConstants.PGMS + "economics/",
                ERequirement.M_141_OR_155_OR_160,
                IdealFirstTerm.IDEAL_1718);
        majors.add(mECON);

        // *** Major in English

        // 5073: ENGL-LANZ-BA, English/Language (DEACTIVATED)
        final Major mENGL1 = new Major(
                new int[]{5073},
                new String[]{"ENGL-LANZ-BA"},
                EMPTY_TRACK_ARRAY,
                "English (Language)",
                MathPlanConstants.PGMS + "english/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mENGL1);

        // *** Major in Journalism and Media Communication

        // FAKE: JAMC-DD-BA, Journalism and Media Communication - Dual degree
        final Major mJAMC = new Major(
                new int[]{-1},
                new String[]{"JAMC-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Journalism and Media Communication",
                MathPlanConstants.PGMS + "journalism-and-media-communication/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mJAMC);

        // *** Major in Music
        // FAKE: MUS0, Pre-Music

        final Major mMUSI = new Major(
                new int[]{01},
                new String[]{"MUS0"},
                EMPTY_TRACK_ARRAY,
                "Music",
                MathPlanConstants.PGMS + "music-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mMUSI);

        // *** Major in Political Science

        // 5153: POLS-ULPZ-BA, Political Science - U.S. Government, Law, and Policy (DEACTIVATED)
        final Major mPOLS1 = new Major(
                new int[]{5153},
                new String[]{"POLS-ULPZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Political Science (U.S. Government, Law, and Policy)",
                MathPlanConstants.PGMS + "political-science/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mPOLS1);

        // FAKE: POLS-DD-BA, Political Science - Dual Degree
        final Major mPOLS2 = new Major(
                new int[]{-1},
                new String[]{"POLS-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Political Science",
                MathPlanConstants.PGMS + "political-science/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mPOLS2);

        // *** Major in Sociology

        // FAKE: SOCI-DGSZ-BA, Sociology - ???
        final Major mSOCI = new Major(
                new int[]{-1},
                new String[]{"SOCI-DGSZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Sociology",
                MathPlanConstants.PGMS + "sociology/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mSOCI);

        // *** Major in Theatre

        // 5171: THTR-DTHZ-BA, Theatre/Design and Technology (DEACTIVATED)
        final Major mTHTR1 = new Major(
                new int[]{5171},
                new String[]{"THTR-DTHZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Theatre (Design and Technology)",
                MathPlanConstants.PGMS + "theatre/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mTHTR1);

        // 5172: THTR-GTRZ-BA, Theatre/General Theatre (DEACTIVATED)
        final Major mTHTR2 = new Major(
                new int[]{5172},
                new String[]{"THTR-GTRZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Theatre (General Theatre)",
                MathPlanConstants.PGMS + "theatre/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mTHTR2);

        // FAKE: THR0, Pre-Theatre
        final Major mTHTR3 = new Major(
                new int[]{-1},
                new String[]{"THR0"},
                EMPTY_TRACK_ARRAY,
                "Theatre",
                MathPlanConstants.PGMS + "theatre/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mTHTR3);

        // *** Major in Interdisciplinary Liberal Arts

        // FAKE: ILAR-DD-BA, Interdisciplinary Liberal Arts - Dual degree
        final Major mILAR = new Major(
                new int[]{-1},
                new String[]{"ILAR-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Interdisciplinary Liberal Arts",
                MathPlanConstants.PGMS + "interdisciplinary-liberal-arts/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mILAR);

        // ============================
        // College of Natural Resources
        // ============================

        // *** Major in Watershed Science and Sustainability

        // 6070: WRSC-BS, Watershed Science and Sustainability (DEACTIVATED)
        final Major mWRSC = new Major(
                new int[]{6070},
                new String[]{"WRSC-BS"},
                EMPTY_TRACK_ARRAY,
                "Watershed Science and Sustainability",
                MathPlanConstants.PGMS + "watershed-science/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_18_OR_HIGHER);
        majors.add(mWRSC);

        // *** Major in Natural Resource Tourism

        // 6040: NRRT-BS, Natural Resource Tourism (DEACTIVATED)
        // 6041: NRRT-GLTZ-BS, Natural Resource Tourism - Global Tourism (DEACTIVATED)
        // 6042: NRRT-NRTZ-BS, Natural Resource Tourism - Natural Resource Tourism (DEACTIVATED)
        final Major mNRRT = new Major(
                new int[]{6040, 6041, 6042},
                new String[]{"NRRT-BS", "NRRT-GLTZ-BS", "NRRT-NRTZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Natural Resource Tourism",
                MathPlanConstants.PGMS + "natural-resource-tourism/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNRRT);

        // ===========================
        // College of Natural Sciences
        // ===========================

        // *** Major in Chemistry

        // 7031: CHEM-ACSZ-BS, Chemistry/ACS Certified (DEACTIVATED)
        // 7032: CHEM-NACZ-BS, Chemistry/Non-ACS Certified (DEACTIVATED)
        final Major mCHEM = new Major(
                new int[]{7031, 7032},
                new String[]{"CHEM-ACSZ-BS", "CHEM-NACZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Chemistry",
                MathPlanConstants.PGMS + "chemistry/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mCHEM);

        // *** Major in Computer Science

        // FAKE: CPSC-DAIZ-BS, Computer Science - ???
        // FAKE: CPSC-DCSZ-BS, Computer Science - ???
        // FAKE: CPSC-DCYZ-BS, Computer Science - ???
        // FAKE: CPSC-DHCZ-BS, Computer Science - ???
        // FAKE: CPSC-DNSZ-BS, Computer Science - ???
        // FAKE: CPSC-DSEZ-BS, Computer Science - ???
        final Major mCPSC = new Major(
                new int[]{-1, -1, -1, -1, -1, -1},
                new String[]{"CPSC-DAIZ-BS", "CPSC-DCSZ-BS", "CPSC-DCYZ-BS", "CPSC-DHCZ-BS", "CPSC-DNSZ-BS",
                        "CPSC-DSEZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Computer Science",
                MathPlanConstants.PGMS + "computer-science/",
                ERequirement.M_156_OR_160,
                IdealFirstTerm.IDEAL_56_OR_60);
        majors.add(mCPSC);

        // *** Major in Data Science
        // 7051: DSCI-DCMZ-BS, Data Science - Computer Science (old program code)

        final Major mDSCI = new Major(
                new int[]{7051},
                new String[]{"DSCI-DCMZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Data Science",
                MathPlanConstants.PGMS + "data-science/",
                ERequirement.M_156,
                IdealFirstTerm.IDEAL_56);
        majors.add(mDSCI);

        // *** Major in Psychology

        // FAKE: PSYC-GDSZ-BS, Psychology - ???
        final Major mPSYC = new Major(
                new int[]{-1},
                new String[]{"PSYC-GDSZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Psychology",
                MathPlanConstants.PGMS + "psychology/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_17);
        majors.add(mPSYC);

        // *** Major in Statistics

        // 7101: STAT-GSTZ-BS, Statistics/General Statistics (DEACTIVATED)
        // 7102: STAT-MSTZ-BS, Statistics/Mathematical Statistics (DEACTIVATED)
        final Major mSTAT = new Major(
                new int[]{7101, 7102},
                new String[]{"STAT-GSTZ-BS", "STAT-MSTZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Statistics",
                MathPlanConstants.PGMS + "statistics/",
                ERequirement.M_156_OR_160,
                IdealFirstTerm.IDEAL_56_OR_60);
        majors.add(mSTAT);

        // ======================================================
        // College of Veterinary Medicine and Biomedical Sciences
        // ======================================================

        // *** Major in Biomedical Sciences

        // 8010: EVHL-BS, Environmental Health (DEACTIVATED)
        final Major mBIOM1 = new Major(
                new int[]{8010},
                new String[]{"EVHL-BS"},
                EMPTY_TRACK_ARRAY,
                "Biomedical Sciences (Environmental Health)",
                MathPlanConstants.PGMS + "biomedical-sciences/",
                ERequirement.PICK_3CR_118_124_125_126_155_160,
                IdealFirstTerm.IDEAL_PICK_18_TO_60);
        majors.add(mBIOM1);

        // 8020: MICR-BS, Microbiology (DEACTIVATED)
        final Major mBIOM2 = new Major(
                new int[]{8020},
                new String[]{"MICR-BS"},
                EMPTY_TRACK_ARRAY,
                "Biomedical Sciences (Microbiology)",
                MathPlanConstants.PGMS + "biomedical-sciences/",
                ERequirement.PICK_3CR_118_124_125_126_155_160,
                IdealFirstTerm.IDEAL_PICK_18_TO_60);
        majors.add(mBIOM2);

        // 8040: Health physics - here so it does not appear in list of selectable majors
        final Major mHLPH = new Major(
                new int[]{8040},
                new String[]{"HLPH-DD-BS"},
                EMPTY_TRACK_ARRAY,
                "Health Physics",
                "https://online.colostate.edu/degrees/health-physics/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mHLPH);

        majors.sort(null);
    }
}
