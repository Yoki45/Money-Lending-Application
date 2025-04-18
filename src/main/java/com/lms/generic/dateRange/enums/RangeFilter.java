package com.lms.generic.dateRange.enums;

public enum RangeFilter {

    CURRENT_WEEK("current_week"), THIS_MONTH("this_month"), THIS_YEAR("this_year"),
    THIS_WEEK("this_week"), ALL_RANGES("all_ranges"), LAST_WEEK("last_week"),
    OVERDUE("overdue"),  CURRENT_MONTH("current_month");

    private String name;

    RangeFilter(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public static RangeFilter findByName(String name) {
        for (RangeFilter filter : RangeFilter.values()) {
            if (filter.getName().equalsIgnoreCase(name)) {
                return filter;
            }
        }
        return RangeFilter.THIS_MONTH;
    }
}
