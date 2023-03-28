package com.xdp.utils;

import com.xdp.lib.exceptions.BusinessException;
import com.xdp.lib.utils.DateUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class DateTimeUtils {

    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static Date getStartOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year, month, day, 0, 0, 0);
        return cal.getTime();
    }

    public static Date getEndOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year, month, day, 23, 59, 59);
        return cal.getTime();
    }

    public static Date getStartOfDateMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, cal.getActualMinimum(Calendar.DATE), 0, 0, 0);
        return cal.getTime();
    }

    public static Date getEndOfDateMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, cal.getActualMaximum(Calendar.DATE), 23, 59, 59);
        return cal.getTime();
    }

    public static Date minDate() {
        try {
            String MIN_DATE = "1000-01-01T00:00:00+0700";
            return DateUtil.toDate(MIN_DATE, FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date maxDate() {
        try {
            String MAX_DATE = "9999-12-31T23:59:59+0700";
            return DateUtil.toDate(MAX_DATE, FORMAT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date plusDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date subMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        cal.set(year, month, 7, 0, 0, 0);
        return cal.getTime();
    }

    public static boolean afterOrEqual(Date date1, Date date2) {
        return !date1.before(date2);
    }

    public static boolean beforeOrEqual(Date date1, Date date2) {
        return !date1.after(date2);
    }

    public static Date minusDay(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    public static String formatDate(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static Integer getMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar.get(Calendar.MONTH);
    }

    public static Date convertStringToDate(String strDate, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            if (!strDate.isEmpty()) {
                date = dateFormat.parse(strDate);
            }

        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return date;
    }

    public static Date toDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Date> parseDate(Date fromDate, Date toDate) {
        List<Date> result = new ArrayList<>();
        if (fromDate.after(toDate)) return result;
        while (fromDate.compareTo(toDate) <= 0) {
            result.add(fromDate);
            fromDate = plusDay(fromDate, 1);
        }
        return result;
    }

    public static Date getDateByMonth(Integer month, Integer year){
        Calendar cal = Calendar.getInstance();
        if (!ObjectUtils.isEmpty(month)) {

            if (month == 1) {
                cal.set(year, Calendar.JANUARY , 1, 0, 0, 0);
            }
            if (month == 2) {
                cal.set(year, Calendar.FEBRUARY , 1, 0, 0, 0);
            }
            if (month == 3) {
                cal.set(year, Calendar.MARCH , 1, 0, 0, 0);
            }
            if (month == 4) {
                cal.set(year, Calendar.APRIL , 1, 0, 0, 0);
            }
            if (month == 5) {
                cal.set(year, Calendar.MAY , 1, 0, 0, 0);
            }
            if (month == 6) {
                cal.set(year, Calendar.JUNE , 1, 0, 0, 0);
            }
            if (month == 7) {
                cal.set(year, Calendar.JULY , 1, 0, 0, 0);
            }
            if (month == 8) {
                cal.set(year, Calendar.AUGUST , 1, 0, 0, 0);
            }
            if (month == 9) {
                cal.set(year, Calendar.SEPTEMBER , 1, 0, 0, 0);
            }
            if (month == 10) {
                cal.set(year, Calendar.OCTOBER , 1, 0, 0, 0);
            }
            if (month == 11) {
                cal.set(year, Calendar.NOVEMBER , 1, 0, 0, 0);
            }
            if (month == 12) {
                cal.set(year, Calendar.DECEMBER, 1, 0, 0, 0);
            }
        }
        return cal.getTime();
    }

    public static Map<String, Date> getDateRangeByMonths(Integer firstMonth, Integer secondMonth, Integer year) {
        Map<String, Date> map = new HashMap<>();
        if (!ObjectUtils.isEmpty(firstMonth) && !ObjectUtils.isEmpty(secondMonth)) {
            if (firstMonth < secondMonth) {
                map.put("startDate", getStartOfDateMonth(getDateByMonth(firstMonth, year)));
                map.put("endDate", getEndOfDateMonth(getDateByMonth(secondMonth, year)));
            }
        }
        return map;
    }

    public static Map<String, Date> getDateRangeByQuarter(Integer quarter, Integer year){
        switch (quarter){
            case 1:
                return getDateRangeByMonths(1, 3, year);
            case 2:
                return getDateRangeByMonths(4, 6, year);
            case 3:
                return getDateRangeByMonths(7, 9, year);
            case 4:
                return getDateRangeByMonths(10, 12, year);
            default:
                return null;
        }
    }

    public static Integer calculateMonthDiff(Date firstDate, Date secondDate) {
        int month = 0;
        String d1 = new SimpleDateFormat("yyyy-MM-dd").format(firstDate);
        String d2 = new SimpleDateFormat("yyyy-MM-dd").format(secondDate);
        Period diff = Period.between(LocalDate.parse(d1), LocalDate.parse(d2));
        if (!org.apache.commons.lang3.ObjectUtils.isEmpty(diff.getYears())){
            month += 12*diff.getYears();
        }
        if (!org.apache.commons.lang3.ObjectUtils.isEmpty(diff.getMonths())){
            month += diff.getMonths();
        }
        return month;
    }

    public static Date plusSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static Date plusMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    public static Map<String, Date> getDateRangeOfLastYear(int year){
        Map<String, Date> map = new HashMap<>();
        int lastYear = year-1;
        Calendar begin = Calendar.getInstance();
        begin.set(lastYear, begin.getActualMinimum(Calendar.MONTH), 1, 0, 0, 0);
        map.put("startDate", begin.getTime());

        Calendar end = Calendar.getInstance();
        end.set(lastYear, end.getActualMaximum(Calendar.MONTH), end.getActualMaximum(Calendar.DATE), 23, 59, 59);
        map.put("endDate", end.getTime());
        return map;
    }

    public static Integer getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

}
