package dev.mathops.db.logic.mathplan.majors;

import dev.mathops.commons.log.Log;
import dev.mathops.db.logic.mathplan.MathPlanConstants;
import dev.mathops.db.logic.mathplan.types.EMajorTrack;
import dev.mathops.db.logic.mathplan.types.ERequirement;
import dev.mathops.db.logic.mathplan.types.IdealFirstTerm;

import java.util.List;

/**
 * An immutable record of all current majors from the latest catalog.
 *
 * <p>
 * NOTE: as majors are added, if any new "ideal first term" sets are defined, check the logic in "RecommendedFirstTerm"
 * to ensure combinations are being treated correctly.
 */
public final class MajorsCurrent extends MajorsBase {

    /** The single instance. */
    public static final MajorsCurrent INSTANCE = new MajorsCurrent();

    /**
     * Constructs a new {@code MajorsCurrent}.
     */
    private MajorsCurrent() {

        super(100);

        final List<Major> majors = innerGetMajors();

        // *** Last reviewed July 16, 2025, from the 2025-2026 Catalog. ***

        // ================================
        // College of Agricultural Sciences
        // ================================

        // *** Major in Agricultural Business
        // 1001: AGBU-AECZ-BS, Agricultural Business - Agricultural Economics
        // 1002: AGBU-FRCZ-BS, Agricultural Business - Farm and Ranch Management
        // 1003: AGBU-FSSZ-BS, Agricultural Business - Food Systems

        final Major mAGBU1 = new Major(
                new int[]{1000, 1001},
                new String[]{"AGBU-BS", "AGBU-AECZ-BS"},
                new EMajorTrack[]{EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Agricultural Business (General or Agricultural Economics)",
                MathPlanConstants.PGMS + "agricultural-business/",
                ERequirement.M_117_118_124_141,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mAGBU1);

        final Major mAGBU2 = new Major(
                new int[]{1002, 1003},
                new String[]{"AGBU-FRCZ-BS", "AGBU-FSSZ-BS"},
                new EMajorTrack[]{EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Agricultural Business (Farm &amp; Ranch Mgt., Food Systems)",
                MathPlanConstants.PGMS + "agricultural-business/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mAGBU2);

        // *** Major in Agricultural Education
        // 1011: AGED-AGLZ-BS, Agricultural Education - Agricultural Literacy
        // 1012: AGED-TDLZ-BS, Agricultural Education - Teacher Development

        final Major mAGED = new Major(
                new int[]{1010, 1011, 1012},
                new String[]{"AGED-BS", "AGED-AGLZ-BS", "AGED-TDLZ-BS"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Agricultural Education",
                MathPlanConstants.PGMS + "agricultural-education/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mAGED);

        // *** Major in Animal Science

        final Major mANIM = new Major(
                new int[]{1020},
                new String[]{"ANIM-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Animal Science",
                MathPlanConstants.PGMS + "animal-science/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mANIM);

        // *** Major in Environmental and Natural Resource Economics

        final Major mENRE = new Major(
                new int[]{1030},
                new String[]{"ENRE-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Environmental and Natural Resource Economics",
                MathPlanConstants.PGMS + "environmental-and-natural-resource-economics/",
                ERequirement.M_117_118_124_141,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mENRE);

        // *** Major in Environmental Horticulture
        // 1041: DEACTIVATED
        // 1042: ENHR-LDAZ-BS, Environmental Horticulture - Landscape Design and Contracting
        // 1043: ENHR-NALZ-BS, Environmental Horticulture - Nursery and Landscape Management
        // 1044: ENHR-TURZ-BS, Environmental Horticulture - Turf Management

        final Major mENHR1 = new Major(
                new int[]{1040, 1043, 1044},
                new String[]{"ENHR-BS", "ENHR-NALZ-BS", "ENHR-TURZ-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Environmental Horticulture (General)",
                MathPlanConstants.PGMS + "environmental-horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mENHR1);

        final Major mENH2 = new Major(
                new int[]{1042},
                new String[]{"ENHR-LDAZ-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Environmental Horticulture (Landscape Design &amp; Contracting)",
                MathPlanConstants.PGMS + "environmental-horticulture/",
                ERequirement.M_117_118_125,
                IdealFirstTerm.IDEAL_171825);
        majors.add(mENH2);

        // *** Major in Equine Science

        final Major mEQSC = new Major(
                new int[]{1050},
                new String[]{"EQSC-BS"},
                new EMajorTrack[]{EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Equine Science",
                MathPlanConstants.PGMS + "equine-science/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEQSC);

        // *** Major in Horticulture
        // 1061: DEACTIVATED
        // 1062: HORT-HBMZ-BS, Horticulture - Horticultural Business Management
        // 1063: HORT-HFCZ-BS, Horticulture - Horticultural Food Crops
        // 1064: HORT-HOSZ-BS, Horticulture - Horticultural Science
        // 1065: DEACTIVATED
        // 1066: HORT-CEHZ-BS, Horticulture - Controlled Environmental Horticulture

        final Major mHORT1 = new Major(
                new int[]{1060, 1062, 1063, 1066},
                new String[]{"HORT-BS", "HORT-HBMZ-BS", "HORT-HFCZ-BS", "HORT-CEHZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Horticulture (General)",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHORT1);

        final Major mHORT2 = new Major(
                new int[]{1064},
                new String[]{"HORT-HOSZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Horticulture (Horticultural Science)",
                MathPlanConstants.PGMS + "horticulture/",
                ERequirement.M_124_125_126_155,
                IdealFirstTerm.IDEAL_242526);
        majors.add(mHORT2);

        // *** Major in Landscape Architecture

        final Major mLDAR = new Major(
                new int[]{1070},
                new String[]{"LDAR-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Landscape Architecture",
                MathPlanConstants.PGMS + "landscape-architecture/",
                ERequirement.M_126,
                IdealFirstTerm.IDEAL_17182526);
        majors.add(mLDAR);

        // *** Major in Livestock Business Management

        final Major mLSBM = new Major(
                new int[]{1075, 1076, 1077},
                new String[]{"LSBM-BS", "LSBM-ASCZ-BS", "LSBM-LMTZ-BS"},
                new EMajorTrack[]{EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Livestock Business Management",
                MathPlanConstants.PGMS + "livestock-business-management/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mLSBM);

        // *** Major in Soil and Crop Sciences
        // 1081: DEACTIVATED
        // 1082: DEACTIVATED
        // 1083: DEACTIVATED
        // 1084: DEACTIVATED
        // 1085: DEACTIVATED
        // 1086: DEACTIVATED
        // 1087: SOCR-PBTZ-BS, Soil and Crop Sciences - Plant Biotechnology
        // 1088: SOCR-SESZ-BS, Soil and Crop Sciences - Soil Science and Environmental Solutions
        // 1089: SOCR-SAMZ-BS, Soil and Crop Sciences - Sustainable Agricultural Management

        final Major mSOCR = new Major(
                new int[]{1080, 1087, 1088, 1089},
                new String[]{"SOCR-BS", "SOCR-PBTZ-BS", "SOCR-SESZ-BS", "SOCR-SAMZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Soil and Crop Sciences",
                MathPlanConstants.PGMS + "soil-and-crop-sciences/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mSOCR);

        // *** Major in Agricultural Biology
        // 1091: AGBI-ENTZ-BS, Agricultural Biology - Entomology
        // 1092: AGBI-PLPZ-BS, Agricultural Biology - Plant Pathology
        // 1093: AGBI-WEEZ-BS, Agricultural Biology - Weed Science

        final Major mAGBI = new Major(
                new int[]{1090, 1091, 1092, 1093},
                new String[]{"AGBI-BS", "AGBI-ENTZ-BS", "AGBI-PLPZ-BS", "AGBI-WEEZ-BS"},
                new EMajorTrack[]{EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Agricultural Biology",
                MathPlanConstants.PGMS + "agricultural-biology/",
                ERequirement.PICK_3CR_117_118_124_120_155,
                IdealFirstTerm.IDEAL_55);
        majors.add(mAGBI);

        // ===================
        // College of Business
        // ===================

        // *** Major in Business Administration
        // 2001: BUSA-ACCZ-BS, Business Administration - Accounting
        // 2002: BUSA-FINZ-BS, Business Administration - Finance
        // 2003: BUSA-FPLZ-BS, Business Administration - Financial Planning
        // 2004: BUSA-HRMZ-BS, Business Administration - Human Resource Management
        // 2005: BUSA-INSZ-BS, Business Administration - Information Systems
        // 2006: BUSA-MKTZ-BS, Business Administration - Marketing
        // 2007: DEACTIVATED
        // 2008: BUSA-REAZ-BS, Business Administration - Real Estate
        // 2009: BUSA-SCMZ-BS, Business Administration - Supply Chain Management
        // 2010: BUSA-MINZ-BS, Business Administration - Management and Innovation
        // 2011: BUSA-SUSZ-BS, Business Administration - Sustainable Business

        final Major mBUSA = new Major(
                new int[]{2000, 2001, 2002, 2003, 2004, 2005, 2006, 2008, 2009, 2010, 2011},
                new String[]{"BUSA-BS", "BUSA-ACCZ-BS", "BUSA-FINZ-BS", "BUSA-FPLZ-BS", "BUSA-HRMZ-BS", "BUSA-INSZ-BS",
                        "BUSA-MKTZ-BS", "BUSA-REAZ-BS", "BUSA-SCMZ-BS", "BUSA-MINZ-BS", "BUSA-SUSZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Business Administration",
                MathPlanConstants.PGMS + "business-administration/",
                ERequirement.PICK_3CR_117_118_124_120_125_126_141_155_156_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_41);
        majors.add(mBUSA);

        // ======================
        // College of Engineering
        // ======================

        // *** Dual-Degree programs in Biomedical Engineering
        // 3001: CBEG-BMEC-BS, Biomedical Engineering, Dual Degree - With Chemical and Biological Engineering
        // 3002: ELEG-BMEE-BS, Biomedical Engineering, Dual Degree - With Electrical Engineering (Electrical Engin.)
        // 3003: ELEG-BMEL-BS, Biomedical Engineering, Dual Degree - With Electrical Engineering (Lasers and Optical)
        // 3004: MECH-BMEM-BS, Biomedical Engineering, Dual Degree - With Mechanical Engineering
        // 3005: CPEG-BMEP-BS, Biomedical Engineering, Dual Degree - With Computer Engineering

        final Major mCBEGDUAL = new Major(
                new int[]{3000, 3001, 3002, 3003, 3004, 3005},
                new String[]{"CBEG-DUAL", "CBEG-BMEC-BS", "ELEG-BMEE-BS", "ELEG-BMEL-BS", "MECH-BMEM-BS",
                        "CPEG-BMEP-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Biomedical Engineering, Dual Degree",
                MathPlanConstants.PGMS + "biomedical-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCBEGDUAL);

        // *** Major in Chemical and Biological Engineering
        // 3011: Chemical and Biological Engineering – Advanced Materials Concentration (CBEG-AVMZ-BS)
        // 3012: Chemical and Biological Engineering – Biomanufacturing Concentration (CBEG-BIMZ-BS)
        // 3013: Chemical and Biological Engineering – Molecular Medicine Concentration (CBEG-MLMZ-BS)
        // 3014: Chemical and Biological Engineering – Sustainable Engineering Concentration (CBEG-SSEZ-BS)

        final Major mCBEG = new Major(
                new int[]{3010, 3011, 3012, 3013, 3014},
                new String[]{"CBEG-BS", "CBEG-AVMZ-BS", "CBEG-BIMZ-BS", "CBEG-MLMZ-BS", "CBEG-SSEZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Chemical and Biological Engineering",
                MathPlanConstants.PGMS + "chemical-biological-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCBEG);

        // *** Major in Civil Engineering

        final Major mCIVE = new Major(
                new int[]{3020},
                new String[]{"CIVE-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Civil Engineering",
                MathPlanConstants.PGMS + "civil-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCIVE);

        // *** Major in Computer Engineering
        // 3031: CPEG-AESZ-BS, Computer Engineering - Aerospace Systems
        // 3032: CPEG-EISZ-BS, Computer Engineering - Embedded and IoT Systems
        // 3033: CPEG-NDTZ-BS, Computer Engineering - Networks and Data
        // 3034: DEACRIVATED

        final Major mCPEG = new Major(
                new int[]{3030, 3031, 3032, 3033},
                new String[]{"CPEG-BS", "CPEG-AESZ-BS", "CPEG-EISZ-BS", "CPEG-NDTZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Computer Engineering",
                MathPlanConstants.PGMS + "computer-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCPEG);

        // *** Major in Electrical Engineering
        // 3041: ELEG-ELEZ-BS, Electrical Engineering - Electrical Engineering
        // 3042: ELEG-LOEZ-BS, Electrical Engineering - Lasers and Optical Engineering
        // 3043: ELEG-ASPZ-BS, Electrical Engineering - Aerospace

        final Major mELEG = new Major(
                new int[]{3040, 3041, 3042, 3043},
                new String[]{"ELEG-BS", "ELEG-ELEZ-BS", "ELEG-LOEZ-BS", "ELEG-ASPZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Electrical Engineering",
                MathPlanConstants.PGMS + "electrical-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mELEG);

        // *** Major in Environmental Engineering

        final Major mENVE = new Major(
                new int[]{3070},
                new String[]{"ENVE-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Environmental Engineering",
                MathPlanConstants.PGMS + "environmental-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mENVE);

        // *** Major in Mechanical Engineering
        // 3081: MECH-ACEZ-BS, Mechanical Engineering - Aerospace Engineering
        // 3082: MECH-ADMZ-BS, Mechanical Engineering - Advanced Manufacturing

        final Major mMECH = new Major(
                new int[]{3080, 3081, 3082},
                new String[]{"MECH-BS", "MECH-ACEZ-BS", "MECH-ADMZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Mechanical Engineering",
                MathPlanConstants.PGMS + "mechanical-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mMECH);

        // *** Major in Construction Engineering

        final Major mCONE = new Major(
                new int[]{3090},
                new String[]{"CONE-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Construction Engineering",
                MathPlanConstants.PGMS + "construction-engineering/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mCONE);

        // ====================================
        // College of Health and Human Sciences
        // ====================================

        // *** Major in Apparel and Merchandising (with three concentrations)
        // 4001: APAM-ADAZ-BS, Apparel and Merchandising - Apparel Design and Production
        // 4002: APAM-MDSZ-BS, Apparel and Merchandising - Merchandising
        // 4003: APAM-PDVZ-BS, Apparel and Merchandising - Product Development

        final Major mAPAM = new Major(
                new int[]{4000, 4001, 4002, 4003},
                new String[]{"APAM-BS", "APAM-ADAZ-BS", "APAM-MDSZ-BS", "APAM-PDVZ-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Apparel and Merchandising",
                MathPlanConstants.PGMS + "apparel-and-merchandising/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mAPAM);

        // *** Major in Construction Management
        // FAKE: CTM0, "Pre-Construction Management

        final Major mCTMG = new Major(
                new int[]{4010},
                new String[]{"CTMG-BS", "CTM0"},
                new EMajorTrack[]{EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Construction Management",
                MathPlanConstants.PGMS + "construction-management/",
                ERequirement.M_117_118_125,
                IdealFirstTerm.IDEAL_171825);
        majors.add(mCTMG);

        // *** Major in Early Childhood Education

        final Major mECHE = new Major(
                new int[]{4020},
                new String[]{"ECHE-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.EDUCATION_TEACHING},
                "Early Childhood Education",
                MathPlanConstants.PGMS + "early-childhood-education/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mECHE);

        // *** Major in Family and Consumer Sciences
        // 4030: DEACTIVATED
        // 4031: DEACTIVATED
        // 4032: DEACTIVATED
        // 4033: DEACTIVATED
        // 4034: FCSE-BS, Family and Consumer Sciences Education

        final Major mFACS = new Major(
                new int[]{4034},
                new String[]{"FCSE-BS"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Family and Consumer Sciences Education",
                MathPlanConstants.PGMS + "family-consumer-sciences/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mFACS);

        // *** Major in Fermentation Science and Technology

        final Major mFMST1 = new Major(
                new int[]{4041, 4043},
                new String[]{"FAFS-BS", "FAFS-FSTZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Fermentation and Food Science (General)",
                MathPlanConstants.PGMS + "fermentation-and-food-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mFMST1);

        final Major mFMST2 = new Major(
                new int[]{4042},
                new String[]{"FAFS-FSIZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Fermentation Science and Technology (Food Science)",
                MathPlanConstants.PGMS + "fermentation-and-food-science/",
                ERequirement.M_117_118_124_125_141_OR_155,
                IdealFirstTerm.IDEAL_17182425);
        majors.add(mFMST2);

        // *** Major in Health and Exercise Science
        // 4051: HAES-HPRZ-BS, Health and Exercise Science - Health Promotion
        // 4052: DEACTIVATED
        // 4053: HAES-EXSZ-BS, Health and Exercise Science - Exercise Science

        final Major mHAES = new Major(
                new int[]{4050, 4051, 4053},
                new String[]{"HAES-BS", "HAES-HPRZ-BS", "HAES-EXSZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Health and Exercise Science",
                MathPlanConstants.PGMS + "health-and-exercise-science/",
                ERequirement.M_118_124_125,
                IdealFirstTerm.IDEAL_182425);
        majors.add(mHAES);

        // *** Major in Hospitality and Event Management

        final Major mHEMG = new Major(
                new int[]{4061},
                new String[]{"HEMG-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Hospitality and Event Management",
                MathPlanConstants.PGMS + "hospitality-event-management/",
                ERequirement.M_117_101,
                IdealFirstTerm.IDEAL_1701);
        majors.add(mHEMG);

        // *** Major in Human Development and Family Studies
        // 4071: HDFS-ECPZ-BS, Human Development and Family Studies - Early Childhood Profession
        // 4072: HDFS-HDEZ-BS, Human Development and Family Studies - Human Development and Family Studies
        // 4073: DEACTIVATED
        // 4074: HDFS-PHPZ-BS, Human Development and Family Studies - Pre-Health Professions
        // 4075: DEACTIVATED
        // 4076: HDFS-LADZ-BS, Human Development and Family Studies - Leadership and Advocacy
        // 4077: HDFS-BMHZ-BS, Human Development and Family Studies - Behavior and Mental Health Programs

        final Major mHDFS = new Major(
                new int[]{4070, 4071, 4072, 4074, 4076, 4077},
                new String[]{"HDFS-BS", "HDFS-ECPZ-BS", "HDFS-HDEZ-BS", "HDFS-PHPZ-BS", "HDFS-LADZ-BS", "HDFS-BMHZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Human Development &amp; Family Studies",
                MathPlanConstants.PGMS + "human-development-and-family-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHDFS);

        // *** Major in Interior Architecture and Design
        // 4081: IARD-BS, Interior Architecture and Design
        // 4082: IARD-IADZ-BS, Interior Architecture and Design - Interior Architecture
        // 4083: IARD-IPRZ-BS, Interior Architecture and Design - Interior Products and Retailing

        final Major mIARD = new Major(
                new int[]{4081, 4082, 4083},
                new String[]{"IARD-BS", "IARD-IADZ-BS", "IARD-IPRZ-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN},
                "Interior Architecture and Design",
                MathPlanConstants.PGMS + "interior-architecture-and-design/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mIARD);

        // *** Major in Social Work
        // 4101: SOWK-ADSZ-BSW, Social Work - Addictions Counseling

        final Major mSOWK = new Major(
                new int[]{4100, 4101, 4102, 4103},
                new String[]{"SOWK-BSW", "SOWK-ADSZ-BW", "SOWK-HOCZ-BW", "SOWK-ISWC-BW"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Social Work",
                MathPlanConstants.PGMS + "social-work/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mSOWK);

        // *** Major in Nutrition Science
        // 4110: NUTR-BS, Nutrition Science
        // 4111: NUTR-DINZ-BS, Nutrition Science - Dietetics and Nutrition Management Concentration
        // 4112: NUTR-PHLZ-BS, Nutrition Science - Pre-Health Nutrition Concentration
        // 4113: NUTR-SNWZ-BS, Nutrition Science - Sports Nutrition and Wellness Concentration

        final Major mNUTR1 = new Major(
                new int[]{4110, 4111, 4113},
                new String[]{"NUTR-BS", "NUTR-DINZ-BS", "NUTR-SNWZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Nutrition Science (General)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNUTR1);

        final Major mNUTR2 = new Major(
                new int[]{4112},
                new String[]{"NUTR-PHLZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Nutrition Science (Pre-Health Nutrition)",
                MathPlanConstants.PGMS + "nutrition-science/",
                ERequirement.M_117_118_124_125,
                IdealFirstTerm.IDEAL_17182425);
        majors.add(mNUTR2);

        // =======================
        // College of Liberal Arts
        // =======================

        // *** Major in Anthropology
        // 5001: ANTH-ARCZ-BA, Anthropology - Archaeology
        // 5002: ANTH-BIOZ-BA, Anthropology - Biological Anthropology
        // 5003: ANTH-CLTZ-BA, Anthropology - Cultural Anthropology

        final Major mANTH = new Major(
                new int[]{5000, 5001, 5002, 5003},
                new String[]{"ANTH-BA", "ANTH-ARCZ-BA", "ANTH-BIOZ-BA", "ANTH-CLTZ-BA",},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Anthropology",
                MathPlanConstants.PGMS + "anthropology/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mANTH);

        // *** Major in Art (B.A. and B.F.A combined)
        // 5011: DEACTIVATED
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

        final Major mARTI = new Major(
                new int[]{5010, 5012, 5013, 5020, 5021, 5022, 5023, 5024, 5025, 5027, 5026, 5028, 5029, 5030, 5031},
                new String[]{"ARTI-BA", "ARTI-ARTZ-BA", "ARTI-IVSZ-BA", "ARTM-BFA", "ARTM-DRAZ-BF", "ARTM-ELAZ-BF",
                        "ARTM-FIBZ-BF", "ARTM-GRDZ-BF", "ARTM-METZ-BF", "ARTM-PNTZ-BF", "ARTM-PHIZ-BF", "ARTM-POTZ-BF",
                        "ARTM-PRTZ-BF", "ARTM-SCLZ-BF", "ARTM-AREZ-BF"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING},
                "Art",
                MathPlanConstants.PGMS + "art-bfa/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mARTI);

        // *** Major in Communication Studies
        // 5041: DEACTIVATED

        final Major mCMST = new Major(
                new int[]{5040},
                new String[]{"CMST-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Communication Studies",
                MathPlanConstants.PGMS + "communication-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mCMST);

        // *** Major in Dance (BA and BFA)

        final Major mDNCE = new Major(
                new int[]{5050, 5055},
                new String[]{"DNCE-BA", "DANC-BFA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING},
                "Dance",
                MathPlanConstants.PGMS + "dance-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mDNCE);

        // *** Major in Economics

        final Major mECON = new Major(
                new int[]{5060},
                new String[]{"ECON-BA"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Economics",
                MathPlanConstants.PGMS + "economics/",
                ERequirement.M_141_OR_155_OR_160,
                IdealFirstTerm.IDEAL_1718);
        majors.add(mECON);

        // *** Major in English
        // 5071: ENGL-CRWZ-BA, English - Creative Writing
        // 5072: ENGL-ENEZ-BA, English - English Education
        // 5073: DEACTIVATED
        // 5074: ENGL-LITZ-BA, English - Literature
        // 5075: ENGL-WRLZ-BA, English - Writing, Rhetoric and Literacy
        // 5076: ENGL-LINZ-BA, English - Linguistics
        // 5077: ENGL-IESZ-BA, English - Integrated English Studies

        final Major mENGL = new Major(
                new int[]{5070, 5071, 5072, 5074, 5075, 5076},
                new String[]{"ENGL-BA", "ENGL-CRWZ-BA", "ENGL-ENEZ-BA", "ENGL-LITZ-BA", "ENGL-WRLZ-BA", "ENGL-LINZ-BA",
                        "ENGL-IESZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING,
                        EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "English",
                MathPlanConstants.PGMS + "english/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mENGL);

        // *** Major in Ethnic Studies
        // 5081: ETST-SOTZ-BA, Ethnic Studies - Social Studies Teaching
        // 5082: ETST-COIZ-BA, Ethnic Studies - Community Organizing and Institutional Change
        // 5083: ETST-RPRZ-BA, Ethnic Studies - Global Race, Power, and Resistance

        final Major mETST = new Major(
                new int[]{5080, 5081, 5082, 5083},
                new String[]{"ETST-BA", "ETST-SOTZ-BA", "ETST-COIZ-BA", "ETST-RPRZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.EDUCATION_TEACHING},
                "Ethnic Studies",
                MathPlanConstants.PGMS + "ethnic-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mETST);

        // *** Major in Geography

        final Major mGEOG = new Major(
                new int[]{5085},
                new String[]{"GEOG-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Geography",
                MathPlanConstants.PGMS + "geography/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mGEOG);

        // *** Major in History
        // 5091: HIST-GENZ-BA, History - General History
        // 5092: HIST-LNGZ-BA, History - Language
        // 5093: HIST-SBSZ-BA, History - Social and Behavioral Sciences
        // 5094: HIST-SSTZ-BA, History - Social Studies Teaching
        // 5095: HIST-DPUZ-BA, History - Digital and Public History

        final Major mHIST = new Major(
                new int[]{5090, 5091, 5092, 5093, 5094, 5095},
                new String[]{"HIST-BA", "HIST-GENZ-BA", "HIST-LNGZ-BA", "HIST-SBSZ-BA", "HIST-SSTZ-BA", "HIST-DPUZ-BA"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING, EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "History",
                MathPlanConstants.PGMS + "history/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mHIST);

        // *** Major in Journalism and Media Communication

        final Major mJAMC = new Major(
                new int[]{5100},
                new String[]{"JAMC-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Journalism and Media Communication",
                MathPlanConstants.PGMS + "journalism-and-media-communication/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mJAMC);

        // *** Major in Languages, Literatures and Cultures
        // 5111: LLAC-LFRZ-BA, Languages, Literatures and Cultures - French
        // 5112: LLAC-LGEZ-BA, Languages, Literatures and Cultures - German
        // 5113: LLAC-LSPZ-BA, Languages, Literatures and Cultures - Spanish
        // 5114: LLAC-SPPZ-BA, Languages, Literatures and Cultures - Spanish for the Professions

        final Major mLLAC = new Major(
                new int[]{5110, 5111, 5112, 5113, 5114},
                new String[]{"LLAC-BA", "LLAC-LFRZ-BA", "LLAC-LGEZ-BA", "LLAC-LSPZ-BA", "LLAC-SPPZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING,
                        EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Languages, Literatures and Cultures",
                MathPlanConstants.PGMS + "languages-literatures-and-cultures/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mLLAC);

        // *** Major in Music, B.A.
        // 5130: MUSC-BM, Music (B.M.)
        // 5131: MUSC-COMZ-BM, Music (B.M.) - Composition
        // 5132: MUSC-MUEZ-BM, Music (B.M.) - Music Education
        // 5133: MUSC-MUTZ-BM, Music (B.M.) - Music Therapy
        // 5134: MUSC-PERZ-BM, Music (B.M.) - Performance

        final Major mMUSI = new Major(
                new int[]{5120, 5130, 5131, 5132, 5133, 5134},
                new String[]{"MUSI-BA", "MUSC-BM", "MUSC-COMZ-BM", "MUSC-MUEZ-BM", "MUSC-MUTZ-BM", "MUSC-PERZ-BM"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING},
                "Music",
                MathPlanConstants.PGMS + "music-ba/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mMUSI);

        // *** Major in Philosophy
        // 5141: PHIL-GNPZ-BA, Philosophy - General Philosophy
        // 5142: PHIL-GPRZ-BA, Philosophy - Global Philosophies and Religions
        // 5143: PHIL-PSAZ-BA, Philosophy - Philosophy, Science, and Technology

        final Major mPHIL = new Major(
                new int[]{5140, 5141, 5142, 5143},
                new String[]{"PHIL-BA", "PHIL-GNPZ-BA", "PHIL-GPRZ-BA", "PHIL-PSAZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN},
                "Philosophy",
                MathPlanConstants.PGMS + "philosophy/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mPHIL);

        // *** Major in Political Science
        // 5151: POLS-EPAZ-BA, Political Science - Environmental Politics and Policy
        // 5152: POLS-GPPZ-BA, Political Science - Global Politics and Policy
        // 5153: DEACTIVATED
        // 5154: POLS-LPGZ-BA, Political Science - Law, Politics, and Government
        // 5155: POLS-PJDZ-BA, Political Science - Power, Justice, and Democracy
        // 5156: POLS-PPSZ-BA, Political Science - Public Policy and Service

        final Major mPOLS = new Major(
                new int[]{5150, 5151, 5152, 5154, 5155, 5156},
                new String[]{"POLS-BA", "POLS-EPAZ-BA", "POLS-GPPZ-BA", "POLS-LPGZ-BA", "POLS-PJDZ-BA",
                        "POLS-PPSZ-BA"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Political Science",
                MathPlanConstants.PGMS + "political-science/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mPOLS);

        // *** Major in Sociology
        // 5161: SOCI-CRCZ-BA, Sociology - Criminology and Criminal Justice
        // 5162: SOCI-ENSZ-BA, Sociology - Environmental Sociology
        // 5163: SOCI-GNSZ-BA, Sociology - General Sociology

        final Major mSOCI = new Major(
                new int[]{5160, 5161, 5162, 5163},
                new String[]{"SOCI-BA", "SOCI-CRCZ-BA", "SOCI-ENSZ-BA", "SOCI-GNSZ-BA"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Sociology",
                MathPlanConstants.PGMS + "sociology/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mSOCI);

        // *** Major in Theatre
        // 5171: DEACTIVATED
        // 5172: DEACTIVATED
        // 5173: THTR-PRFZ-BA, Theatre - Performance
        // 5174: THTR-LDTZ-BA, Theatre - Lighting Design and Technology
        // 5175: THTR-MUSZ-BA, Theatre - Musical Theatre
        // 5176: THTR-PDTZ-BA, Theatre - Projection Design and Technology
        // 5177: THTR-SDSZ-BA, Theatre - Set Design
        // 5178: THTR-SDTZ-BA, Theatre - Sound Design and Technology
        // 5179: THTR-CDTZ-BA, Theatre - Costume Design and Technology

        final Major mTHTR = new Major(
                new int[]{5170, 5173, 5174, 5175, 5176, 5177, 5178, 5179},
                new String[]{"THTR-BA", "THTR-PRFZ-BA", "THTR-LDTZ-BA", "THTR-MUSZ-BA", "THTR-PDTZ-BA", "THTR-SDSZ-BA",
                        "THTR-SDTZ-BA", "THTR-CDTZ-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN},
                "Theatre",
                MathPlanConstants.PGMS + "theatre/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mTHTR);

        // *** Major in Women's and Gender Studies

        final Major mWGST = new Major(
                new int[]{5180},
                new String[]{"WGST-BA"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Women's and Gender Studies",
                MathPlanConstants.PGMS + "womens-and-gender-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mWGST);

        // *** Major in International Studies
        // 5191: INST-ASTZ-BA, International Studies - Asian Studies
        // 5192: INST-EUSZ-BA, International Studies - European Studies
        // 5193: INST-LTSZ-BA, International Studies - Latin American Studies
        // 5194: INST-MEAZ-BA, International Studies - Middle East and North African Studies
        // 5195: INST-GBLZ-BA, International Studies - Global Studies

        final Major mINST = new Major(
                new int[]{5190, 5191, 5192, 5193, 5194, 5195},
                new String[]{"INST-BA", "INST-ASTZ-BA", "INST-EUSZ-BA", "INST-LTSZ-BA", "INST-MEAZ-BA", "INST-GBLZ-BA"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "International Studies",
                MathPlanConstants.PGMS + "international-studies/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mINST);

        // *** Major in Interdisciplinary Liberal Arts

        final Major mILAR = new Major(
                new int[]{5200},
                new String[]{"ILAR-BA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Interdisciplinary Liberal Arts",
                MathPlanConstants.PGMS + "interdisciplinary-liberal-arts/",
                ERequirement.CORE_ONLY,
                IdealFirstTerm.CORE_ONLY);
        majors.add(mILAR);

        // ============================
        // College of Natural Resources
        // ============================

        // *** Major in Ecosystem Science and Sustainability

        final Major mECSS = new Major(
                new int[]{6000},
                new String[]{"ECSS-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Ecosystem Science and Sustainability",
                MathPlanConstants.PGMS + "ecosystem-science-sustainability/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mECSS);

        // *** Major in Fish, Wildlife and Conservation Biology
        // 6011: FWCB-CNVZ-BS, Fish, Wildlife and Conservation Biology - Conservation Biology
        // 6012: FWCB-FASZ-BS, Fish, Wildlife and Conservation Biology - Fisheries and Aquatic Sciences
        // 6013: FWCB-WDBZ-BS, Fish, Wildlife and Conservation Biology - Wildlife Biology

        final Major mFWCB = new Major(
                new int[]{6010, 6011, 6012, 6013},
                new String[]{"FWCB-BS", "FWCB-CNVZ-BS", "FWCB-FASZ-BS", "FWCB-WDBZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Fish, Wildlife and Conservation Biology",
                MathPlanConstants.PGMS + "fish-wildlife-and-conservation-biology/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mFWCB);

        // *** Major in Forest and Rangeland Stewardship
        // 6081: FRRS-FRBZ-BS, Forest and Rangeland Stewardship - Forest Biology
        // 6082: FRRS-FRFZ-BS, Forest and Rangeland Stewardship - Forest Fire Science
        // 6083: FRRS-FMGZ-BS, Forest and Rangeland Stewardship - Forest Management
        // 6084: FRRS-RFMZ-BS, Forest and Rangeland Stewardship - Rangeland and Forest Management
        // 6085: FRRS-RCMZ-BS, Forest and Rangeland Stewardship - Rangeland Conservation and Management

        final Major mFRRS1 = new Major(
                new int[]{6080, 6082, 6083, 6084},
                new String[]{"FRRS-BS", "FRRS-FRFZ-BS", "FRRS-FMGZ-BS", "FRRS-RFMZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Forest &amp; Rangeland Stewardship (General)",
                MathPlanConstants.PGMS + "forest-and-rangeland-stewardship/",
                ERequirement.M_141,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mFRRS1);

        final Major mFRRS2 = new Major(
                new int[]{6085},
                new String[]{"FRRS-RCMZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Forest &amp; Rangeland Stewardship (Rangeland Conservation &amp; Management)",
                MathPlanConstants.PGMS + "forest-and-rangeland-stewardship/",
                ERequirement.PICK_3CR_117_118_125_141,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mFRRS2);

        final Major mFRRS3 = new Major(
                new int[]{6081},
                new String[]{"FRRS-FRBZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Forest &amp; Rangeland Stewardship (Forest Biology)",
                MathPlanConstants.PGMS + "forest-and-rangeland-stewardship/",
                ERequirement.M_155,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mFRRS3);

        // *** Major in Geology
        // 6021: GEOL-EVGZ-BS, Geology - Environmental Geology
        // 6022: GEOL-GEOZ-BS, Geology - Geology
        // 6023: GEOL-GPYZ-BS, Geology - Geophysics
        // 6024: GEOL-HYDZ-BS, Geology - Hydrogeology

        final Major mGEOL = new Major(
                new int[]{6020, 6021, 6022, 6023, 6024},
                new String[]{"GEOL-BS", "GEOL-EVGZ-BS", "GEOL-GEOZ-BS", "GEOL-GPYZ-BS", "GEOL-HYDZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Geology",
                MathPlanConstants.PGMS + "geology/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mGEOL);

        // *** Major in Human Dimensions of Natural Resources

        final Major mHDNR = new Major(
                new int[]{6030},
                new String[]{"HDNR-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Human Dimensions of Natural Resources",
                MathPlanConstants.PGMS + "human-dimensions-of-natural-resources/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mHDNR);

        // *** Major in Natural Resource Tourism
        // 6040: DEACTIVATED
        // 6041: DEACTIVATED
        // 6042: DEACTIVATED
        // 6043: NRTM-BS, Natural Resource Tourism
        // 6044: NRTM-GLTZ-BS, Natural Resource Tourism - Global Tourism
        // 6045: NRTM-NRTZ-BS, Natural Resource Tourism - Natural Resource Tourism

        final Major mNRTM = new Major(
                new int[]{6043, 6044, 6045},
                new String[]{"NRTM-BS", "NRTM-GLTZ-BS", "NRTM-NRTZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Natural Resource Tourism",
                MathPlanConstants.PGMS + "natural-resource-tourism/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mNRTM);

        // *** Major in Natural Resources Management

        final Major mNRMG = new Major(
                new int[]{6050},
                new String[]{"NRMG-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Natural Resources Management",
                MathPlanConstants.PGMS + "natural-resources-management/",
                ERequirement.M_117_118_125,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mNRMG);

        // *** Major in Restoration Ecology

        final Major mRECO = new Major(
                new int[]{6060},
                new String[]{"RECO-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Restoration Ecology",
                MathPlanConstants.PGMS + "restoration-ecology/",
                ERequirement.PICK_3CR_117_118_125_141,
                IdealFirstTerm.IDEAL_17_OR_HIGHER);
        majors.add(mRECO);

        // *** Major in Watershed Science and Sustainability
        // 6070: DEACTIVATED
        // 6071: WSSS-WSDZ-BS, Watershed Science and Sustainability - Watershed Data
        // 6072: WSSS-WSSZ-BS, Watershed Science and Sustainability - Watershed Science
        // 6073: WSSS-WSUZ-BS, Watershed Science and Sustainability - Watershed Sustainability
        // 6074: WSSS-BS, Watershed Science and Sustainability

        final Major mWRSC1 = new Major(
                new int[]{6071, 6072, 6074},
                new String[]{"WSSS-WSDZ-BS", "WSSS-WSSZ-BS", "WSSS-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Watershed Science and Sustainability",
                MathPlanConstants.PGMS + "watershed-science/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_18_OR_HIGHER);
        majors.add(mWRSC1);

        final Major mWRSC2 = new Major(
                new int[]{6073},
                new String[]{"WSSS-WSUZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Watershed Science and Sustainability (Watershed Sustainability)",
                MathPlanConstants.PGMS + "watershed-science/",
                ERequirement.M_141_OR_155_OR_160,
                IdealFirstTerm.IDEAL_18_OR_HIGHER);
        majors.add(mWRSC2);

        // ===========================
        // College of Natural Sciences
        // ===========================

        // *** Major in Biochemistry
        // 7011: Deactivated
        // 7012: BCHM-HMSZ-BS, Biochemistry - Health and Medical Sciences
        // 7013: BCHM-PPHZ-BS, Biochemistry - Pre-Pharmacy
        // 7014: BCHM-ASBZ-BS, Biochemistry - ASBMB
        // 7015: BCHM-DTSZ-BS, Biochemistry - Data Science
        // FAKE: BCHM-GBCZ-BS, Biochemistry - General Biochemistry

        final Major mBCHM = new Major(
                new int[]{7010, 7012, 7013, 7014, 7015},
                new String[]{"BCHM-BS", "BCHM-HMSZ-BS", "BCHM-PPHZ-BS", "BCHM-ASBZ-BS", "BCHM-DTSZ-BS", "BCHM-GBCZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES, EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Biochemistry",
                MathPlanConstants.PGMS + "biochemistry/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mBCHM);

        // *** Major in Biological Science
        // 7021: BLSC-BLSZ-BS, Biological Science - Biological Science
        // 7022: BLSC-BTNZ-BS, Biological Science - Botany

        final Major mBLSC = new Major(
                new int[]{7020, 7021, 7022},
                new String[]{"BLSC-BS", "BLSC-BLSZ-BS", "BLSC-BTNZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Biological Science",
                MathPlanConstants.PGMS + "biology/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_55);
        majors.add(mBLSC);

        // *** Major in Chemistry
        // 7031: DEACTIVATED
        // 7032: DEACTIVATED
        // 7033: CHEM-ECHZ-BS, Chemistry - Environmental Chemistry
        // 7034: CHEM-FCHZ-BS, Chemistry - Forensic Chemistry
        // 7035: CHEM-HSCZ-BS, Chemistry - Health Sciences
        // 7036: CHEM-SCHZ-BS, Chemistry - Sustainable Chemistry
        // 7037: CHEM-MTRZ-BS, Chemistry - Materials

        final Major mCHEM = new Major(
                new int[]{7030, 7033, 7034, 7035, 7036, 7037},
                new String[]{"CHEM-BS", "CHEM-ECHZ-BS", "CHEM-FCHZ-BS", "CHEM-HSCZ-BS", "CHEM-SCHZ-BS", "CHEM-MTRZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Chemistry",
                MathPlanConstants.PGMS + "chemistry/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mCHEM);

        // *** Major in Computer Science
        // 7041: CPSC-CPSZ-BS, Computer Science - Computer Science
        // 7042: CPSC-HCCZ-BS, Computer Science - Human-Centered Computing
        // 7043: CPSC-AIMZ-BS, Computer Science - Artificial Intelligence and Machine Learning
        // 7044: CPSC-CSYZ-BS, Computer Science - Computing Systems
        // 7045: CPSC-NSCZ-BS, Computer Science - Networks and Security
        // 7046: CPSC-SEGZ-BS, Computer Science - Software Engineering
        // 7047: CPSC-CSEZ-BS, Computer Science - Computer Science Education
        // 7048: CPSC-CFCZ-BS, Computer Science - Computing for Creatives

        final Major mCPSC = new Major(
                new int[]{7040, 7041, 7042, 7043, 7044, 7045, 7046, 7047},
                new String[]{"CPSC-BS", "CPSC-CPSZ-BS", "CPSC-HCCZ-BS", "CPSC-AIMZ-BS", "CPSC-CSYZ-BS", "CPSC-NSCZ-BS",
                        "CPSC-SEGZ-BS", "CPSC-CSEZ-BS", "CPSC-CFCZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING, EMajorTrack.EDUCATION_TEACHING},
                "Computer Science",
                MathPlanConstants.PGMS + "computer-science/",
                ERequirement.M_156_OR_160,
                IdealFirstTerm.IDEAL_56_OR_60);
        majors.add(mCPSC);

        // *** Major in Data Science
        // 7051: DSCI-CSCZ-BS, Data Science - Computer Science
        // 7052: DSCI-ECNZ-BS, Data Science - Economics
        // 7053: DSCI-MATZ-BS, Data Science - Mathematics
        // 7054: DSCI-STSZ-BS, Data Science - Statistics
        // 7055: DSCI-NEUZ-BS, Data Science - Neuroscience

        final Major mDSCI = new Major(
                new int[]{7050, 7051, 7052, 7053, 7054, 7055},
                new String[]{"DSCI-BS", "DSCI-CSCZ-BS", "DSCI-ECNZ-BS", "DSCI-MATZ-BS", "DSCI-STSZ-BS", "DSCI-NEUZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Data Science",
                MathPlanConstants.PGMS + "data-science/",
                ERequirement.M_156,
                IdealFirstTerm.IDEAL_56);
        majors.add(mDSCI);

        // *** Major in Mathematics
        // 7061: MATH-ALSZ-BS, Mathematics - Actuarial Sciences
        // 7062: MATH-AMTZ-BS, Mathematics - Applied Mathematics
        // 7064: MATH-GNMZ-BS, Mathematics - General Mathematics
        // 7065: MATH-MTEZ-BS, Mathematics - Mathematics Education
        // 7066: MATH-CPMZ-BS, Mathematics - Computational Mathematics

        final Major mMATH = new Major(
                new int[]{7060, 7061, 7062, 7063, 7064, 7065, 7066},
                new String[]{"MATH-BS", "MATH-ALSZ-BS", "MATH-AMTZ-BS", "MATH-GNMZ-BS", "MATH-MTEZ-BS", "MATH-CPMZ-BS"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN, EMajorTrack.EDUCATION_TEACHING,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING, EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Mathematics",
                MathPlanConstants.PGMS + "mathematics/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mMATH);

        // *** Major in Natural Sciences
        // 7071: NSCI-BLEZ-BS, Natural Sciences - Biology Education
        // 7072: NSCI-CHEZ-BS, Natural Sciences - Chemistry Education
        // 7073: NSCI-GLEZ-BS, Natural Sciences - Geology Education
        // 7074: NSCI-PHSZ-BS, Natural Sciences - Physical Science
        // 7075: NSCI-PHEZ-BS, Natural Sciences - Physics Education

        final Major mNSCI1 = new Major(
                new int[]{7070, 7071, 7072, 7073, 7074},
                new String[]{"NSCI-BS", "NSCI-BLEZ-BS", "NSCI-CHEZ-BS", "NSCI-GLEZ-BS", "NSCI-PHSZ-BS"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING, EMajorTrack.GLOBAL_SOCIAL_SCIENCES,
                        EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Natural Sciences",
                MathPlanConstants.PGMS + "natural-sciences/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mNSCI1);

        final Major mNSCI2 = new Major(
                new int[]{7075},
                new String[]{"NSCI-PHEZ-BS"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING},
                "Natural Sciences (Physics Education)",
                MathPlanConstants.PGMS + "natural-sciences/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mNSCI2);

        // *** Major in Physics
        // 7081: PHYS-APPZ-BS, Physics - Applied Physics
        // 7082: PHYS-PHYZ-BS, Physics - Physics

        final Major mPHYS = new Major(
                new int[]{7080, 7081, 7082},
                new String[]{"PHYS-BS", "PHYS-APPZ-BS", "PHYS-PHYZ-BS"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Physics",
                MathPlanConstants.PGMS + "physics/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mPHYS);

        // *** Major in Psychology
        // 7091: PSYC-ADCZ-BS, Psychology - Addictions Counseling
        // 7092: PSYC-CCPZ-BS, Psychology - Clinical/Counseling Psychology
        // 7093: PSYC-GPSZ-BS, Psychology - General Psychology
        // 7094: PSYC-IOPZ-BS, Psychology - Industrial/Organizational
        // 7095: PSYC-MBBZ-BS, Psychology - Mind, Brain, and Behavior
        // 7096: PSYC-AACZ-BS, Psychology - Accelerated Addictions Counseling

        final Major mPSYC1 = new Major(
                new int[]{7090, 7091, 7092, 7093, 7094, 7096},
                new String[]{"PSYC-BS", "PSYC-ADCZ-BS", "PSYC-CCPZ-BS", "PSYC-GPSZ-BS", "PSYC-IOPZ-BS", "PSYC-AACZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Psychology",
                MathPlanConstants.PGMS + "psychology/",
                ERequirement.M_117_118_124,
                IdealFirstTerm.IDEAL_17);
        majors.add(mPSYC1);

        final Major mPSYC2 = new Major(
                new int[]{7095},
                new String[]{"PSYC-MBBZ-BS"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Psychology (Mind, Brain, and Behavior)",
                MathPlanConstants.PGMS + "psychology/",
                ERequirement.PICK_3CR_117_118_124_120_125_126,
                IdealFirstTerm.IDEAL_17);
        majors.add(mPSYC2);

        // *** Major in Statistics
        // 7101: DEACTIVATED
        // 7102: DEACTIVATED

        final Major mSTAT = new Major(
                new int[]{7100},
                new String[]{"STAT-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Statistics",
                MathPlanConstants.PGMS + "statistics/",
                ERequirement.M_156_OR_160,
                IdealFirstTerm.IDEAL_56_OR_60);
        majors.add(mSTAT);

        // *** Major in Zoology

        final Major mZOOL = new Major(
                new int[]{7110},
                new String[]{"ZOOL-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Zoology",
                MathPlanConstants.PGMS + "zoology/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_PICK_17_TO_55);
        majors.add(mZOOL);

        // ======================================================
        // College of Veterinary Medicine and Biomedical Sciences
        // ======================================================

        // *** Major in Biomedical Sciences
        // 8001: BIOM-APHZ-BS, Biomedical Sciences - Anatomy and Physiology
        // 8002: BIOM-EPHZ-BS, Biomedical Sciences - Environmental Public Health
        // 8003: BIOM-MIDZ-BS, Biomedical Sciences - Microbiology and Infectious Disease
        // 8010: DEACTIVATED
        // 8020: DEACTIVATED

        final Major mBIOM1 = new Major(
                new int[]{8000, 8002, 8003},
                new String[]{"BIOM-BS", "BIOM-EPHZ-BS", "BIOM-MIDZ-BS"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES, EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Biomedical Sciences (General)",
                MathPlanConstants.PGMS + "biomedical-sciences/",
                ERequirement.PICK_3CR_118_124_125_126_155_160,
                IdealFirstTerm.IDEAL_PICK_18_TO_60);
        majors.add(mBIOM1);

        final Major mBIOM2 = new Major(
                new int[]{8001},
                new String[]{"BIOM-APHZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Biomedical Sciences (Anatomy &amp; Physiology)",
                MathPlanConstants.PGMS + "biomedical-sciences/",
                ERequirement.M_155_OR_160,
                IdealFirstTerm.IDEAL_55_OR_60);
        majors.add(mBIOM2);

        // *** Major in Neuroscience
        // 8031: NERO-BCNZ-BS, Neuroscience - Behavioral and Cognitive Neuroscience
        // 8032: NERO-CMNZ-BS, Neuroscience - Cell and Molecular Neuroscience

        final Major mNERO = new Major(
                new int[]{8030, 8031, 8032},
                new String[]{"NERO-BS", "NERO-BCNZ-BS", "NERO-CMNZ-BS"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Neuroscience",
                MathPlanConstants.PGMS + "neuroscience/",
                ERequirement.M_155,
                IdealFirstTerm.IDEAL_55);
        majors.add(mNERO);

        // *** Major in Health Physics (CSU Online only)

//        final Major mHLPH = new Major(
//                new int[]{8040},
//                new String[]{"HLPH-DD-BS"},
//                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES,
//                        EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
//                "Health Physics",
//                null,
//                ERequirement.M_160,
//                IdealFirstTerm.IDEAL_17_OR_HIGHER);
//        majors.add(mHLPH);

        // *** Major in Exploratory Studies

        final Major mEXUN = new Major(
                new int[]{9000},
                new String[]{"EXUN"},
                new EMajorTrack[]{},
                "Exploratory Studies",
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXUN);

        final Major mEXAD = new Major(
                new int[]{9001},
                new String[]{"EXAD", "EXLA", "EXCO", "USJC", "UNLA"},
                new EMajorTrack[]{EMajorTrack.ARTS_HUMANITIES_DESIGN},
                "Exploratory Studies", // Arts, Humanities, and Design
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXAD);

        final Major mEXHF = new Major(
                new int[]{9002},
                new String[]{"EXHF"},
                new EMajorTrack[]{EMajorTrack.HEALTH_LIFE_FOOD_SCIENCES},
                "Exploratory Studies", // Health, Life, and Food Sciences
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXHF);

        final Major mEXTC = new Major(
                new int[]{9003},
                new String[]{"EXTC"},
                new EMajorTrack[]{EMajorTrack.EDUCATION_TEACHING},
                "Exploratory Studies", // Education and Teaching
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXTC);

        final Major mEXPL = new Major(
                new int[]{9004},
                new String[]{"EXPL"},
                new EMajorTrack[]{EMajorTrack.LAND_PLANT_ANIMAL_SCIENCES},
                "Exploratory Studies", // Land, Plant, and Animal Sciences
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXPL);

        final Major mEXNR = new Major(
                new int[]{9005},
                new String[]{"EXNR"},
                new EMajorTrack[]{EMajorTrack.ENVIRONMENTAL_NATURAL_RESOURCES},
                "Exploratory Studies", // Environment and Natural Resources
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXNR);

        final Major mEXPE = new Major(
                new int[]{9006},
                new String[]{"EXPE", "USEG", "USCS", "ENGO", "EGOP"},
                new EMajorTrack[]{EMajorTrack.MATH_PHYSICAL_SCIENCES_ENGINEERING},
                "Exploratory Studies", // Physical Sciences and Engineering
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.M_160,
                IdealFirstTerm.IDEAL_60);
        majors.add(mEXPE);

        final Major mEXGS = new Major(
                new int[]{9007},
                new String[]{"EXGS", "EXPO"},
                new EMajorTrack[]{EMajorTrack.GLOBAL_SOCIAL_SCIENCES},
                "Exploratory Studies", // Global and Social Sciences
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXGS);

        final Major mEXBU = new Major(
                new int[]{9008},
                new String[]{"EXOM", "EXBU", "USBU", "USBS"},
                new EMajorTrack[]{EMajorTrack.ORGANIZATION_MANAGEMENT_ENTERPRISE},
                "Exploratory Studies", // Organization, Management, and Enterprise
                MathPlanConstants.PGMS + "exploratory-studies/",
                ERequirement.PICK_3CR_117_118_124_120,
                IdealFirstTerm.IDEAL_171824);
        majors.add(mEXBU);

        majors.sort(null);
    }
}
