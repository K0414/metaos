/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.*;
import com.metaos.datamgt.*;
import com.metaos.engine.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.*;
import com.metaos.signalgrt.predictors.specific.volume.*;

/**
 * Tester of daily predictions.
 * If <code>dump results</code> functionallity is enabled, predicted and real
 * data are dumped into files named <code>yyyy-MM-dd-prediction.csv</code>
 * and <code>yyyy-MM-dd-real.csv</code> and, at the same time,
 * <code>backtest.lastPrediction</code> and <code>backtest.lastReal</code>
 * variables are defined into the R engine with predicted and real values.
 */
public abstract class AbstractDailyDataProfileComparator 
        implements ForecastingTest {
    private static final Logger log = Logger.getLogger(
            AbstractDailyDataProfileComparator.class.getPackage().getName());
    private static final SimpleDateFormat dateFormat = 
            new SimpleDateFormat("yyyy-MM-dd");
    private final boolean cleanOutliers;
    private final Field field;
    private final Errors<Integer> minuteErrors;
    private final Errors<String> dayErrors;
    private final double[] dailyData;
    private final boolean dumpResults;
    private final String symbol;
    private final Filter[] filters;
    protected final CalUtils.InstantGenerator instantGenerator;

    /**
     * Compares forecasts with real intrday daily data.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param field the field to compare profile.
     * @param filters Filters to apply over date.
     * @param symbol to make comparisons.
     * @param cleanOutliers true if values greater than a fraction of last 
     * value should be removed (false if not).
     */
    public AbstractDailyDataProfileComparator(
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final Filter filters[],
            final boolean cleanOutliers) {
        this.instantGenerator = instantGenerator;
        this.minuteErrors = new Errors<Integer>();
        this.dayErrors = new Errors<String>();
        this.field = field;
        this.symbol = symbol;
        this.dailyData = new double[this.instantGenerator.maxInstantValue()];
        for(int i=0; i<this.dailyData.length; i++) {
            this.dailyData[i] = Double.NaN;
        }

        this.dumpResults = System.getProperty("DUMPRESULTS") != null;
        this.filters = filters;
        this.cleanOutliers = cleanOutliers;
    }


    /**
     * Gets collector for daily (intra-day) error vectors.
     */
    public Errors<String> getDayErrors() {
        return this.dayErrors;
    }


    /**
     * Gets collector for minute by minute (inter-day) error vectors.
     */
    public Errors<Integer> getMinuteErrors() {
        return this.minuteErrors;
    }



    public void notify(final ParseResult parseResult) {
        final int index = this.instantGenerator.generate(
                parseResult.getLocalTimestamp(this.symbol));
        // Effect:only takes memory of each minute for the last received day
        if(parseResult.values(this.symbol) != null &&
                parseResult.values(this.symbol).get(field) != null) {
            this.dailyData[index] = parseResult.values(this.symbol).get(field);
        }
    }


    /**
     * Evaluates errors if the moment verifies filters.
     */
    public void evaluate(final Calendar when, final double[] predictedValues) {
        if(this.filters!=null) {
            for(final Filter f : filters) {
                if(!f.filter(when, this.symbol, null)) return;
            }
        }

        final String dayStr = dateFormat.format(when.getTime());

        // Clean values, removing outliers
        if(this.cleanOutliers) {
            log.info("Cleaning outliers from real data before "
                    + "comparing prediction");
            RemoveVolumeData.cleanOutliers(this.dailyData);
        }

        final double errors[] = 
                contrast(predictedValues, this.dailyData);

        boolean anyErrorPresent = false;
        boolean predictionValid = false;
        boolean dailyDataValid = false;
        for(int i=0; i<errors.length; i++) {
            if(!Double.isNaN(errors[i]) && errors[i]>=0) {
                anyErrorPresent = true;
                this.minuteErrors.addError(i, errors[i]);
                this.dayErrors.addError(dayStr, errors[i]);
            } else {
                if(!Double.isNaN(predictedValues[i])) predictionValid = true;
                if(!Double.isNaN(this.dailyData[i])) dailyDataValid = true;
            }
        }
        // Dumps data.
        if(this.dumpResults && anyErrorPresent) {
            try {
                final PrintWriter predictedFile = new PrintWriter(
                    new FileWriter(dayStr +"-prediction.csv"));
                final PrintWriter realFile = new PrintWriter(
                    new FileWriter(dayStr +"-real.csv"));
            
                for(int i=0; i<predictedValues.length; i++) {
                    predictedFile.println(predictedValues[i]);
                }
                for(int i=0; i<this.dailyData.length; i++) {
                    realFile.println(this.dailyData[i]);
                }

                predictedFile.flush();
                predictedFile.close();
                realFile.flush();
                realFile.close();

                if(System.getProperty("RCONSOLE")!=null) {
                    final StringBuffer predictedValsStr = new StringBuffer()
                            .append(predictedValues[0]);
                    final StringBuffer realValsStr = new StringBuffer()
                            .append(this.dailyData[0]);
                    for(int i=1; i<predictedValues.length; i++) {
                        predictedValsStr.append(",").append(predictedValues[i]);
                    }
                    for(int i=1; i<this.dailyData.length; i++) {
                        realValsStr.append(",").append(this.dailyData[i]);
                    }
                    final R r = Engine.getR();
                    r.eval("backtest.lastPrediction<-c("+predictedValsStr+")");
                    r.eval("backtest.lastReal<-c(" + realValsStr + ")");
                }
            } catch(IOException ioe) {
                log.log(Level.SEVERE, "Error dumping results to file", ioe);
            }
        } else if(!anyErrorPresent) {
            log.finest("Prediction comparison invalid");
            if(!predictionValid) log.finest("All values in prediction are NaN");
            if(!dailyDataValid) log.finest("All values in daily data are NaN");
        }



        // Cleans dailyData, to avoid contamination
        for(int i=0; i<this.dailyData.length; i++) {
            this.dailyData[i] = Double.NaN;
        }
    }


    //
    // Protected stuff -------------------------------
    //

    /**
     * Hook method to calculate differences between normalized 
     * vectors a and b and returns the maximum value for the each sample.
     * Values less than 0 are ignored and not considered for contrast.
     */
    protected abstract double[] contrast(final double a[], final double b[]);
}
