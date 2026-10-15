package com.bloodmatcher.common;

public final class Constants {

    private Constants() {
    }

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Default cooldown period in days between whole blood donations (standard preliminary product filter: 56 days / 8 weeks).
     */
    public static final int DONATION_COOLDOWN_DAYS = 56;

    /**
     * Maximum units allowed per request.
     */
    public static final int MAX_REQUEST_UNITS = 20;

    /**
     * Health and Preliminary Screening Disclaimer:
     * Blood Matcher is a coordination and preliminary software screening tool based on ABO/Rh compatibility.
     * It does NOT provide medical clearance, clinical diagnostics, or transfusion safety guarantees.
     * Professional blood bank serological screening, cross-matching, antibody testing, and medical history
     * verification by authorized healthcare practitioners are strictly required prior to any blood donation or transfusion.
     */
    public static final String HEALTH_DISCLAIMER = "Blood Matcher red-blood-cell compatibility screening is a preliminary software coordination aid only. "
            + "It is NOT medical clearance and does not guarantee transfusion safety. Formal blood bank testing, antibody screening, "
            + "cross-matching, and clinical evaluation by licensed medical personnel remain mandatory before any transfusion.";
}
