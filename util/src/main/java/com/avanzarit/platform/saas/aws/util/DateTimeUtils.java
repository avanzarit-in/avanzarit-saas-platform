package com.avanzarit.platform.saas.aws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class containing methods for formatting and parsing dates in various formats.
 */
public class DateTimeUtils {

    public static final String TIMEZONE_BASELINE = "UTC";
    public static final String TIMESTAMP_WITH_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIMESTAMP_WITHOUT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String TIMESTAMP_WITHOUT_MILLISECONDS_WITHOUT_Z = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DAY = "yyyyMMdd";
    public static final String DAY_HOUR = "yyyyMMdd'T'HH";
    public static final String DASHED_DAY = "yyyy-MM-dd";
    public static final String YEARWEEK = "yyyyww";
    public static final String MONTH = "yyyyMM";

    /**
     * Converts the given {@link Date} object to its formatted String representation using the
     * {@value TIMESTAMP_WITH_MILLISECONDS} format.
     */
    public static String getFormattedTimestamp(Date date) {
        return getDateFormat(TIMESTAMP_WITH_MILLISECONDS).format(date);
    }

    /**
     * Converts the given {@link Date} object to its formatted String representation using the
     * {@value TIMESTAMP_WITHOUT_MILLISECONDS} format.
     */
    public static String getFormattedTimestampNoMs(Date date) {
        return getDateFormat(TIMESTAMP_WITHOUT_MILLISECONDS).format(date);
    }

    /**
     * Converts the given {@link Date} object to its formatted String representation using the
     * {@value TIMESTAMP_WITHOUT_MILLISECONDS} format.
     */
    public static String getFormattedTimestampNoMsNoZ(Date date) {
        return getDateFormat(TIMESTAMP_WITHOUT_MILLISECONDS_WITHOUT_Z).format(date);
    }

    /**
     * Converts the given String date to its Java {@link Date} object equivalent. The String is
     * expected to be in the {@value TIMESTAMP_WITHOUT_MILLISECONDS} format.
     */
    public static Date getDateFromFormattedTimestampWithoutMs(String timestampWithoutMs)
            throws ParseException {
        return getDateFormat(TIMESTAMP_WITHOUT_MILLISECONDS).parse(timestampWithoutMs);
    }

    /**
     * Converts the given String date to its Java {@link Date} object equivalent. The String is
     * expected to be in the {@value TIMESTAMP_WITH_MILLISECONDS} format.
     */
    public static Date getDateFromFormattedTimestampWithMs(String timestampWithMs)
            throws ParseException {
        return getDateFormat(TIMESTAMP_WITH_MILLISECONDS).parse(timestampWithMs);
    }

    /**
     * Gets the 'day' component of the given {@link Date} object as a String in the format
     * {@value DAY}.
     */
    public static String getFormattedDay(Date date) {
        return getDateFormat(DAY).format(date);
    }

    /**
     * Gets the 'day' and 'hour' component of the given {@link Date} object as a String in the format
     * {@value DAY_HOUR}.
     */
    public static String getFormattedDayHour(Date date) {
        return getDateFormat(DAY_HOUR).format(date);
    }

    /**
     * Gets the 'day' component of the given {@link Date} object as a String in the format
     * {@value DASHED_DAY}.
     */
    public static String getFormattedDashedDay(Date date) {
        return getDateFormat(DASHED_DAY).format(date);
    }

    /**
     * Gets the 'week' component of the given {@link Date} object as a String in the format
     * {@value YEARWEEK}.
     */
    public static String getFormattedYearWeek(Date date) {
        return getDateFormat(YEARWEEK).format(date);
    }

    /**
     * Converts the given date String in the format {@value DASHED_DAY} to the
     * {@value TIMESTAMP_WITHOUT_MILLISECONDS} format.
     */
    public static String fromDashedDayToTimestampWithoutMs(String date) throws ParseException {
        if (date != null) {
            Date parsedDate = getDateFormat(DASHED_DAY).parse(date);
            return getFormattedTimestampNoMs(parsedDate);
        } else {
            return null;
        }
    }

    /**
     * Converts the given String date to its Java {@link Date} object equivalent. The String is
     * expected to be in the {@value DASHED_DAY} format.
     */
    public static Date getDateFromDashedDate(String dashedDay) throws ParseException {
        return getDateFormat(DASHED_DAY).parse(dashedDay);
    }

    /**
     * Gets a {@link DateFormat} instance for the given pattern. The timezone is set to
     * {@value TIMEZONE_BASELINE} by default.
     */
    public static DateFormat getDateFormat(String pattern) {
        SimpleDateFormat result = new SimpleDateFormat(pattern);
        result.setTimeZone(TimeZone.getTimeZone(TIMEZONE_BASELINE));
        return result;
    }

    /**
     * Gets a {@link Date} instance for the given pattern.
     */
    public static Date getDashedDateFromFormattedTimestamp(String timestampWithMs)
            throws ParseException {
        return getDateFormat(DASHED_DAY).parse(timestampWithMs);
    }
}
