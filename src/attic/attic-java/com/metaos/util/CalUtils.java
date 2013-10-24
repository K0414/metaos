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
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utilities for calendar generation.
 */
public class CalUtils {
    private static final long MILLISECONDS_PER_YEAR = 365 * 24 * 60 * 60 * 1000;

    /**
     * Creates a Calendar for given day/month/year as human antural
     * way (month 1 is January, day 1 is the 1st day of month and so).
     */
    public static Calendar createStrikeDate(final int day, final int month, 
            final int year) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }


    /**
     * Normalizes a date to be comparable with strike dates.
     */
    public static void normalizeCalendar(final Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }


    /**
     * Clones and normalizes a calendar.
     */
    public static Calendar clone(final Calendar cal) {
        final Calendar cl = (Calendar) cal.clone();
        normalizeCalendar(cl);
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
