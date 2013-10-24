/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.specific;

import java.util.*;
import java.util.logging.Logger;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.simple.*;
import com.metaos.util.*;
import com.metaos.datamgt.*;

/**
 * Interface to deifne how to difference between days.
 * It's a strategy in the sense of Design Patterns.
 */
public interface TypeOfDaysStrategy {
    /**
     * How many different types of day are recognized.
     */
    public int numberOfDays();

    /**
     * Which type of day is given date.
     */
    public int typeOfDay(final Calendar when);


    //
    // Inner classes inner ------------------
    //


    /**
     * Differences between Mon, Thu,...Friday and third Friday of month.
     */
    public static class MonTueWedThuFri3Fri implements TypeOfDaysStrategy {
        private static final Logger log = Logger.getLogger(
                TypeOfDaysStrategy.class.getPackage().getName());
        public int numberOfDays() { return 6; }
        public int typeOfDay(final Calendar when) {
            switch(when.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY: return 0;
                case Calendar.TUESDAY: return 1;
                case Calendar.WEDNESDAY: return 2;
                case Calendar.THURSDAY: return 3;
                case Calendar.FRIDAY:
                    return when.get(Calendar.WEEK_OF_MONTH)==3 ? 5 : 4;
                default:
                    log.info("Don't how to deal with SATURDAY or SUNDAYS");
                    return -1;
            }
        }
    }



    /**
     * Differences between Mon, Thu,...and Friday 
     */
    public static class MonTueWedThuFri implements TypeOfDaysStrategy {
        private static final Logger log = Logger.getLogger(
                TypeOfDaysStrategy.class.getPackage().getName());
        public int numberOfDays() { return 5; }
        public int typeOfDay(final Calendar when) {
            switch(when.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY: return 0;
                case Calendar.TUESDAY: return 1;
                case Calendar.WEDNESDAY: return 2;
                case Calendar.THURSDAY: return 3;
                case Calendar.FRIDAY: return 4;
                default:
                    log.info("Don't how to deal with SATURDAY or SUNDAYS");
                    return -1;
            }
        }
    }
}
