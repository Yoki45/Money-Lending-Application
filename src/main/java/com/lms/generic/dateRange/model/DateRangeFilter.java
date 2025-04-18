package com.lms.generic.dateRange.model;

import java.util.Calendar;

public class DateRangeFilter {

    private final Calendar beginCalendar;
    private final Calendar endCalendar;
    private final Boolean overdue;

    public DateRangeFilter(Calendar beginCalendar, Calendar endCalendar, Boolean overdue) {
        this.beginCalendar = beginCalendar;
        this.endCalendar = endCalendar;
        this.overdue = overdue;
    }

    public Calendar getBeginCalendar() {
        return beginCalendar;
    }

    public Calendar getEndCalendar() {
        return endCalendar;
    }

    public Boolean getOverdue() {
        return overdue;
    }
}
