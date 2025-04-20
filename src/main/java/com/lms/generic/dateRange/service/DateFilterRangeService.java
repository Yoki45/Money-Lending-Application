package com.lms.generic.dateRange.service;

import com.lms.generic.dateRange.enums.RangeFilter;
import com.lms.generic.dateRange.model.DateRangeFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

import static com.lms.utils.Utils.stringNullOrEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class DateFilterRangeService {


    public DateRangeFilter getFilterDateRange(String dateRange) {

        if (stringNullOrEmpty(dateRange)) {
            return new DateRangeFilter(null, null, null);
        }

        RangeFilter rangeFilter = RangeFilter.findByName(dateRange);

        Calendar beginCalendar = null;
        Calendar endCalendar = null;
        Boolean overdue = null;

        switch (rangeFilter) {
            case OVERDUE:
                overdue = true;
                break;

            case CURRENT_WEEK:
                beginCalendar = getStartOfWeek(LocalDate.now());
                endCalendar = getEndOfWeek(LocalDate.now());
                break;
            case THIS_WEEK:
                beginCalendar = getStartOfWeek(LocalDate.now());
                endCalendar = getEndOfWeek(LocalDate.now());
                break;

            case LAST_WEEK:
                LocalDate lastWeek = LocalDate.now().minusWeeks(1);
                beginCalendar = getStartOfWeek(lastWeek);
                endCalendar = getEndOfWeek(lastWeek);
                break;

            case CURRENT_MONTH:
                beginCalendar = getStartOfMonth(LocalDate.now());
                endCalendar = getEndOfMonth(LocalDate.now());
                break;
            case THIS_MONTH:
                beginCalendar = getStartOfMonth(LocalDate.now());
                endCalendar = getEndOfMonth(LocalDate.now());
                break;


            case THIS_YEAR:
                LocalDate now = LocalDate.now();
                beginCalendar = toCalendar(now.with(TemporalAdjusters.firstDayOfYear()));
                endCalendar = toCalendar(now.with(TemporalAdjusters.lastDayOfYear()));
                break;

            case ALL_RANGES:
                break;

            default:
                beginCalendar = getStartOfWeek(LocalDate.now());
                endCalendar = getEndOfWeek(LocalDate.now());
                break;
        }

        log.info("FilterDateRange: {}", rangeFilter);

        return new DateRangeFilter(beginCalendar, endCalendar, overdue);
    }

    private static Calendar getStartOfWeek(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return toCalendar(startOfWeek);
    }

    private static Calendar getEndOfWeek(LocalDate date) {
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return toCalendar(endOfWeek);
    }

    private static Calendar getStartOfMonth(LocalDate date) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        return toCalendar(startOfMonth);
    }

    private static Calendar getEndOfMonth(LocalDate date) {
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return toCalendar(endOfMonth);
    }



    private static Calendar toCalendar(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        return calendar;
    }

}
