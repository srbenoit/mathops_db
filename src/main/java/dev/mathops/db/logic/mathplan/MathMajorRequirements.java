package dev.mathops.db.logic.mathplan;

import dev.mathops.db.old.logic.mathplan.data.Major;
import dev.mathops.db.old.logic.mathplan.data.MathPlanConstants;
import dev.mathops.db.old.rawrecord.RawRecordConstants;

import java.util.TreeMap;

/**
 * An immutable record of a list of courses from which a student may select one course, or some number of credits.
 *
 * <p>
 * Some "course groups" consist of single courses. This allows us to treat all requirements in a major as a collection
 * of course groups, where the student must satisfy every course group, but may select one course or some number of
 * credits from each group.
 *
 * <p>
 * Course groups have an ordering based on the lowest course number required to satisfy the group. If two groups have
 * the same lowest course number, the group with the longer set of options is considered "less than" the group with
 * fewer options.
 */
public enum MathMajorRequirements {
    ;

    // *** Last reviewed July 9, 2025 - Concentrations condensed into one code for the major ***

    // ================================
    // College of Agricultural Sciences
    // ================================

    // *** Major in Agricultural Biology
    // Concentrations grouped into major:
    // 1091: AGBI-ENTZ-BS, Agricultural Biology - Entomology
    // 1092: AGBI-PLPZ-BS, Agricultural Biology - Plant Pathology
    // 1093: AGBI-WEEZ-BS, Agricultural Biology - Weed Science

    public static final MajorMathRequirement mAGBI = new MajorMathRequirement(
            new int[]{1090, 1091, 1092, 1093},
            new String[]{"AGBI-BS", "AGBI-ENTZ-BS", "AGBI-PLPZ-BS", "AGBI-WEEZ-BS"},
            "Agricultural Biology",
            MathPlanConstants.PGMS + "agricultural-biology/",
            EHowImportantIsMathFirstSemester.IMPORTANT,
            new String[]{MathPlanConstants.M_155});

    // *** Major in Agricultural Business
    // Concentrations grouped into major:
    // 1001: AGBU-AECZ-BS, Agricultural Business - Agricultural Economics
    // 1002: AGBU-FRCZ-BS, Agricultural Business - Farm and Ranch Management
    // 1003: AGBU-FSSZ-BS, Agricultural Business - Food Systems
    // FAKE: AGBU-DD-BS, Agricultural Business - Dual Degree

    final Major mAGBU = new Major(
            new int[]{1000, 1001, 1002, 1003},
            new String[]{"AGBU-BS", "AGBU-AECZ-BS", "AGBU-FRCZ-BS", "AGBU-FSSZ-BS", "AGBU-DD-BS"},
            "Agricultural Business",
            MathPlanConstants.PGMS + "agricultural-business/");
    final MajorMathRequirement rAGBU = new MajorMathRequirement("AGBU-BS")
            .setSemesterCourses("M 117!,M 118!,M 124", null, MathPlanConstants.M_141);
                map.put(mAGBU,rAGBU);

    // *** Major in Agricultural Education

    final Major mAGED = new Major(
            new int[]{1010, 1011, 1012},
            new String[]{"AGED-BS", "AGED-AGLZ-BS", "AGED-TDLZ-BS"},
            "Agricultural Education",
            MathPlanConstants.PGMS + "agricultural-education/");
    final MajorMathRequirement rAGED = new MajorMathRequirement("AGED-BS")
            .setSemesterCourses(MathPlanConstants.AGED3A, null, null);
                map.put(mAGED,rAGED);

    // Concentrations grouped into major:
    // 1011: AGED-AGLZ-BS, Agricultural Education - Agricultural Literacy
    // 1012: AGED-TDLZ-BS, Agricultural Education - Teacher Development

    // *** Major in Animal Science

    final Major mANIM = new Major(
            new int[]{1020},
            new String[]{"ANIM-BS"},
            "Animal Science",
            MathPlanConstants.PGMS + "animal-science/");
    final MajorMathRequirement rANIM = new MajorMathRequirement("ANIM-BS")
            .setSemesterCourses("ANIM3!", null, null);
                map.put(mANIM,rANIM);

    // *** Major in Environmental and Natural Resource Economics

    final Major mENRE = new Major(
            new int[]{1030},
            new String[]{"ENRE-BS", "ENRE-DD-BS"},
            "Environmental and Natural Resource Economics",
            MathPlanConstants.PGMS + "environmental-and-natural-resource-economics/");
    final MajorMathRequirement rENRE = new MajorMathRequirement("ENRE-BS")
            .setSemesterCourses("M 117!,M 118,M 124", null, MathPlanConstants.M_141);
                map.put(mENRE,rENRE);

    // Concentrations grouped into major:
    // FAKE: ENRE-DD-BS, Environmental and Natural Resource Economics - Dual degree

    // *** Major in Environmental Horticulture

    final Major mENHR = new Major(
            new int[]{1040, 1041, 1042, 1043, 1044},
            new String[]{"ENHR-BS", "ENHR-LNBZ-BS", "ENHR-LDAZ-BS", "ENHR-NALZ-BS", "ENHR-TURZ-BS"},
            "Environmental Horticulture",
            MathPlanConstants.PGMS + "environmental-horticulture/"
    );
    final MajorMathRequirement rENHR = new MajorMathRequirement("ENHR-BS")
            .setSemesterCourses("M 117!,M 118!", null, null);
                map.put(mENHR,rENHR);

    // Concentrations grouped into major:
    // 1041: ENHR-LNBZ-BS, Environmental Horticulture - Landscape Business (DEACTIVATED)
    // 1042: ENHR-LDAZ-BS, Environmental Horticulture - Landscape Design and Contracting
    // 1043: ENHR-NALZ-BS, Environmental Horticulture - Nursery and Landscape Management
    // 1044: ENHR-TURZ-BS, Environmental Horticulture - Turf Management

    // *** Major in Equine Science

    final Major mEQSC = new Major(
            new int[]{1050},
            new String[]{"EQSC-BS"},
            "Equine Science",
            MathPlanConstants.PGMS + "equine-science/");
    final MajorMathRequirement rEQSC = new MajorMathRequirement("EQSC-BS")
            .setSemesterCourses(MathPlanConstants.ANIM3, null, null);
                map.put(mEQSC,rEQSC);

    // *** Major in Horticulture

    final Major mHORT = new Major(
            new int[]{1060, 1061, 1062, 1063, 1064, 1065, 1066},
            new String[]{"HORT-BS", "HORT-FLOZ-BS", "HORT-HBMZ-BS", "HORT-HFCZ-BS", "HORT-HOSZ-BS",
                    "HORT-HTHZ-BS", "HORT-CEHZ-BS", "HORT-DHBZ-BS"},
            "Horticulture",
            MathPlanConstants.PGMS + "horticulture/");
    final MajorMathRequirement rHORT = new MajorMathRequirement("HORT-BS")
            .setSemesterCourses("M 117!,M 118,M 124", null, null);
                map.put(mHORT,rHORT);

    // Concentrations grouped into major:
    // 1061: HORT-FLOZ-BS, Horticulture - Floriculture (DEACTIVATED)
    // 1062: HORT-HBMZ-BS, Horticulture - Horticultural Business Management
    // 1063: HORT-HFCZ-BS, Horticulture - Horticultural Food Crops
    // 1064: HORT-HOSZ-BS, Horticulture - Horticultural Science
    // 1065: HORT-HTHZ-BS, Horticulture-Horticultural Therapy (DEACTIVATED)
    // 1066: HORT-CEHZ-BS, Horticulture - Controlled Environmental Horticulture
    // FAKE: HORT-DHBZ-BS, Horticulture - ???

    // *** Major in Landscape Architecture

    final Major mLDAR = new Major(
            new int[]{1070},
            new String[]{"LDAR-BS"},
            "Landscape Architecture",
            MathPlanConstants.PGMS + "landscape-architecture/");
    final MajorMathRequirement rLDAR = new MajorMathRequirement("LDAR-BS")
            .setSemesterCourses(MathPlanConstants.AUCC2, RawRecordConstants.M126, null);
                map.put(mLDAR,rLDAR);

    // *** Major in Livestock Business Management

    final Major mLSBM = new Major(
            new int[]{1075},
            new String[]{"LSBM-BS"},
            "Livestock Business Management",
            MathPlanConstants.PGMS + "livestock-business-management/");
    final MajorMathRequirement rLSBM = new MajorMathRequirement("LSBM-BS")
            .setSemesterCourses("AGED3A!", RawRecordConstants.M126, null);
                map.put(mLSBM,rLSBM);

    // *** Major in Soil and Crop Sciences

    final Major mSOCR = new Major(
            new int[]{1080, 1081, 1082, 1083, 1084, 1085, 1086, 1087, 1088, 1089},
            new String[]{"SOCR-BS", "SOCR-APMZ-BS", "SOCR-APIZ-BS", "SOCR-ISCZ-BS", "SOCR-PBGZ-BS",
                    "SOCR-SOEZ-BS", "SOCR-SRNZ-BS", "SOCR-PBTZ-BS", "SOCR-SESZ-BS", "SOCR-SAMZ-BS",
                    "SOCR-DSAZ-BS"},
            "Soil and Crop Sciences",
            MathPlanConstants.PGMS + "soil-and-crop-sciences/");
    final MajorMathRequirement rSOCR = new MajorMathRequirement("SOCR-BS")
            .setSemesterCourses("M 117!,M 118!,M 124!", null, null);
                map.put(mSOCR,rSOCR);

    // Concentrations grouped into major:
    // 1081: SOCR-APMZ-BS, Soil and Crop Sciences - Agronomic Production Management (DEACTIVATED)
    // 1082: SOCR-APIZ-BS, Soil and Crop Sciences - Applied Information Technology (DEACTIVATED)
    // 1083: SOCR-ISCZ-BS, Soil and Crop Sciences - International Soil and Crop Sciences (DEACTIVATED)
    // 1084: SOCR-PBGZ-BS, Soil and Crop Sciences - Plant Biotechnology, Genetics and Breeding (DEACTIVATED)
    // 1085: SOCR-SOEZ-BS, Soil and Crop Sciences - Soil Ecology (DEACTIVATED)
    // 1086: SOCR-SRNZ-BS, Soil and Crop Sciences - Soil Restoration and Conservation (DEACTIVATED)
    // 1087: SOCR-PBTZ-BS, Soil and Crop Sciences - Plant Biotechnology
    // 1088: SOCR-SESZ-BS, Soil and Crop Sciences - Soil Science and Environmental Solutions
    // 1089: SOCR-SAMZ-BS, Soil and Crop Sciences - Sustainable Agricultural Management
    // FAKE: SOCR-DSAZ-BS, Soil and Crop Sciences - ???

    // ===================
    // College of Business
    // ===================

    // *** Major in Business Administration

    final Major mBUSA = new Major(
            new int[]{2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010},
            new String[]{"BUSA-BS", "BUSA-ACCZ-BS", "BUSA-FINZ-BS", "BUSA-FPLZ-BS", "BUSA-HRMZ-BS",
                    "BUSA-INSZ-BS", "BUSA-MKTZ-BS", "BUSA-OIMZ-BS", "BUSA-REAZ-BS", "BUSA-SCMZ-BS",
                    "BUSA-MINZ-BS", "BUSA-DACZ-BS", "BUSA-OIMZ-BS"},
            "Business Administration",
            MathPlanConstants.PGMS + "business-administration/");
    final MajorMathRequirement rBUSA = new MajorMathRequirement("BUSA-BS")
            .setSemesterCourses(MathPlanConstants.BUSA3, null, null);
                map.put(mBUSA,rBUSA);

    // Concentrations grouped into major:
    // 2001: BUSA-ACCZ-BS, Business Administration - Accounting
    // 2002: BUSA-FINZ-BS, Business Administration - Finance
    // 2003: BUSA-FPLZ-BS, Business Administration - Financial Plannin
    // 2004: BUSA-HRMZ-BS, Business Administration - Human Resource Management
    // 2005: BUSA-INSZ-BS, Business Administration - Information Systems
    // 2006: BUSA-MKTZ-BS, Business Administration - Marketing
    // 2007: BUSA-OIMZ-BS, Business Administration - Organization and Innovation Management (DEACTIVATED)
    // 2008: BUSA-REAZ-BS, Business Administration - Real Estate
    // 2009: BUSA-SCMZ-BS, Business Administration - Supply Chain Management
    // 2010: BUSA-MINZ-BS, Business Administration - Management and Innovation
    // FAKE: BUSA-DACZ-BS, Business Administration
    // FAKE: BUSA-OIMZ-BS, Business Administration

    // ======================
    // College of Engineering
    // ======================

    // *** Dual-Degree programs in Biomedical Engineering

    final Major mCBEGDUAL = new Major(
            new int[]{3000, 3001, 3002, 3003, 3004, 3005},
            new String[]{"CBEG-DUAL", "CBEG-BMEC-BS", "ELEG-BMEE-BS", "ELEG-BMEL-BS", "MECH-BMEM-BS",
                    "CPEG-BMEP-BS"},
            "Biomedical Engineering, Dual Degree",
            MathPlanConstants.PGMS + "biomedical-engineering/");
    final MajorMathRequirement rCBEGDUAL =
            new MajorMathRequirement("CBEG-DUAL")
                    .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mCBEGDUAL,rCBEGDUAL);

    // Concentrations grouped into major:
    // 3001: CBEG-BMEC-BS, Biomedical Engineering, Dual Degree - With Chemical and Biological Engineering
    // 3002: ELEG-BMEE-BS, Biomedical Engineering, Dual Degree - With Electrical Engineering (Electrical
    //                     Engineering)
    // 3003: ELEG-BMEL-BS, Biomedical Engineering, Dual Degree - With Electrical Engineering (Lasers and
    //                     Optical)
    // 3004: MECH-BMEM-BS, Biomedical Engineering, Dual Degree - With Mechanical Engineering
    // 3005: CPEG-BMEP-BS, Biomedical Engineering, Dual Degree - With Computer Engineering

    // *** Major in Chemical and Biological Engineering

    final Major mCBEG = new Major(
            new int[]{3010},
            new String[]{"CBEG-BS"},
            "Chemical and Biological Engineering",
            MathPlanConstants.PGMS + "chemical-biological-engineering/");
    final MajorMathRequirement rCBEG = new MajorMathRequirement("CBEG-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mCBEG,rCBEG);

    // *** Major in Civil Engineering

    final Major mCIVE = new Major(
            new int[]{3020},
            new String[]{"CIVE-BS"},
            "Civil Engineering",
            MathPlanConstants.PGMS + "civil-engineering/");
    final MajorMathRequirement rCIVE = new MajorMathRequirement("CIVE-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mCIVE,rCIVE);

    // *** Major in Computer Engineering

    final Major mCPEG = new Major(
            new int[]{3030, 3032, 3032, 3033, 3034},
            new String[]{"CPEG-BS", "CPEG-AESZ-BS", "CPEG-EISZ-BS", "CPEG-NDTZ-BS", "CPEG-VICZ-BS"},
            "Computer Engineering",
            MathPlanConstants.PGMS + "computer-engineering/");
    final MajorMathRequirement rCPEG = new MajorMathRequirement("CPEG-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mCPEG,rCPEG);

    // Concentrations grouped into major:
    // 3031: CPEG-AESZ-BS, Computer Engineering - Aerospace Systems
    // 3032: CPEG-EISZ-BS, Computer Engineering - Embedded and IoT Systems
    // 3033: CPEG-NDTZ-BS, Computer Engineering - Networks and Data
    // 3034: CPEG-VICZ-BS, Computer Engineering - VLSI and Integrated Circuits

    // *** Major in Electrical Engineering

    final Major mELEG = new Major(
            new int[]{3040, 3041, 3042, 3043},
            new String[]{"ELEG-BS", "ELEG-ELEZ-BS", "ELEG-LOEZ-BS", "ELEG-ASPZ-BS"},
            "Electrical Engineering",
            MathPlanConstants.PGMS + "electrical-engineering/");
    final MajorMathRequirement rELEG = new MajorMathRequirement("ELEG-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mELEG,rELEG);

    // Concentrations grouped into major:
    // 3041: ELEG-ELEZ-BS, Electrical Engineering - Electrical Engineering
    // 3042: ELEG-LOEZ-BS, Electrical Engineering - Lasers and Optical Engineering
    // 3043: ELEG-ASPZ-BS, Electrical Engineering - Aerospace

    // *** Major in Engineering Science

    // 3050: EGSC-BS, Engineering Science (DEACTIVATED)
    // 3051: EGSC-EGPZ-BS, Engineering Science - Engineering Physics (DEACTIVATED)
    // 3052: EGSC-SPEZ-BS, Engineering Science - Space Engineering (DEACTIVATED)
    // 3053: EGSC-TCEZ-BS, Engineering Science - Teacher Education (DEACTIVATED)

    // *** Dual-Degree programs in Engineering Science

    // 3060: EGIS-DUAL, Engineering Science Dual Degree (DEACTIVATED)
    // 3061: ILES-BA, Engineering Science Dual Degree - With Interdisciplinary Liberal Arts (DEACTIVATED)
    // 3062: EGIS-BS, Engineering Science Dual Degree - With International Studies (DEACTIVATED)

    // *** Major in Environmental Engineering

    final Major mENVE = new Major(
            new int[]{3070},
            new String[]{"ENVE-BS"},
            "Environmental Engineering",
            MathPlanConstants.PGMS + "environmental-engineering/");
    final MajorMathRequirement rENVE = new MajorMathRequirement("ENVE-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mENVE,rENVE);

    // *** Major in Mechanical Engineering

    final Major mMECH = new Major(
            new int[]{3080, 3081, 3082},
            new String[]{"MECH-BS", "MECH-ACEZ-BS", "MECH-ADMZ-BS"},
            "Mechanical Engineering",
            MathPlanConstants.PGMS + "mechanical-engineering/");
    final MajorMathRequirement rMECH = new MajorMathRequirement("MECH-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mMECH,rMECH);

    // Concentrations grouped into major:
    // 3081: MECH-ACEZ-BS, Mechanical Engineering - Aerospace Engineering
    // 3082: MECH-ADMZ-BS, Mechanical Engineering - Advanced Manufacturing

    // *** Major in Construction Engineering

    final Major mCONE = new Major(
            new int[]{3090},
            new String[]{"CONE-BS"},
            "Construction Engineering",
            MathPlanConstants.PGMS + "construction-engineering/");
    final MajorMathRequirement rCONE = new MajorMathRequirement("CONE-BS")
            .setSemesterCourses("M 160!", "M 161!", "M 261,M 340");
                map.put(mCONE,rCONE);

    // ====================================
    // College of Health and Human Sciences
    // ====================================

    // *** Major in Apparel and Merchandising (with three concentrations)

    final Major mAPAM = new Major(
            new int[]{4000, 4001, 4002, 4003},
            new String[]{"APAM-BS", "APAM-ADAZ-BS", "APAM-MDSZ-BS", "APAM-PDVZ-BS"},
            "Apparel and Merchandising",
            MathPlanConstants.PGMS + "apparel-and-merchandising/");
    final MajorMathRequirement rAPAM = new MajorMathRequirement("APAM-BS")
            .setSemesterCourses("M 117!,M 118!", RawRecordConstants.M124, null);
                map.put(mAPAM,rAPAM);

    // Concentrations grouped into major:
    // 4001: APAM-ADAZ-BS, Apparel and Merchandising - Apparel Design and Production
    // 4002: APAM-MDSZ-BS, Apparel and Merchandising - Merchandising
    // 4003: APAM-PDVZ-BS, Apparel and Merchandising - Product Development

    // *** Major in Construction Management

    final Major mCTMG = new Major(
            new int[]{4010},
            new String[]{"CTMG-BS", "CTM0"},
            "Construction Management",
            MathPlanConstants.PGMS + "construction-management/");
    final MajorMathRequirement rCTMG = new MajorMathRequirement("CTMG-BS")
            .setSemesterCourses("M 117!,M 118!,M 125!", null, MathPlanConstants.M_141);
                map.put(mCTMG,rCTMG);

    // Concentrations grouped into major:
    // FAKE: CTM0, "Pre-Construction Management

    // *** Major in Early Childhood Education

    final Major mECHE = new Major(
            new int[]{4020},
            new String[]{"ECHE-BS"},
            "Early Childhood Education",
            MathPlanConstants.PGMS + "early-childhood-education/");
    final MajorMathRequirement rECHE = new MajorMathRequirement("ECHE-BS")
            .setSemesterCourses(MathPlanConstants.AUCC3, null, null);
                map.put(mECHE,rECHE);

    // *** Major in Family and Consumer Sciences

    final Major mFACS = new Major(
            new int[]{4030, 4031, 4032, 4033},
            new String[]{"FACS-BS", "FACS-FACZ-BS", "FACS-FCSZ-BS", "FACS-IDSZ-BS"},
            "Family and Consumer Sciences",
            MathPlanConstants.PGMS + "family-consumer-sciences/");
    final MajorMathRequirement rFACS = new MajorMathRequirement("FACS-BS")
            .setSemesterCourses("AUCC3!", null, null);
                map.put(mFACS,rFACS);

    // Concentrations grouped into major:
    // 4031: FACS-FACZ-BS, Family and Consumer Sciences/Family and Consumer Sciences (DEACTIVATED)
    // 4032: FACS-FCSZ-BS, Family and Consumer Sciences - Family and Consumer Sciences Education
    // 4033: FACS-IDSZ-BS, Family and Consumer Sciences - Interdisciplinary

    // *** Major in Fermentation Science and Technology

    final Major mFMST = new Major(
            new int[]{4040},
            new String[]{"FMST-BS"},
            "Fermentation Science and Technology",
            MathPlanConstants.PGMS + "fermentation-and-food-science/");
    final MajorMathRequirement rFMST = new MajorMathRequirement("FMST-BS")
            .setSemesterCourses("M 117!,M 118!,M 124!", "M 125!", null);
                map.put(mFMST,rFMST);

    // *** Major in Health and Exercise Science

    final Major mHAES = new Major(
            new int[]{4050, 4051, 4052},
            new String[]{"HAES-BS", "HAES-HPRZ-BS", "HAES-SPMZ-BS"},
            "Health and Exercise Science",
            MathPlanConstants.PGMS + "health-and-exercise-science/");
    final MajorMathRequirement rHAES = new MajorMathRequirement("HAES-BS")
            .setSemesterCourses("M 118.,M 124.", "M 125!", null);
                map.put(mHAES,rHAES);

    // Concentrations grouped into major:
    // 4051: HAES-HPRZ-BS, Health and Exercise Science - Health Promotion
    // 4052: HAES-SPMZ-BS, Health and Exercise Science - Sports Medicine

    // *** Major in Hospitality Management

    final Major mHSMG = new Major(
            new int[]{4060},
            new String[]{"HSMG-BS"},
            "Hospitality and Event Management",
            MathPlanConstants.PGMS + "hospitality-event-management/");
    final MajorMathRequirement rHSMG = new MajorMathRequirement("HSMG-BS")
            .setSemesterCourses("M 101,M 117!", null, null);
                map.put(mHSMG,rHSMG);

    // *** Major in Human Development and Family Studies

    final Major mHDFS = new Major(
            new int[]{4070, 4071, 4072, 4073, 4074, 4075, 4076},
            new String[]{"HDFS-BS", "HDFS-ECPZ-BS", "HDFS-HDEZ-BS", "HDFS-PHPZ-BS", "HDFS-PHPZ-BS",
                    "HDFS-PISZ-BS", "HDFS-LADZ-BS", "HDFS-DECZ-BS", "HDFS-DHDZ-BS", "HDFS-DPHZ-BS",
                    "HDFS-DPIZ-BS", "HDFS-DLAZ-BS", "HDFS-DLEZ-BS", "HDFS-LEPZ-BS"},
            "Human Development and Family Studies",
            MathPlanConstants.PGMS + "human-development-and-family-studies/");
    final MajorMathRequirement rHDFS = new MajorMathRequirement("HDFS-BS")
            .setSemesterCourses(MathPlanConstants.AUCC3, null, null);
                map.put(mHDFS,rHDFS);

    // Concentrations grouped into major:
    // 4071: HDFS-ECPZ-BS, Human Development and Family Studies - Early Childhood Profession
    // 4072: HDFS-HDEZ-BS, Human Development and Family Studies - Human Development and Family Studies
    // 4073: HDFS-PHPZ-BS, Human Development and Family Studies - Leadership and Entrepreneurial
    //                     Professions (DEACTIVATED)
    // 4074: HDFS-PHPZ-BS, Human Development and Family Studies - Pre-Health Professions
    // 4075: HDFS-PISZ-BS, Human Development and Family Studies - Prevention and Intervention Sciences
    // 4076: HDFS-LADZ-BS, Human Development and Family Studies - Leadership and Advocacy
    // FAKE: HDFS-DECZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-DHDZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-DPHZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-DPIZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-DLAZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-DLEZ-BS, Human Development and Family Studies - ???
    // FAKE: HDFS-LEPZ-BS, Human Development and Family Studies - ???

    // *** Major in Interior Architecture and Design

    final Major mIARD = new Major(
            new int[]{4081, 4080},
            new String[]{"IARD-BS", "INTD-BS"},
            "Interior Architecture and Design",
            MathPlanConstants.PGMS + "interior-architecture-and-design/");
    final MajorMathRequirement rIARD = new MajorMathRequirement("IARD-BS")
            .setSemesterCourses("M 117.,M 118.", "M 124.", null);
                map.put(mIARD,rIARD);

    // Concentrations grouped into major:
    // 4080: INTD-BS, Interior Architecture and Design (DEACTIVATED)

    // *** Major in Nutrition Science

    final Major mNAFS = new Major(
            new int[]{4090, 4091, 4092, 4093, 4094, 4095, 4096, 4097},
            new String[]{"NAFS-BS", "NAFS-DNMZ-BS", "NAFS-FSNZ-BS", "NAFS-NFTZ-BS", "NAFS-NUSZ-BS",
                    "NAFS-FSYZ-BS", "NAFS-FSCZ-BS", "NAFS-PHNZ-BS", "NAFS-DNRZ-BS", "NAFS-GLTZ-BS",
                    "NAFS-NRTZ-BS", "NAFS-CPSY-BS"},
            "Nutrition Science",
            MathPlanConstants.PGMS + "nutrition-science/");
    final MajorMathRequirement rNAFS = new MajorMathRequirement("NAFS-BS")
            .setSemesterCourses("M 117!,M 118!,M 124!", null, null);
                map.put(mNAFS,rNAFS);

    // Concentrations grouped into major:
    // 4091: NAFS-DNMZ-BS, Nutrition and Food Science - Dietetics and Nutrition Management
    // 4092: NAFS-FSNZ-BS, Nutrition and Food Science - Food Safety and Nutrition (DEACTIVATED)
    // 4093: NAFS-NFTZ-BS, Nutrition and Food Science - Nutrition and Fitness
    // 4094: NAFS-NUSZ-BS, Nutrition and Food Science - Nutritional Sciences (DEACTIVATED)
    // 4095: NAFS-FSYZ-BS, Nutrition and Food Science - Food Systems (DEACTIVATED)
    // 4096: NAFS-FSCZ-BS, Nutrition and Food Science - Food Science
    // 4097: NAFS-PHNZ-BS, Nutrition and Food Science - Pre-Health Nutrition
    // FAKE: NAFS-DNRZ-BS, Nutrition and Food Science - ???
    // FAKE: NAFS-GLTZ-BS, Nutrition and Food Science - ???
    // FAKE: NAFS-NRTZ-BS, Nutrition and Food Science - ???
    // FAKE: NAFS-CPSY-BS, Nutrition and Food Science - ???

    // *** Major in Social Work

    final Major mSOWK = new Major(
            new int[]{4100, 4101},
            new String[]{"SOWK-BSW", "SOWK-ADSZ-BSW"},
            "Social Work",
            MathPlanConstants.PGMS + "social-work/");
    final MajorMathRequirement rSOWK =
            new MajorMathRequirement("SOWK-BSW")
                    .setSemesterCourses("AUCC3!", null, null);
                map.put(mSOWK,rSOWK);

    // Concentrations grouped into major:
    // 4101: SOWK-ADSZ-BSW, Social Work - Addictions Counseling

    // =======================
    // College of Liberal Arts
    // =======================

    // *** Major in Anthropology

    final Major mANTH = new Major(
            new int[]{5000, 5001, 5002, 5003},
            new String[]{"ANTH-BA", "ANTH-ARCZ-BA", "ANTH-BIOZ-BA", "ANTH-CLTZ-BA", "ANTH-DD-BA"},
            "Anthropology",
            MathPlanConstants.PGMS + "anthropology/");
    final MajorMathRequirement rANTH = new MajorMathRequirement("ANTH-BA")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mANTH,rANTH);

    // Concentrations grouped into major:
    // 5001: ANTH-ARCZ-BA, Anthropology - Archaeology
    // 5002: ANTH-BIOZ-BA, Anthropology - Biological Anthropology
    // 5003: ANTH-CLTZ-BA, Anthropology - Cultural Anthropology
    // FAKE: ANTH-DD-BA, Anthropology - Dual Degree

    // *** Major in Art (B.A. and B.F.A combined)

    final Major mARTI = new Major(
            new int[]{5010, 5012, 5013, 5020, 5021, 5022, 5023, 5024, 5025, 5027, 5026, 5028, 5029, 5030,
                    5031},
            new String[]{"ARTI-BA", "ARTI-ARTZ-BA", "ARTI-IVSZ-BA", "ARTM-BFA", "ARTM-DRAZ-BF",
                    "ARTM-ELAZ-BF", "ARTM-FIBZ-BF", "ARTM-GRDZ-BF", "ARTM-METZ-BF", "ARTM-PNTZ-BF",
                    "ARTM-PHIZ-BF", "ARTM-POTZ-BF", "ARTM-PRTZ-BF", "ARTM-SCLZ-BF", "ARTM-AREZ-BF"},
            "Art",
            MathPlanConstants.PGMS + "art-bfa/");
    final MajorMathRequirement rARTI = new MajorMathRequirement("ARTI-BA")
            .setSemesterCourses(null, MathPlanConstants.AUCC3, null);
                map.put(mARTI,rARTI);

    // Concentrations (and BFA) grouped into BA major:
    // 5012: ARTI-ARTZ-BA - Art, B.A. - Art History
    // 5013: ARTI-IVSZ-BA, Art, B.A. - Integrated Visual Studies
    // 5020: ARTM-BFA, Art, B.F.A.
    // 5021: ARTM-DRAZ-BF, Art, B.F.A. - Drawing
    // 5022: ARTM-ELAZ-BF, Art, B.F.A. - Electronic Art
    // 5023: ARTM-FIBZ-BF, Art, B.F.A. - Fibers
    // 5024: ARTM-GRDZ-BF, Art, B.F.A. - Graphic Design
    // 5025: ARTM-METZ-BF, Art, B.F.A. - Metalsmithing
    // 5027: ARTM-PNTZ-BF, Art, B.F.A. - Painting
    // 5026: ARTM-PHIZ-BF, Art, B.F.A. - Photo Image Making
    // 5028: ARTM-POTZ-BF, Art, B.F.A. - Pottery
    // 5029: ARTM-PRTZ-BF, Art, B.F.A. - Printmaking
    // 5030: ARTM-SCLZ-BF, Art, B.F.A. - Sculpture
    // 5031: ARTM-AREZ-BF, Art, B.F.A. - Art Education

    // *** Major in Communication Studies

    final Major mCMST = new Major(
            new int[]{5040, 5041},
            new String[]{"CMST-BA", "CMST-TCLZ-BA", "CMST-DD-BA"},
            "Communication Studies",
            MathPlanConstants.PGMS + "communication-studies/");
    final MajorMathRequirement rCMST = new MajorMathRequirement("CMST-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mCMST,rCMST);

    // Concentrations (and BFA) grouped into BA major:
    // 5041: CMST-TCLZ-BA, Communication Studies - Speech Teacher Licensure
    // FAKE: CMST-DD-BA, Communication Studies - Dual Degree

    // *** Major in Dance (BA)

    final Major mDNCE = new Major(
            new int[]{5050, 5051},
            new String[]{"DNCE-BA", "DANC-BFA", "DNC0", "DANC-DEDZ-BF"},
            "Dance",
            MathPlanConstants.PGMS + "dance-ba/");
    final MajorMathRequirement rDNCE = new MajorMathRequirement("DNCE-BA")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mDNCE,rDNCE);

    // BFA grouped into BA major:
    // 5050: DANC-BFA, Dance
    // FAKE: DNC0, Pre-Dance
    // FAKE: DANC-DEDZ-BF, Dance - ???

    // *** Major in Economics

    final Major mECON = new Major(
            new int[]{5060},
            new String[]{"ECON-BA", "ECON-DD-BA"},
            "Economics",
            MathPlanConstants.PGMS + "economics/");
    final MajorMathRequirement rECON = new MajorMathRequirement("ECON-BA")
            .setSemesterCourses(null, "CALC!", null);
                map.put(mECON,rECON);

    // Concentrations grouped into major:
    // FAKE: ECON-DD-BA, Economics - Dual degree

    // *** Major in English

    final Major mENGL = new Major(
            new int[]{5070, 5071, 5072, 5073, 5074, 5075, 5076},
            new String[]{"ENGL-BA", "ENGL-CRWZ-BA", "ENGL-ENEZ-BA", "ENGL-LANZ-BA", "ENGL-LITZ-BA",
                    "ENGL-WRLZ-BA", "ENGL-LINZ-BA", "ENGL-LANZ-BA"},
            "English",
            MathPlanConstants.PGMS + "english/");
    final MajorMathRequirement rENGL = new MajorMathRequirement("ENGL-BA")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mENGL,rENGL);

    // Concentrations grouped into major:
    // 5071: ENGL-CRWZ-BA, English - Creative Writing
    // 5072: ENGL-ENEZ-BA, English - English Education
    // 5073: ENGL-LANZ-BA, English/Language (DEACTIVATED)
    // 5074: ENGL-LITZ-BA, English - Literature
    // 5075: ENGL-WRLZ-BA, English - Writing, Rhetoric and Literacy
    // 5076: ENGL-LINZ-BA, English - Linguistics
    // FAKE: ENGL-LANZ-BA, English - ???

    // *** Major in Ethnic Studies

    final Major mETST = new Major(
            new int[]{5080, 5081, 5082, 5083},
            new String[]{"ETST-BA", "ETST-SOTZ-BA", "ETST-COIZ-BA", "ETST-RPRZ-BA"},
            "Ethnic Studies",
            MathPlanConstants.PGMS + "ethnic-studies/");
    final MajorMathRequirement rETST = new MajorMathRequirement("ETST-BA")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mETST,rETST);

    // Concentrations grouped into major:
    // 5081: ETST-SOTZ-BA, Ethnic Studies - Social Studies Teaching
    // 5082: ETST-COIZ-BA, Ethnic Studies - Community Organizing and Institutional Change
    // 5083: ETST-RPRZ-BA, Ethnic Studies - Global Race, Power, and Resistance

    // *** Major in Geography

    final Major mGEOG = new Major(
            new int[]{5085},
            new String[]{"GEOG-BS"},
            "Geography",
            MathPlanConstants.PGMS + "geography/");
    final MajorMathRequirement rGEOG = new MajorMathRequirement("GEOG-BS")
            .setSemesterCourses(MathPlanConstants.AUCC3, null, null);
                map.put(mGEOG,rGEOG);

    // *** Major in History

    final Major mHIST = new Major(
            new int[]{5090, 5091, 5092, 5093, 5094, 5095},
            new String[]{"HIST-BA", "HIST-GENZ-BA", "HIST-LNGZ-BA", "HIST-SBSZ-BA", "HIST-SSTZ-BA",
                    "HIST-DPUZ-BA"},
            "History",
            MathPlanConstants.PGMS + "history/");
    final MajorMathRequirement rHIST = new MajorMathRequirement("HIST-BA")
            .setSemesterCourses(MathPlanConstants.AUCC3, null, null);
                map.put(mHIST,rHIST);

    // Concentrations grouped into major:
    // 5091: HIST-GENZ-BA, History - General History
    // 5092: HIST-LNGZ-BA, History - Language
    // 5093: HIST-SBSZ-BA, History - Social and Behavioral Sciences
    // 5094: HIST-SSTZ-BA, History - Social Studies Teaching
    // 5095: HIST-DPUZ-BA, History - Digital and Public History

    // *** Major in Journalism and Media Communication

    final Major mJAMC = new Major(
            new int[]{5100},
            new String[]{"JAMC-BA", "JAMC-DD-BA"},
            "Journalism and Media Communication",
            MathPlanConstants.PGMS + "journalism-and-media-communication/");
    final MajorMathRequirement rJAMC = new MajorMathRequirement("JAMC-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mJAMC,rJAMC);

    // Concentrations grouped into major:
    // FAKE: JAMC-DD-BA, Journalism and Media Communication - Dual degree

    // *** Major in Languages, Literatures and Cultures

    final Major mLLAC = new Major(
            new int[]{5110, 5111, 5112, 5113, 5114},
            new String[]{"LLAC-BA", "LLAC-LFRZ-BA", "LLAC-LGEZ-BA", "LLAC-LSPZ-BA", "LLAC-SPPZ-BA"},
            "Languages, Literatures and Cultures",
            MathPlanConstants.PGMS + "languages-literatures-and-cultures/");
    final MajorMathRequirement rLLAC = new MajorMathRequirement("LLAC-BA")
            .setSemesterCourses(null, null, MathPlanConstants.AUCC3);
                map.put(mLLAC,rLLAC);

    // Concentrations grouped into major:
    // 5111: LLAC-LFRZ-BA, Languages, Literatures and Cultures - French
    // 5112: LLAC-LGEZ-BA, Languages, Literatures and Cultures - German
    // 5113: LLAC-LSPZ-BA, Languages, Literatures and Cultures - Spanish
    // 5114: LLAC-SPPZ-BA, Languages, Literatures and Cultures - Spanish for the Professions

    // *** Major in Music, B.A.

    final Major mMUSI = new Major(
            new int[]{5120, 5130, 5131, 5132, 5133, 5134},
            new String[]{"MUSI-BA", "MUSC-BM", "MUSC-COMZ-BM", "MUSC-MUEZ-BM", "MUSC-MUTZ-BM",
                    "MUSC-PERZ-BM", "MUS0"},
            "Music",
            MathPlanConstants.PGMS + "music-ba/");
    final MajorMathRequirement rMUSI = new MajorMathRequirement("MUSI-BA")
            .setSemesterCourses("AUCC3!", null, null);
                map.put(mMUSI,rMUSI);

    // Concentrations (and BM) grouped into BA major:
    // 5130: MUSC-BM, Music (B.M.)
    // 5131: MUSC-COMZ-BM, Music (B.M.) - Composition
    // 5132: MUSC-MUEZ-BM, Music (B.M.) - Music Education
    // 5133: MUSC-MUTZ-BM, Music (B.M.) - Music Therapy
    // 5134: MUSC-PERZ-BM, Music (B.M.) - Performance
    // FAKE: MUS0, Pre-Music

    // *** Major in Philosophy

    final Major mPHIL = new Major(
            new int[]{5140, 5141, 5142, 5143},
            new String[]{"PHIL-BA", "PHIL-GNPZ-BA", "PHIL-GPRZ-BA", "PHIL-PSAZ-BA"},
            "Philosophy",
            MathPlanConstants.PGMS + "philosophy/");
    final MajorMathRequirement rPHIL = new MajorMathRequirement("PHIL-BA")
            .setSemesterCourses(MathPlanConstants.AUCC3, null, null);
                map.put(mPHIL,rPHIL);

    // Concentrations grouped into major:
    // 5141: PHIL-GNPZ-BA, Philosophy - General Philosophy
    // 5142: PHIL-GPRZ-BA, Philosophy - Global Philosophies and Religions
    // 5143: PHIL-PSAZ-BA, Philosophy - Philosophy, Science, and Technology

    // *** Major in Political Science

    final Major mPOLS = new Major(
            new int[]{5150, 5151, 5152, 5153},
            new String[]{"POLS-BA", "POLS-EPAZ-BA", "POLS-GPPZ-BA", "POLS-ULPZ-BA", "POLS-DD-BA"},
            "Political Science",
            MathPlanConstants.PGMS + "political-science/");
    final MajorMathRequirement rPOLS = new MajorMathRequirement("POLS-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mPOLS,rPOLS);

    // Concentrations grouped into major:
    // 5151: POLS-EPAZ-BA, Political Science - Environmental Politics and Policy
    // 5152: POLS-GPPZ-BA, Political Science - Global Politics and Policy
    // 5153: POLS-ULPZ-BA, Political Science - U.S. Government, Law, and Policy
    // FAKE: POLS-DD-BA, Political Science - Dual Degree

    // *** Major in Sociology

    final Major mSOCI = new Major(
            new int[]{5160, 5161, 5162, 5163},
            new String[]{"SOCI-BA", "SOCI-CRCZ-BA", "SOCI-ENSZ-BA", "SOCI-GNSZ-BA", "SOCI-DGSZ-BA"},
            "Sociology",
            MathPlanConstants.PGMS + "sociology/");
    final MajorMathRequirement rSOCI = new MajorMathRequirement("SOCI-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mSOCI,rSOCI);

    // Concentrations grouped into major:
    // 5161: SOCI-CRCZ-BA, Sociology - Criminology and Criminal Justice
    // 5162: SOCI-ENSZ-BA, Sociology - Environmental Sociology
    // 5163: SOCI-GNSZ-BA, Sociology - General Sociology
    // FAKE: SOCI-DGSZ-BA, Sociology - ???

    // *** Major in Theatre

    final Major mTHTR = new Major(
            new int[]{5170, 5171, 5172, 5173, 5174, 5175, 5176, 5177, 5178, 5179},
            new String[]{"THTR-BA", "THTR-DTHZ-BA", "THTR-GTRZ-BA", "THTR-PRFZ-BA", "THTR-LDTZ-BA",
                    "THTR-MUSZ-BA", "THTR-PDTZ-BA", "THTR-SDSZ-BA", "THTR-SDTZ-BA", "THTR-CDTZ-BA", "THR0"},
            "Theatre",
            MathPlanConstants.PGMS + "theatre/");
    final MajorMathRequirement rTHTR = new MajorMathRequirement("THTR-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mTHTR,rTHTR);

    // Concentrations grouped into major:
    // 5171: THTR-DTHZ-BA, Theatre/Design and Technology (DEACTIVATED)
    // 5172: THTR-GTRZ-BA, Theatre/General Theatre (DEACTIVATED)
    // 5173: THTR-PRFZ-BA, Theatre - Performance
    // 5174: THTR-LDTZ-BA, Theatre - Lighting Design and Technology
    // 5175: THTR-MUSZ-BA, Theatre - Musical Theatre
    // 5176: THTR-PDTZ-BA, Theatre - Projection Design and Technology
    // 5177: THTR-SDSZ-BA, Theatre - Set Design
    // 5178: THTR-SDTZ-BA, Theatre - Sound Design and Technology
    // 5179: THTR-CDTZ-BA, Theatre - Costume Design and Technology
    // FAKE: THR0, Pre-Theatre

    // *** Major in Women's and Gender Studies

    final Major mWGST = new Major(
            new int[]{5180},
            new String[]{"WGST-BA"},
            "Women's and Gender Studies",
            MathPlanConstants.PGMS + "womens-and-gender-studies/");
    final MajorMathRequirement rWGST = new MajorMathRequirement("WGST-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mWGST,rWGST);

    // *** Major in International Studies

    final Major mINST = new Major(
            new int[]{5190, 5191, 5192, 5193, 5194, 5195},
            new String[]{"INST-BA", "INST-ASTZ-BA", "INST-EUSZ-BA", "INST-LTSZ-BA", "INST-MEAZ-BA",
                    "INST-GBLZ-BA"},
            "International Studies",
            MathPlanConstants.PGMS + "international-studies/");
    final MajorMathRequirement rINST = new MajorMathRequirement("INST-BA")
            .setSemesterCourses(null, "AUCC3!", null);
                map.put(mINST,rINST);

    // Concentrations grouped into major:
    // 5191: INST-ASTZ-BA, International Studies - Asian Studies
    // 5192: INST-EUSZ-BA, International Studies - European Studies
    // 5193: INST-LTSZ-BA, International Studies - Latin American Studies
    // 5194: INST-MEAZ-BA, International Studies - Middle East and North African Studies
    // 5195: INST-GBLZ-BA, International Studies - Global Studies

    // *** Major in Interdisciplinary Liberal Arts

    final Major mILAR = new Major(
            new int[]{5200},
            new String[]{"ILAR-BA", "ILAR-DD-BA"},
            "Interdisciplinary Liberal Arts",
            MathPlanConstants.PGMS + "interdisciplinary-liberal-arts/");
    final MajorMathRequirement rILAR = new MajorMathRequirement("ILAR-BA")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mILAR,rILAR);

    // Concentrations grouped into major:
    // FAKE: ILAR-DD-BA, Interdisciplinary Liberal Arts - Dual degree

    // ============================
    // College of Natural Resources
    // ============================

    // *** Major in Ecosystem Science and Sustainability

    final Major mECSS = new Major(
            new int[]{6000},
            new String[]{"ECSS-BS"},
            "Ecosystem Science and Sustainability",
            MathPlanConstants.PGMS + "ecosystem-science-sustainability/");
    final MajorMathRequirement rECSS = new MajorMathRequirement("ECSS-BS")
            .setSemesterCourses(null, MathPlanConstants.CALC1BIO, null);
                map.put(mECSS,rECSS);

    // *** Major in Fish, Wildlife and Conservation Biology

    final Major mFWCB = new Major(
            new int[]{6010, 6011, 6012, 6013},
            new String[]{"FWCB-BS", "FWCB-CNVZ-BS", "FWCB-FASZ-BS", "FWCB-WDBZ-BS"},
            "Fish, Wildlife and Conservation Biology",
            MathPlanConstants.PGMS + "fish-wildlife-and-conservation-biology/");
    final MajorMathRequirement rFWCB = new MajorMathRequirement("FWCB-BS")
            .setSemesterCourses("M 117!,M 118!,M 124!", "M 125!", MathPlanConstants.CALC1BIO);
                map.put(mFWCB,rFWCB);

    // Concentrations grouped into major:
    // 6011: FWCB-CNVZ-BS, Fish, Wildlife and Conservation Biology - Conservation Biology
    // 6012: FWCB-FASZ-BS, Fish, Wildlife and Conservation Biology - Fisheries and Aquatic Sciences
    // 6013: FWCB-WDBZ-BS, Fish, Wildlife and Conservation Biology - Wildlife Biology

    // *** Major in Forest and Rangeland Stewardship

    final Major mFRRS = new Major(
            new int[]{6080, 6081, 6082, 6083, 6084, 6085},
            new String[]{"FRRS-BS", "FRRS-FRBZ-BS", "FRRS-FRFZ-BS", "FRRS-FMGZ-BS", "FRRS-RFMZ-BS",
                    "FRRS-RCMZ-BS"},
            "Forest and Rangeland Stewardship",
            MathPlanConstants.PGMS + "forest-and-rangeland-stewardship/");
    final MajorMathRequirement rFRRS = new MajorMathRequirement("FRRS-BS")
            .setSemesterCourses("M 117!,M 118!,M 125", "M 141", null);
                map.put(mFRRS,rFRRS);

    // Concentrations grouped into major:
    // 6081: FRRS-FRBZ-BS, Forest and Rangeland Stewardship - Forest Biology
    // 6082: FRRS-FRFZ-BS, Forest and Rangeland Stewardship - Forest Fire Science
    // 6083: FRRS-FMGZ-BS, Forest and Rangeland Stewardship - Forest Management
    // 6084: FRRS-RFMZ-BS, Forest and Rangeland Stewardship - Rangeland and Forest Management
    // 6085: FRRS-RCMZ-BS, Forest and Rangeland Stewardship - Rangeland Conservation and Management

    // *** Major in Geology

    final Major mGEOL = new Major(
            new int[]{6020, 6021, 6022, 6023, 6024},
            new String[]{"GEOL-BS", "GEOL-EVGZ-BS", "GEOL-GEOZ-BS", "GEOL-GPYZ-BS", "GEOL-HYDZ-BS"},
            "Geology",
            MathPlanConstants.PGMS + "geology/");
    final MajorMathRequirement rGEOL = new MajorMathRequirement("GEOL-BS")
            .setSemesterCourses("M 124!,M 125!,M 126.", "M 160.", MathPlanConstants.M_161);
                map.put(mGEOL,rGEOL);

    // Concentrations grouped into major:
    // 6021: GEOL-EVGZ-BS, Geology - Environmental Geology
    // 6022: GEOL-GEOZ-BS, Geology - Geology
    // 6023: GEOL-GPYZ-BS, Geology - Geophysics
    // 6024: GEOL-HYDZ-BS, Geology - Hydrogeology

    // *** Major in Human Dimensions of Natural Resources

    final Major mHDNR = new Major(
            new int[]{6030},
            new String[]{"HDNR-BS"},
            "Human Dimensions of Natural Resources",
            MathPlanConstants.PGMS + "human-dimensions-of-natural-resources/");
    final MajorMathRequirement rHDNR = new MajorMathRequirement("HDNR-BS")
            .setSemesterCourses("M 117!,M 118!,M 124.", null, null);
                map.put(mHDNR,rHDNR);

    // *** Major in Natural Resource Tourism

    final Major mNRRT = new Major(
            new int[]{6040, 6041, 6042},
            new String[]{"NRRT-BS", "NRRT-GLTZ-BS", "NRRT-NRTZ-BS"},
            "Natural Resource Tourism",
            MathPlanConstants.PGMS + "natural-resource-tourism/");
    final MajorMathRequirement rNRRT = new MajorMathRequirement("NRRT-BS")
            .setSemesterCourses("M 117!,M 118!,M 124.", null, null);
                map.put(mNRRT,rNRRT);

    // Concentrations grouped into major:
    // 6041: NRRT-GLTZ-BS, Natural Resource Tourism - Global Tourism
    // 6042: NRRT-NRTZ-BS, Natural Resource Tourism - Natural Resource Tourism

    // *** Major in Natural Resources Management

    final Major mNRMG = new Major(
            new int[]{6050},
            new String[]{"NRMG-BS"},
            "Natural Resources Management",
            MathPlanConstants.PGMS + "natural-resources-management/");
    final MajorMathRequirement rNRMG = new MajorMathRequirement("NRMG-BS")
            .setSemesterCourses("M 117!,M 118!,M 125!", null, null);
                map.put(mNRMG,rNRMG);

    // *** Major in Restoration Ecology

    final Major mRECO = new Major(
            new int[]{6060},
            new String[]{"RECO-BS"},
            "Restoration Ecology",
            MathPlanConstants.PGMS + "restoration-ecology/");
    final MajorMathRequirement rRECO = new MajorMathRequirement("RECO-BS")
            .setSemesterCourses("FRRS3!", null, null);
                map.put(mRECO,rRECO);

    // *** Major in Watershed Science and Sustainability

    final Major mWRSC = new Major(
            new int[]{6070, 6071, 6072, 6073},
            new String[]{"WRSC-BS", "WRSC-WSDZ-BS", "WRSC-WSSZ-BS", "WRSC-WSUZ-BS", "WSSS-BS"},
            "Watershed Science and Sustainability",
            MathPlanConstants.PGMS + "watershed-science/");
    final MajorMathRequirement rWRSC = new MajorMathRequirement("WRSC-BS")
            .setSemesterCourses("CALC1BIO!", null, null);
                map.put(mWRSC,rWRSC);

    // Concentrations grouped into major:
    // 6071: WRSC-WSDZ-BS, Watershed Science and Sustainability - Watershed Data
    // 6072: WRSC-WSSZ-BS, Watershed Science and Sustainability - Watershed Science
    // 6073: WRSC-WSUZ-BS, Watershed Science and Sustainability - Watershed Sustainability
    // FAKEL WSSS-BS, Watershed Science and Sustainability

    // ===========================
    // College of Natural Sciences
    // ===========================

    // *** Major in Biochemistry

    final Major mBCHM = new Major(
            new int[]{7010, 7012, 7013, 7014, 7015},
            new String[]{"BCHM-BS", "BCHM-HMSZ-BS", "BCHM-PPHZ-BS", "BCHM-ASBZ-BS", "BCHM-DTSZ-BS",
                    "BCHM-GBCZ-BS"},
            "Biochemistry",
            MathPlanConstants.PGMS + "biochemistry/");
    final MajorMathRequirement rBCHM = new MajorMathRequirement("BCHM-BS")
            .setSemesterCourses("CALC1BIO!", "CALC2BIO!", null);
                map.put(mBCHM,rBCHM);

    // Concentrations grouped into major:
    // 7012: BCHM-HMSZ-BS, Biochemistry - Health and Medical Sciences
    // 7013: BCHM-PPHZ-BS, Biochemistry - Pre-Pharmacy
    // 7014: BCHM-ASBZ-BS, Biochemistry - ASBMB
    // 7015: BCHM-DTSZ-BS, Biochemistry - Data Science
    // FAKE: BCHM-GBCZ-BS, Biochemistry - General Biochemistry

    // *** Major in Biological Science

    final Major mBLSC = new Major(
            new int[]{7020, 7021, 7022},
            new String[]{"BLSC-BS", "BLSC-BLSZ-BS", "BLSC-BTNZ-BS"},
            "Biology",
            MathPlanConstants.PGMS + "biology/");
    final MajorMathRequirement rBLSC = new MajorMathRequirement("BLSC-BS")
            .setSemesterCourses("M 117.,M 118.", "M 124.,M 125.,CALC1BIO!", null);
                map.put(mBLSC,rBLSC);

    // Concentrations grouped into major:
    // 7021: BLSC-BLSZ-BS, Biological Science - Biological Science
    // 7022: BLSC-BTNZ-BS, Biological Science - Botany

    // *** Major in Chemistry

    final Major mCHEM = new Major(
            new int[]{7030, 7031, 7032, 7033, 7034, 7035, 7036},
            new String[]{"CHEM-BS", "CHEM-ACSZ-BS", "CHEM-NACZ-BS", "CHEM-ECHZ-BS", "CHEM-FCHZ-BS",
                    "CHEM-HSCZ-BS", "CHEM-SCHZ-BS"},
            "Chemistry",
            MathPlanConstants.PGMS + "chemistry/");
    final MajorMathRequirement rCHEM = new MajorMathRequirement("CHEM-BS")
            .setSemesterCourses(null, "CALC1BIO!", "CALC2CHM,CALC3CHM");
                map.put(mCHEM,rCHEM);

    // Concentrations grouped into major:
    // 7031: CHEM-ACSZ-BS, Chemistry/ACS Certified (DEACTIVATED)
    // 7032: CHEM-NACZ-BS, Chemistry/Non-ACS Certified (DEACTIVATED)
    // 7033: CHEM-ECHZ-BS, Chemistry - Environmental Chemistry
    // 7034: CHEM-FCHZ-BS, Chemistry - Forensic Chemistry
    // 7035: CHEM-HSCZ-BS, Chemistry - Health Sciences
    // 7036: CHEM-SCHZ-BS, Chemistry - Sustainable Chemistry

    // *** Major in Computer Science

    final Major mCPSC = new Major(
            new int[]{7040, 7041, 7042, 7043, 7044, 7045, 7046, 7047},
            new String[]{"CPSC-BS", "CPSC-CPSZ-BS", "CPSC-HCCZ-BS", "CPSC-AIMZ-BS", "CPSC-CSYZ-BS",
                    "CPSC-NSCZ-BS", "CPSC-SEGZ-BS", "CPSC-CSEZ-BS", "APCT-CPTZ-BS", "CPSC-CFCZ-BS",
                    "CPSC-DAIZ-BS", "CPSC-DCSZ-BS", "CPSC-DCYZ-BS", "CPSC-DHCZ-BS", "CPSC-DNSZ-BS",
                    "CPSC-DSEZ-BS"},
            "Computer Science",
            MathPlanConstants.PGMS + "computer-science/");
    final MajorMathRequirement rCPSC = new MajorMathRequirement("CPSC-BS")
            .setSemesterCourses("M 124!,M 126!", MathPlanConstants.CALC1CS, MathPlanConstants.LINALG369);
                map.put(mCPSC,rCPSC);

    // Concentrations grouped into major:
    // 7041: CPSC-CPSZ-BS, Computer Science - Computer Science
    // 7042: CPSC-HCCZ-BS, Computer Science - Human-Centered Computing
    // 7043: CPSC-AIMZ-BS, Computer Science - Artificial Intelligence and Machine Learning
    // 7044: CPSC-CSYZ-BS, Computer Science - Computing Systems
    // 7045: CPSC-NSCZ-BS, Computer Science - Networks and Security
    // 7046: CPSC-SEGZ-BS, Computer Science - Software Engineering
    // 7047: CPSC-CSEZ-BS, Computer Science - Computer Science Education
    // FAKE: APCT-CPTZ-BS, Computer Science - Applied Computing Technology
    // FAKE: CPSC-CFCZ-BS, Computer Science - ???
    // FAKE: CPSC-DAIZ-BS, Computer Science - ???
    // FAKE: CPSC-DCSZ-BS, Computer Science - ???
    // FAKE: CPSC-DCYZ-BS, Computer Science - ???
    // FAKE: CPSC-DCYZ-BS, Computer Science - ???
    // FAKE: CPSC-DHCZ-BS, Computer Science - ???
    // FAKE: CPSC-DNSZ-BS, Computer Science - ???
    // FAKE: CPSC-DSEZ-BS, Computer Science - ???

    // *** Major in Data Science

    final Major mDSCI = new Major(
            new int[]{7050, 7051, 7052, 7053, 7054, 7055},
            new String[]{"DSCI-BS", "DSCI-CSCZ-BS", "DSCI-ECNZ-BS", "DSCI-MATZ-BS", "DSCI-STSZ-BS",
                    "DSCI-NEUZ-BS"},
            "Data Science",
            MathPlanConstants.PGMS + "data-science/");
    final MajorMathRequirement rDSCI = new MajorMathRequirement("DSCI-BS")
            .setSemesterCourses(MathPlanConstants.M_156, MathPlanConstants.D_369, "M 151,M 256");
                map.put(mDSCI,rDSCI);

    // Concentrations grouped into major:
    // 7051: DSCI-CSCZ-BS, Data Science - Computer Science
    // 7052: DSCI-ECNZ-BS, Data Science - Economics
    // 7053: DSCI-MATZ-BS, Data Science - Mathematics
    // 7054: DSCI-STSZ-BS, Data Science - Statistics
    // 7055: DSCI-NEUZ-BS, Data Science - Neuroscience

    // *** Major in Mathematics

    final Major mMATH = new Major(
            new int[]{7060, 7061, 7062, 7063, 7064, 7065, 7066},
            new String[]{"MATH-BS", "MATH-ALSZ-BS", "MATH-AMTZ-BS", "MATH-GNMZ-BS", "MATH-MTEZ-BS",
                    "MATH-CPMZ-BS"},
            "Mathematics",
            MathPlanConstants.PGMS + "mathematics/");
    final MajorMathRequirement rMATH = new MajorMathRequirement("MATH-BS")
            .setSemesterCourses("M 124!,M 126!,M 160.,M 192", "M 161.", "M 235,M 261,M 317,M 369,ODE");
                map.put(mMATH,rMATH);

    // Concentrations grouped into major:
    // 7061: MATH-ALSZ-BS, Mathematics - Actuarial Sciences
    // 7062: MATH-AMTZ-BS, Mathematics - Applied Mathematics
    // 7064: MATH-GNMZ-BS, Mathematics - General Mathematics
    // 7065: MATH-MTEZ-BS, Mathematics - Mathematics Education
    // 7066: MATH-CPMZ-BS, Mathematics - Computational Mathematics

    // *** Major in Natural Sciences

    final Major mNSCI = new Major(
            new int[]{7070, 7071, 7072, 7073, 7074, 7075},
            new String[]{"NSCI-BS", "NSCI-BLEZ-BS", "NSCI-CHEZ-BS", "NSCI-GLEZ-BS", "NSCI-PHSZ-BS",
                    "NSCI-PHEZ-BS"},
            "Natural Sciences",
            MathPlanConstants.PGMS + "natural-sciences/");
    final MajorMathRequirement rNSCI = new MajorMathRequirement("NSCI-BS")
            .setSemesterCourses("CALC1BIO.", "CALC2BIO.", null);
                map.put(mNSCI,rNSCI);

    // Concentrations grouped into major:
    // 7071: NSCI-BLEZ-BS, Natural Sciences - Biology Education
    // 7072: NSCI-CHEZ-BS, Natural Sciences - Chemistry Education
    // 7073: NSCI-GLEZ-BS, Natural Sciences - Geology Education
    // 7074: NSCI-PHSZ-BS, Natural Sciences - Physical Science
    // 7075: NSCI-PHEZ-BS, Natural Sciences - Physics Education

    // *** Major in Physics

    final Major mPHYS = new Major(
            new int[]{7080, 7081, 7082},
            new String[]{"PHYS-BS", "PHYS-APPZ-BS", "PHYS-PHYZ-BS",},
            "Physics",
            MathPlanConstants.PGMS + "physics/");
    final MajorMathRequirement rPHYS = new MajorMathRequirement("PHYS-BS")
            .setSemesterCourses("M 160.", "M 161.", "M 261,ODE");
                map.put(mPHYS,rPHYS);

    // Concentrations grouped into major:
    // 7081: PHYS-APPZ-BS, Physics - Applied Physics
    // 7082: PHYS-PHYZ-BS, Physics - Physics

    // *** Major in Psychology

    final Major mPSYC = new Major(
            new int[]{7090, 7091, 7092, 7093, 7094, 7095},
            new String[]{"PSYC-BS", "PSYC-ADCZ-BS", "PSYC-CCPZ-BS", "PSYC-GPSZ-BS", "PSYC-IOPZ-BS",
                    "PSYC-MBBZ-BS", "PSYC-GDSZ-BS"},
            "Psychology",
            MathPlanConstants.PGMS + "psychology/");
    final MajorMathRequirement rPSYC = new MajorMathRequirement("PSYC-BS")
            .setSemesterCourses("M 117!", "M 118!,M 124!", null);
                map.put(mPSYC,rPSYC);

    // Concentrations grouped into major:
    // 7091: PSYC-ADCZ-BS, Psychology - Addictions Counseling
    // 7092: PSYC-CCPZ-BS, Psychology - Clinical/Counseling Psychology
    // 7093: PSYC-GPSZ-BS, Psychology - General Psychology
    // 7094: PSYC-IOPZ-BS, Psychology - Industrial/Organizational
    // 7095: PSYC-MBBZ-BS, Psychology - Mind, Brain, and Behavior
    // FAKE: PSYC-GDSZ-BS, Psychology - ???

    // *** Major in Statistics

    final Major mSTAT = new Major(
            new int[]{7100, 7101, 7102},
            new String[]{"STAT-BS", "STAT-GSTZ-BS", "STAT-MSTZ-BS"},
            "Statistics",
            MathPlanConstants.PGMS + "statistics/");
    final MajorMathRequirement rSTAT = new MajorMathRequirement("STAT-BS")
            .setSemesterCourses("M 160.", "M 161.", "M 261,LINALG369");
                map.put(mSTAT,rSTAT);

    // Concentrations grouped into major:
    // 7101: STAT-GSTZ-BS, Statistics/General Statistics (DEACTIVATED)
    // 7102: STAT-MSTZ-BS, Statistics/Mathematical Statistics (DEACTIVATED)

    // *** Major in Zoology

    final Major mZOOL = new Major(
            new int[]{7110},
            new String[]{"ZOOL-BS"},
            "Zoology",
            MathPlanConstants.PGMS + "zoology/");
    final MajorMathRequirement rZOOL = new MajorMathRequirement("ZOOL-BS")
            .setSemesterCourses("M 117!,M 118!", MathPlanConstants.CALC1BIO, null);
                map.put(mZOOL,rZOOL);

    // ======================================================
    // College of Veterinary Medicine and Biomedical Sciences
    // ======================================================

    // *** Major in Biomedical Sciences

    final Major mBIOM = new Major(
            new int[]{8000, 8001, 8002, 8003, 8010, 8020},
            new String[]{"BIOM-BS", "BIOM-APHZ-BS", "BIOM-EPHZ-BS", "BIOM-MIDZ-BS", "EVHL-BS", "MICR-BS"},
            "Biomedical Sciences",
            MathPlanConstants.PGMS + "biomedical-sciences/");
    final MajorMathRequirement rBIOM = new MajorMathRequirement("BIOM-BS")
            .setSemesterCourses("M 124,M 125,M 126", MathPlanConstants.CALC1BIO, null);
                map.put(mBIOM,rBIOM);

    // Concentrations (and older major codes) grouped into major:
    // 8001: BIOM-APHZ-BS, Biomedical Sciences - Anatomy and Physiology
    // 8002: BIOM-EPHZ-BS, Biomedical Sciences - Environmental Public Health
    // 8003: BIOM-MIDZ-BS, Biomedical Sciences - Microbiology and Infectious Disease
    // 8010: EVHL-BS, Environmental Health (DEACTIVATED)
    // 8020: MICR-BS, Microbiology (DEACTIVATED)

    // *** Major in Neuroscience

    final Major mNERO = new Major(
            new int[]{8030, 8031, 8032},
            new String[]{"NERO-BS", "NERO-BCNZ-BS", "NERO-CMNZ-BS"},
            "Neuroscience",
            MathPlanConstants.PGMS + "neuroscience/");
    final MajorMathRequirement rNERO = new MajorMathRequirement("NERO-BS")
            .setSemesterCourses("M 124!,M 125!,M 126!", MathPlanConstants.M_155, null);
                map.put(mNERO,rNERO);

    // Concentrations grouped into major:
    // 8031: NERO-BCNZ-BS, Neuroscience - Behavioral and Cognitive Neuroscience
    // 8032: NERO-CMNZ-BS, Neuroscience - Cell and Molecular Neuroscience

    // *** Major in Exploratory Studies

    final Major mEXUN = new Major(
            new int[]{9000},
            new String[]{"EXUN"},
            "Exploratory Studies",
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXUN = new MajorMathRequirement("EXUN")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXUN,rEXUN);

    final Major mEXAD = new Major(
            new int[]{9001},
            new String[]{"EXAD", "EXLA", "EXCO", "USJC", "UNLA"},
            "Exploratory Studies", // Arts, Humanities, and Design
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXAD = new MajorMathRequirement("EXAD")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXAD,rEXAD);

    final Major mEXHF = new Major(
            new int[]{9002},
            new String[]{"EXHF"},
            "Exploratory Studies", // Health, Life, and Food Sciences
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXHF = new MajorMathRequirement("EXHF")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXHF,rEXHF);

    final Major mEXTC = new Major(
            new int[]{9003},
            new String[]{"EXTC"},
            "Exploratory Studies", // Education and Teaching
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXTC = new MajorMathRequirement("EXTC")
            .setSemesterCourses("AUCC3.", null, null);
                map.put(mEXTC,rEXTC);

    final Major mEXPL = new Major(
            new int[]{9004},
            new String[]{"EXPL"},
            "Exploratory Studies", // Land, Plant, and Animal Sciences
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXPL = new MajorMathRequirement("EXPL")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXPL,rEXPL);

    final Major mEXNR = new Major(
            new int[]{9005},
            new String[]{"EXNR"},
            "Exploratory Studies", // Environment and Natural Resources
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXNR = new MajorMathRequirement("EXNR")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXNR,rEXNR);

    final Major mEXPE = new Major(
            new int[]{9006},
            new String[]{"EXPE", "USEG", "USCS", "ENGO"},
            "Exploratory Studies", // Physical Sciences and Engineering
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXPE = new MajorMathRequirement("EXPE")
            .setSemesterCourses("M 160!", null, null);
                map.put(mEXPE,rEXPE);

    final Major mEXGS = new Major(
            new int[]{9007},
            new String[]{"EXGS", "EXPO"},
            "Exploratory Studies", // Global and Social Sciences
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXGS = new MajorMathRequirement("EXGS")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXGS,rEXGS);

    final Major mEXBU = new Major(
            new int[]{9008},
            new String[]{"EXBU", "USBU", "USBS"},
            "Exploratory Studies", // Organization, Management, and Enterprise
            MathPlanConstants.PGMS + "exploratory-studies/");
    final MajorMathRequirement rEXBU = new MajorMathRequirement("EXBU")
            .setSemesterCourses("M 117.,M 118.", null, null);
                map.put(mEXBU,rEXBU);

    // Codes that the Math Plan will ignore:
    // IGNORE: FESV-DD-BS, Fire and Emergency Services Administration
    // IGNORE: IAD0
    // IGNORE: EGOP
    // IGNORE: CSOR
    // IGNORE: N2IE-SI
    // IGNORE: GUES-CEUG
    // IGNORE: N2EG-ENGX-UG
    // IGNORE: GRAD-UG
    // IGNORE: SPCL-UG
    // IGNORE: CTED-UG
    // IGNORE: FCST-UG
    // IGNORE: SSAS-UG

                this.majors =new TreeMap<>(map);
}
