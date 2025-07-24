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

        super(50);

        final List<Major> majors = innerGetMajors();

        // *** Last reviewed July 16, 2025, from the 2025-2026 Catalog. ***

        // ================================
        // College of Agricultural Sciences
        // ================================

        // *** Major in Agricultural Business

        // FAKE: AGBU-DD-BS, Agricultural Business - Dual Degree
        final Major mAGBU = new Major(
                new int[]{1000},
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
                new int[]{1030},
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
                new int[]{1040, 1041},
                new String[]{"ENHR-LNBZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Environmental Horticulture (Landscape Business)",
                MathPlanConstants.PGMS + "environmental-horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mENHR);

        // *** Major in Horticulture

        // 1061: HORT-FLOZ-BS, Horticulture - Floriculture (DEACTIVATED)
        // 1065: HORT-HTHZ-BS, Horticulture - Horticultural Therapy (DEACTIVATED)
        // FAKE: HORT-DHBZ-BS, Horticulture - ???
        final Major mHORT = new Major(
                new int[]{1060, 1061, 1065},
                new String[]{"HORT-FLOZ-BS", "HORT-HTHZ-BS", "HORT-DHBZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Horticulture",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHORT);

        // *** Major in Soil and Crop Sciences

        // 1081: SOCR-APMZ-BS, Soil and Crop Sciences - Agronomic Production Management (DEACTIVATED)
        // 1082: SOCR-APIZ-BS, Soil and Crop Sciences - Applied Information Technology (DEACTIVATED)
        // 1083: SOCR-ISCZ-BS, Soil and Crop Sciences - International Soil and Crop Sciences (DEACTIVATED)
        // 1084: SOCR-PBGZ-BS, Soil and Crop Sciences - Plant Biotechnology, Genetics and Breeding (DEACTIVATED)
        // 1085: SOCR-SOEZ-BS, Soil and Crop Sciences - Soil Ecology (DEACTIVATED)
        // 1086: SOCR-SRNZ-BS, Soil and Crop Sciences - Soil Restoration and Conservation (DEACTIVATED)
        // FAKE: SOCR-DSAZ-BS, Soil and Crop Sciences - ???
        final Major mSOCR = new Major(
                new int[]{1080, 1081, 1082, 1083, 1084, 1085, 1086},
                new String[]{"SOCR-APMZ-BS", "SOCR-APIZ-BS", "SOCR-ISCZ-BS", "SOCR-PBGZ-BS", "SOCR-SOEZ-BS",
                        "SOCR-SRNZ-BS", "SOCR-DSAZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Soil and Crop Sciences",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR);

        // ===================
        // College of Business
        // ===================

        // *** Major in Business Administration
        // 2007: BUSA-OIMZ-BS, Business Administration - Organization and Innovation Management (DEACTIVATED)
        // FAKE: BUSA-DACZ-BS, Business Administration
        // FAKE: BUSA-OIMZ-BS, Business Administration
        final Major mBUSA = new Major(
                new int[]{2000, 2007},
                new String[]{"BUSA-OIMZ-BS", "BUSA-DACZ-BS", "BUSA-OIMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Business Administration",
                MathPlanConstants.PGMS + "business-administration/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_41);
        majors.add(mBUSA);

        // ======================
        // College of Engineering
        // ======================

        // *** Major in Computer Engineering
        // 3034: CPEG-VICZ-BS, Computer Engineering - VLSI and Integrated Circuits (DEACTIVATED)

        final Major mCPEG = new Major(
                new int[]{3030, 3034},
                new String[]{"CPEG-VICZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Computer Engineering (VLSI and Integrated Circuits)",
                MathPlanConstants.PGMS + "computer-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCPEG);

        // *** Major in Engineering Science

        // 3050: EGSC-EGPZ-BS, Engineering Science (DEACTIVATED)
        // 3051: EGSC-EGPZ-BS, Engineering Science - Engineering Physics (DEACTIVATED)
        // 3052: EGSC-SPEZ-BS, Engineering Science - Space Engineering (DEACTIVATED)
        // 3053: EGSC-TCEZ-BS, Engineering Science - Teacher Education (DEACTIVATED)
        final Major mEGSC = new Major(
                new int[]{3050, 3051, 3052, 3053},
                new String[]{"EGSC-BS", "EGSC-EGPZ-BS", "EGSC-SPEZ-BS", "EGSC-TCEZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGSC);

        // *** Dual-Degree programs in Engineering Science

        // 3060: EGIS-DUAL, Engineering Science Dual Degree (DEACTIVATED)
        final Major mEGIS = new Major(
                new int[]{3060, 3061, 3062},
                new String[]{"EGIS-DUAL", "ILES-BA"},
                EMPTY_TRACK_ARRAY,
                "Engineering Science",
                MathPlanConstants.PGMS,
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEGIS);

        // ====================================
        // College of Health and Human Sciences
        // ====================================

        // *** Major in Family and Consumer Sciences

        // 4030: FACS-BS, Family and Consumer Sciences (DEACTIVATED)
        // 4031: FACS-FACZ-BS, Family and Consumer Sciences/Family and Consumer Sciences (DEACTIVATED)
        // 4032: FACS-FCSZ-BS, Family and Consumer Sciences - Family and Consumer Sciences Education (DEACTIVATED)
        // 4033: FACS-IDSZ-BS, Family and Consumer Sciences - Interdisciplinary (DEACTIVATED)
        final Major mFACS = new Major(
                new int[]{4034, 4030, 4031, 4032, 4033},
                new String[]{"FACS-BS", "FACS-FACZ-BS", "FACS-FCSZ-BS", "FACS-IDSZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Family and Consumer Sciences",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS);

        // *** Major in Fermentation Science and Technology

        final Major mFMST = new Major(
                new int[]{4041, 4040},
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
                new int[]{4050, 4052},
                new String[]{"HAES-SPMZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Health and Exercise Science (Sports Medicine)",
                MathPlanConstants.PGMS + "health-and-exercise-science/",
                ERequirement.M_118_124_125,
                IdealFirstTerm.IDEAL_182425);
        majors.add(mHAES);

        // *** Major in Hospitality Management

        final Major mHSMG = new Major(
                new int[]{4061, 4060},
                new String[]{"HSMG-BS"},
                EMPTY_TRACK_ARRAY,
                "Hospitality and Event Management",
                MathPlanConstants.PGMS + "hospitality-event-management/",
                ERequirement.M_117_101,
                IdealFirstTerm.IDEAL_1701);
        majors.add(mHSMG);

        // *** Major in Human Development and Family Studies

        // 4073: HDFS-PHPZ-BS, Human Development and Family Studies - Leadership and Entrepreneurial Prof. (DEACTIVATED)
        // 4075: HDFS-PISZ-BS, Human Development and Family Studies - Prevention and Intervention Sciences (DEACTIVATED)
        // FAKE: HDFS-DECZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DHDZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DPHZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DPIZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DLAZ-BS, Human Development and Family Studies - ???
        // FAKE: HDFS-DLEZ-BS, Human Development and Family Studies - ???
        final Major mHDFS = new Major(
                new int[]{4070, 4073, 4075},
                new String[]{"HDFS-LEPZ-BS", "HDFS-PISZ-BS", "HDFS-DECZ-BS", "HDFS-DHDZ-BS", "HDFS-DPHZ-BS",
                        "HDFS-DPIZ-BS", "HDFS-DLAZ-BS", "HDFS-DLEZ-BS", "HDFS-LEPZ-BS"},
                EMPTY_TRACK_ARRAY,
                "Human Development &amp; Family Studies (Leadership and Entrepreneurial Professions)",
                MathPlanConstants.PGMS + "human-development-and-family-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHDFS);

        // *** Major in Interior Architecture and Design
        // 4080: INTD-BS, Interior Architecture and Design (DEACTIVATED)

        final Major mINTD = new Major(
                new int[]{4081, 4080},
                new String[]{"INTD-BS", "IAD0"},
                EMPTY_TRACK_ARRAY,
                "Interior Design",
                MathPlanConstants.PGMS + "interior-architecture-and-design/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mINTD);

        // *** Major in Nutrition and Food Science

        // 4091: NAFS-DNMZ-BS, Nutrition and Food Science - Dietetics and Nutrition Management (DEACTIVATED)
        // 4091: NAFS-DNMZ-BS, Nutrition and Food Science - Dietetics and Nutrition Management (DEACTIVATED)
        // 4092: NAFS-FSNZ-BS, Nutrition and Food Science - Food Safety and Nutrition (DEACTIVATED)
        // 4093: NAFS-NFTZ-BS, Nutrition and Food Science - Nutrition and Fitness (DEACTIVATED)
        // 4094: NAFS-NUSZ-BS, Nutrition and Food Science - Nutritional Sciences (DEACTIVATED)
        // 4095: NAFS-FSYZ-BS, Nutrition and Food Science - Food Systems (DEACTIVATED)
        // 4096: NAFS-FSCZ-BS, Nutrition and Food Science - Food Science (DEACTIVATED)
        // 4097: NAFS-PHNZ-BS, Nutrition and Food Science - Pre-Health Nutrition (DEACTIVATED)
        // FAKE: NAFS-DNRZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-GLTZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-NRTZ-BS, Nutrition and Food Science - ???
        // FAKE: NAFS-CPSY-BS, Nutrition and Food Science - ???
        final Major mNAFS = new Major(
                new int[]{4110, 4090, 4091, 4092, 4093, 4094, 4095, 4096, 4097},
                new String[]{"NAFS-BS", "NAFS-DNMZ-BS", "NAFS-FSNZ-BS", "NAFS-NFTZ-BS", "NAFS-NUSZ-BS", "NAFS-FSYZ-BS",
                        "NAFS-FSCZ-BS", "NAFS-PHNZ-BS", "NAFS-DNRZ-BS", "NAFS-GLTZ-BS", "NAFS-NRTZ-BS", "NAFS-CPSY-BS"},
                EMPTY_TRACK_ARRAY,
                "Nutrition and Food Science",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNAFS);

        // =======================
        // College of Liberal Arts
        // =======================

        // *** Major in Anthropology
        // FAKE: ANTH-DD-BA, Anthropology - Dual Degree

        final Major mANTH = new Major(
                new int[]{5000},
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
                new int[]{5010, 5011},
                new String[]{"ARTI-ARTZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING},
                "Art (Art Education)",
                MathPlanConstants.PGMS + "art-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mARTI);

        // *** Major in Communication Studies

        // 5041: CMST-TCLZ-BA, Communication Studies - Speech Teacher Licensure (DEACTIVATED)
        // FAKE: CMST-DD-BA, Communication Studies - Dual Degree
        final Major mCMST1 = new Major(
                new int[]{5040, 5041},
                new String[]{"CMST-TCLZ-BA", "CMST-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Communication Studies",
                MathPlanConstants.PGMS + "communication-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mCMST1);

        // *** Major in Dance

        // FAKE: DNC0, Pre-Dance
        // FAKE: DANC-DEDZ-BF, Dance - ???
        final Major mDNCE = new Major(
                new int[]{5050},
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
                new int[]{5060},
                new String[]{"ECON-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Economics",
                MathPlanConstants.PGMS + "economics/",
                ERequirement.M_141_OR_155_OR_160,
                IdealFirstTerm.IDEAL_1718);
        majors.add(mECON);

        // *** Major in English

        // 5073: ENGL-LANZ-BA, English/Language (DEACTIVATED)
        final Major mENGL = new Major(
                new int[]{5070, 5073},
                new String[]{"ENGL-LANZ-BA"},
                EMPTY_TRACK_ARRAY,
                "English (Language)",
                MathPlanConstants.PGMS + "english/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mENGL);

        // *** Major in Journalism and Media Communication

        // FAKE: JAMC-DD-BA, Journalism and Media Communication - Dual degree
        final Major mJAMC = new Major(
                new int[]{5100},
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
                new int[]{5120},
                new String[]{"MUS0"},
                EMPTY_TRACK_ARRAY,
                "Music",
                MathPlanConstants.PGMS + "music-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mMUSI);

        // *** Major in Political Science

        // 5153: POLS-ULPZ-BA, Political Science - U.S. Government, Law, and Policy (DEACTIVATED)
        // FAKE: POLS-DD-BA, Political Science - Dual Degree
        final Major mPOLS = new Major(
                new int[]{5150, 5153},
                new String[]{"POLS-ULPZ-BA", "POLS-DD-BA"},
                EMPTY_TRACK_ARRAY,
                "Political Science",
                MathPlanConstants.PGMS + "political-science/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mPOLS);

        // *** Major in Sociology

        // FAKE: SOCI-DGSZ-BA, Sociology - ???
        final Major mSOCI = new Major(
                new int[]{5160},
                new String[]{"SOCI-DGSZ-BA"},
                EMPTY_TRACK_ARRAY,
                "Sociology",
                MathPlanConstants.PGMS + "sociology/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mSOCI);

        // *** Major in Theatre

        // 5171: THTR-DTHZ-BA, Theatre/Design and Technology (DEACTIVATED)
        // 5172: THTR-GTRZ-BA, Theatre/General Theatre (DEACTIVATED)
        // FAKE: THR0, Pre-Theatre
        final Major mTHTR1 = new Major(
                new int[]{5170, 5171, 5172},
                new String[]{"THTR-DTHZ-BA", "THTR-GTRZ-BA", "THR0"},
                EMPTY_TRACK_ARRAY,
                "Theatre",
                MathPlanConstants.PGMS + "theatre/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mTHTR1);

        // *** Major in Interdisciplinary Liberal Arts

        // FAKE: ILAR-DD-BA, Interdisciplinary Liberal Arts - Dual degree
        final Major mILAR = new Major(
                new int[]{5200},
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
                new int[]{6071, 6070},
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
                new int[]{6043, 6040, 6041, 6042},
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
                new int[]{7030, 7031, 7032},
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
                new int[]{7040},
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
                new int[]{7050, 7051},
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
                new int[]{7090},
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
                new int[]{7100, 7101, 7102},
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
