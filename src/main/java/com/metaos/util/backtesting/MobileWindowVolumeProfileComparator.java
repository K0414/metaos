/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.util.*;
import com.metaos.datamgt.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.*;

/**
 * Tester of volume profile predictions using mobile windows.
 *
 * TODO: Generalize, since it's not implemented only for volume.
 */
public class MobileWindowVolumeProfileComparator 
        extends AbstractDailyDataProfileComparator {
    private final int windowSize;

    /**
     * Compares forecasts with real volume profiles.
     * @param windowSize size in 'instants' of window to test forecastss.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to compare profile.
     * @param field the field to compare profile.
     * @param minimumDay minimum day to compare forecasts.
     * @param cleanOutliers true if outliers should be cleaned before comparing.
     */
    public MobileWindowVolumeProfileComparator(final int windowSize,
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final Calendar minimumDay,
            final boolean cleanOutliers) {
        super(instantGenerator, symbol, field, new Filter[] {
                new Filter() {
                    public boolean filter(Calendar when, String symbol, 
                            Map<Field, Double> values) {
                        return !when.before(minimumDay);
                    }
                }
            }, cleanOutliers);
        this.windowSize = windowSize;
    }

    //
    // Protected stuff -------------------------------
    //

    /**
     * Calculates quadratic differences between normalized windows of 
     * size 'windowSize' for vectors a and b and returns the maximum 
     * value for the each sample.
     * Errors are relative to vector 'b'.
     * <br/>
     * Values less than 0 are ignored and not considered for contrast.
     */
    protected double[] contrast(final double a[], final double b[]) {
        if(a.length!=b.length) {
            System.err.println("Danger!!");
            System.err.println("Size of expected values (given by "
                    + "'instantGenerator.maxInstantValues()' function) "
                    + "does not match with size of predicted values vector");
            System.err.println("As information, currently used "
                    + "'instantGenerator' object is an instance of "
                    + this.instantGenerator.getClass());
            throw new RuntimeException("Sizes of predicted volume profile "
                    + "and expected observed volume profile do not match "
                    + "(" + a.length + " differs to " + b.length + ")");
        }
        final double diffs[] = new double[a.length-windowSize];
        for(int i=0; i<a.length-windowSize; i++) {
            double sumA = 0;
            double sumB = 0;
            for(int j=i; j<i+windowSize; j++) {
                sumA += a[j]>=0 ? a[j] : 0;
                sumB += b[j]>=0 ? b[j] : 0;
            }

            double maxDiff = -1;
            for(int j=i; j<i+windowSize; j++) {
                if(a[j]>0 && b[j]>0) {
                    double e = (a[j] / sumA) - (b[j] / sumB);
                    e = (e*e) / b[j];
                    if(maxDiff < e) maxDiff=e;
                }
            }
            diffs[i] = maxDiff;
        }
        return diffs;
    }
}
