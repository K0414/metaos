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
 * Tester of predictions for each day as a whole.
 */
public class WholeDayVolumeProfileComparator 
        extends AbstractDailyDataProfileComparator {
    private final int groupSize;

    /**
     * Creates comparator of day forecasts with real data.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to make comparisons.
     * @param field the field to compare profile.
     * @param minimumDay minimum day to compare forecasts.
     * @deprecated Use constructor with filters instead
     */
    public WholeDayVolumeProfileComparator(
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final Calendar minimumDay) {
        this(instantGenerator, symbol, field, minimumDay, 1, true);
    }


    /**
     * Creates comparator of day forecasts with real data.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to make comparisons.
     * @param field the field to compare profile.
     * @param minimumDay minimum day to compare forecasts.
     * @param removeOutliers true to remove outliers before comparing.
     * @deprecated Use constructor with filters instead
     */
    public WholeDayVolumeProfileComparator(
            final CalUtils.InstantGenerator instantGenerator,
            final String symbol, final Field field, final Calendar minimumDay,
	    final boolean removeOutliers) {
        this(instantGenerator, symbol, field, minimumDay, 1, removeOutliers);
    }


    /**
     * Creates comparator of forecasts with real data groupping results into
     * blocks of given size before comparing.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to make comparisons.
     * @param field the field to compare profile.
     * @param minimumDay minimum day to compare forecasts.
     * @param groupSize size of the groups to be done before comparing.
     * @deprecated Use constructor with filters instead
     *
     * Thus, if there are N bins in a day to compare, they will be groupped,
     * in sequential order, in N/groupSize equally sized blocks, adding values.
     */
    public WholeDayVolumeProfileComparator(
            final CalUtils.InstantGenerator instantGenerator, 
            final String symbol, final Field field, 
            final Calendar minimumDay, final int groupSize, 
            final boolean cleanOutliers) {
        this(instantGenerator, symbol, field, new Filter[] {
                new Filter() {
                    public boolean filter(Calendar when, 
                            String symbol, Map<Field, Double> values) {
                        return !when.before(minimumDay);
                    }
                }
            }, groupSize, cleanOutliers);
    }


    /**
     * Creates comparator of forecasts with real data groupping results into
     * blocks of given size before comparing.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to make comparisons.
     * @param field the field to compare profile.
     * @param filters set of filters to apply to dates.
     * @param groupSize size of the groups to be done before comparing.
     * @param cleanOutliers true if outliers will be cleaned before comparing.
     *
     * Thus, if there are N bins in a day to compare, they will be groupped,
     * in sequential order, in N/groupSize equally sized blocks, adding values.
     */
    public WholeDayVolumeProfileComparator(
            final CalUtils.InstantGenerator instantGenerator, 
            final String symbol, final Field field, 
            final Filter filters[], final int groupSize, 
            final boolean cleanOutliers) {
        super(instantGenerator, symbol, field, filters, cleanOutliers);
        this.groupSize = groupSize;
    }


    /**
     * Creates comparator of forecasts with real data groupping results into
     * blocks of given size before comparing.
     * @param instantGenerator definition of 'instant', coherent with one
     * used in predictors.
     * @param symbol symbol to make comparisons.
     * @param field the field to compare profile.
     * @param filters set of filters to apply to dates.
     * @param cleanOutliers true if outliers will be cleaned before comparing.
     */
    public WholeDayVolumeProfileComparator(
            final CalUtils.InstantGenerator instantGenerator, 
            final String symbol, final Field field, final Filter filters[], 
            final boolean cleanOutliers) {
        this(instantGenerator, symbol, field, filters, 1, cleanOutliers);
    }




    //
    // Protected stuff -------------------------------
    //

    /**
     * Calculates quadratic differences between normalized windows of 
     * size 'windowSize' for vectors a and b and returns the maximum 
     * value for the each sample.
     * Values are returned as relative errors to vector 'b'.
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
            throw new RuntimeException("Sizes of predicted values "
                    + "and observed values do not match "
                    + "(" + a.length + " differs to " + b.length + ")");
        }

        final double diffs[] = new double[a.length / this.groupSize];
        double sumA = 0;
        double sumB = 0;
        for(int i=0; i<a.length; i+=this.groupSize) {
            for(int j=0; j<this.groupSize; j++) {
                sumA += (!Double.isNaN(a[i+j]) && a[i+j]>=0) ? a[i+j] : 0;
                sumB += (!Double.isNaN(b[i+j]) && b[i+j]>=0) ? b[i+j] : 0;
            }
        }

        for(int i=0; i<a.length; i+=this.groupSize) {
            double a2 = 0, b2 = 0;
            for(int j=0; j<this.groupSize; j++) {
                if(a[i+j]>=0 && b[i+j]>=0) {
                    a2 += Double.isNaN(a[i+j]) ? 0 : a[i+j];
                    b2 += Double.isNaN(b[i+j]) ? 0 : b[i+j];
                }
            }
            if(a2>0 && b2>0) {
                double e = Math.abs((a2 / sumA) - (b2 / sumB));
                diffs[i/this.groupSize] = e;
            } else {
                diffs[i/this.groupSize] = -1;
            }
        }

        this.takeNoteInternalStatistics(diffs);

        return diffs;
    }


    /**
     * Shows internal statistics for verification.
     */
    protected void finalize() {
        System.out.println(this.getClass() + " internal statistics: ->");
        System.out.println("  Number of comparisons:" + this.numOfComparisons);
        System.out.println("  Minimum size of comparison: " 
                + this.minimumSizeComparisons);
        System.out.println("  Maximum size of comparison: " 
                + this.maximumSizeComparisons);
        System.out.println("  Maximum number of void buckets in one comparison:"
                + this.maximumVoidBucketsComparison);
    }


    //
    // Private stuff --------------------------
    //

    private int numOfComparisons = 0;               // Internal statistics
    private int minimumSizeComparisons = Integer.MAX_VALUE;
    private int maximumSizeComparisons = 0;         // Internal statistics
    private int maximumVoidBucketsComparison = 0;   // Internal statistics

    /**
     * Take note of statistics for post-execution human verification.
     */
    private void takeNoteInternalStatistics(final double diffs[]) {
        this.numOfComparisons++;
        if(diffs.length > maximumSizeComparisons) {
            maximumSizeComparisons = diffs.length;
        }
        if(diffs.length < minimumSizeComparisons) {
            minimumSizeComparisons = diffs.length;
        }
        int voids = 0;
        for(int i=0; i<diffs.length; i++) if(diffs[i]<0) voids++;
        if(voids>maximumVoidBucketsComparison) {
            maximumVoidBucketsComparison = voids;
        }
    }
}
