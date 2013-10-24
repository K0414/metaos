/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Utilities for calendar generation.
 */
public class CalUtils {
    private static final long MILLISECONDS_PER_YEAR = 365 * 24 * 60 * 60 * 1000;
    private static final DateFormat internalDateFormat=getDateFormat(
            "d-M-y HH:mm:ss.SSS");
    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT");


    /**
     * Template method to generate instants from timestamps.
     */
    public interface InstantGenerator {
        public int generate(final Calendar when);
        public int maxInstantValue();
    }




    /**
     * Gets a date format which sets GMT+0 to created dates.
     */
    public static DateFormat getDateFormat(final String format) {
        final DateFormat df = new SimpleDateFormat(format, Locale.UK);
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return df;
    }


    /**
     * Gets a reset calendar (set to zero).
     */
    public static Calendar getZeroCalendar() {
        final Calendar cal = Calendar.getInstance(GMT0, Locale.UK);
        cal.setTimeInMillis(0);
        return cal;
    }


    /**
     * Creates a Calendar for given day/month/year as human natural
     * way (month 1 is January, day 1 is the 1st day of month and so)
     * fixed at 12 hours, 0 seconds, 0 minutes of the day GMT+0.
     */
    public static Calendar createDate(final int day, final int month, 
            final int year) {
        try {
            final Date d=internalDateFormat.parse(
                    day + "-" + month + "-" + year + " 00:00:00.000");
            final Calendar cal = getZeroCalendar();
            cal.setTimeInMillis(d.getTime());
            return cal;
        } catch(ParseException pe) {
            throw new RuntimeException(pe);
        }
    }


    /**
     * Normalizes a date to be comparable with another date,
     * fixed at 12 hours, 0 seconds, 0 minutes of the day GMT+0.
     */
    public static void normalize(final Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }


    /**
     * Clones and normalizes a calendar.
     */
    public static Calendar normalizedClone(final Calendar cal) {
        final Calendar cl = (Calendar) cal.clone();
        normalize(cl);
        return cl;
    }


    /**
     * Calculates difference in years between to dates.
     * @param a first element of substraction of dates.
     * @param b second element of substraction of dates.
     * @return a-b in fractions of year.
     */
    public static double differenceInYears(final Calendar a, final Calendar b) {
        final long millisDif = a.getTimeInMillis() - b.getTimeInMillis();
        return millisDif / MILLISECONDS_PER_YEAR;
    }
}
