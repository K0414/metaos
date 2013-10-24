/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.simple;

import com.metaos.signalgrt.predictors.*;
import com.metaos.engine.*;
import com.metaos.util.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * PCA as a predictor of data received in a sequence of equally-sized vectors.
 * Ideally designed to receive a vector with all trade information in each
 * instant (for example, a set of N traded products) .
 *
 * <h2>NaN to deal with unknown data</h2> 
 * Internal design is prepared to remove starting and ending unknown data,
 * represented by NaN. This feature is specially thought to deal with intraday
 * data on markets that open and close at certain times.
 * If any datum in vector is NaN, the <b>all other data MUST be NaN too</b>.
 * <br/>
 * Be careful: it cannot deal with NaN <b>among</b> valid block of data.
 * <br/>
 * For example:
 * <ul>
 *  <li>These sequence of vectors valid: 
 *  [NaN,NaN,NaN], [NaN,NaN,NaN], [1,2,3], [4,5,6], [NaN,NaN,NaN]</li>
 *  <li>But this other are not: [NaN,NaN,NaN], [1,2,3], [NaN,NaN,NaN], 
 *      [4,5,6], [NaN,NaN,NaN]</li>
 *  <li>Neither this one: [NaN,3,NaN], [1,2,3], [4,5,6], [NaN,NaN,NaN]</li>
 * </ul>
 * In invalid cases, NaN will be changed by zeros and an info logs will be
 * reported.
 *
 * <h2>R engine</h2>
 * R engine is used to perform calculi.<br/>
 * After each prediction, in <code>vals</code> variable input data matrix
 * is stored and <code>previousVals</code> contains previous <code>vals</code>
 * variable.<br/>
 *
 * <h2>Usage in backtest</h2>
 * Remember that, according to BacktesterAgent, if you're backtesting, a
 * prediction is done at the end of a day and it's evaluated at the end of
 * the next valid day. So, in backtesting usages, <code>previousVals</code>
 * contains the value of volumes used in the last evaulated prediction, and
 * <code>vals</code> contains a matrix of data unsued to predict anything (yet).
 *
 * <h2>Thread safe</h2>
 * <br/>ATTENTION: Not thread safe.
 * <br/>Moreover: usage of R engine is NOT CONCURRENT, thanks to faulty
 * JRI implementation.
 */
public class PCAPredictor implements Predictor {
    protected static final Logger log = Logger.getLogger(PCAPredictor.class
            .getPackage().getName());
    private static final SimpleDateFormat parsingDate = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    protected final double matrix[][];
    protected final CalUtils.InstantGenerator instantGenerator;
    protected final double minimumExplainedVariance;
    protected int minInstant, maxInstant;
    protected int lastInstant = -1;
    protected Calendar lastLearningTime;

    private final double scale;
    private final boolean shiftToScale;


    /**
     * Creates an PCAPredictor predictor with given parameters.
     * Predicted values WILL be shift to get to desired statistical expectance.
     *
     * @param minimumExplainedVariance quantity of variance that should be
     * explained by eigenvectors.
     * @param scale inputs and predictions will be scaled to sum desired scale
     * value (0 to not scale)
     */
    public PCAPredictor(final CalUtils.InstantGenerator instantGenerator,
            final double minimumExplainedVariance, final double scale) {
        log.finest("Created PCAPredictor minimumExplainedVariance=" 
                + minimumExplainedVariance + ", scale=" + scale
                + ", maxNumberOfInstants="+instantGenerator.maxInstantValue());
        this.matrix = new double[instantGenerator.maxInstantValue()][];
        this.instantGenerator = instantGenerator;
        this.minInstant = 0;
        this.maxInstant = instantGenerator.maxInstantValue();
        this.minimumExplainedVariance = minimumExplainedVariance;
        this.scale = scale;
        this.shiftToScale = scale>0;

        final R r = Engine.getR();
        try {
            r.lock();
            r.eval("vals<-c(0)");
            r.eval("previousVals<-c(0)");
        } finally {
            r.release();
        }
    }


    /**
     * Predicted values will not be shift or rescaled.
     */
    public PCAPredictor(final CalUtils.InstantGenerator instantGenerator,
            final double minimumExplainedVariance) {
        log.finest("Created PCAPredictor minimumExplainedVariance=" 
                + minimumExplainedVariance + ", no scaled"
                + ", maxNumberOfInstants="+instantGenerator.maxInstantValue());
        this.matrix = new double[instantGenerator.maxInstantValue()][];
        this.instantGenerator = instantGenerator;
        this.minInstant = 0;
        this.maxInstant = instantGenerator.maxInstantValue();
        this.minimumExplainedVariance = minimumExplainedVariance;
        this.scale = 0;
        this.shiftToScale = false;

        final R r = Engine.getR();
        try {
            r.lock();
            r.eval("vals<-c(0)");
            r.eval("previousVals<-c(0)");
        } finally {
            r.release();
        }
    }



    /**
     * Creates an PCAPredictor predictor with given parameters.
     * Only first eigenvector will be used to explain variance.
     */
    public PCAPredictor(final CalUtils.InstantGenerator instantGenerator) {
        this(instantGenerator, 0.0d);
    }


    public double predict(final Calendar ignored) {
        throw new UnsupportedOperationException(
                "Cannot predict only one value");
    }


    /**
     * Gets profile for the principal component analysis.
     */
    public double[] predictVector(final Calendar when) {
        if(this.lastLearningTime!=null 
                    && !when.after(this.lastLearningTime)) {
            log.info("Trying to predict not after the last learning time:"
                + "learningTime=" + lastLearningTime + ",\nwhen=" + when);
        }
        this.lastInstant = -1;

        final int n = matrix.length;
        if(matrix[0] == null) {
            // Not trained yet
            return emptyAnswerVector();
        }
        final int m = matrix[0].length;


        // Search for NaN/zeros at the begining and at the end
        int maxIndexOfZerosAtTheBegining = 0;
        outter: for(int i=0; i<n; i++) {
            if(matrix[i]!=null) {
                for(int j=0; j<m; j++) {
                    if(matrix[i]!=null && !Double.isNaN(matrix[i][j]) 
                            && matrix[i][j]!=0) {
                        break outter;
                    }
                }
            }
            maxIndexOfZerosAtTheBegining++;
        }

        int minIndexOfZerosAtTheEnd = n;
        outter: for(int i=n-1; i>maxIndexOfZerosAtTheBegining; i--) {
            for(int j=0; j<m; j++) {
                if(matrix[i]!=null && !Double.isNaN(matrix[i][j])
                        && matrix[i][j]!=0) {
                    break outter;
                }
            }
            minIndexOfZerosAtTheEnd--;
        }

        // Pass elements to R
        final int n2 = minIndexOfZerosAtTheEnd - maxIndexOfZerosAtTheBegining;

        final R r = Engine.getR();
        try {
            r.lock();
            r.eval("previousVals<-vals");
            r.eval("vals<-array(dim=c(" + n2 + "," + m + "))");
            for(int i=0; i<n2; i++) {
                int i2 = i + maxIndexOfZerosAtTheBegining;
                if(matrix[i2]==null) {
                    matrix[i2] = new double[m];
                }
                for(int j=0; j<m; j++) {
                    if(Double.isNaN(matrix[i2][j])) {
                        log.info("Found NaN at position " + i2 + "," + j
                            + "; changed by 0 in PCA matrix positions (" 
                            + (i+1) + "," + (j+1) + ")"
                            + " - prediction requested for moment "
                            + parsingDate.format(when.getTime()));
                    
                    }
                    r.eval("vals[" + (i+1) + "," + (j+1) + "]<-" 
                          + (Double.isNaN(matrix[i2][j]) ? 0 : matrix[i2][j]));
                }
            }
            // Rescale, if needed.
            if(this.shiftToScale) {
                for(int j=0; j<m; j++) {
                    r.eval("vals[," + (j+1) + "]<-" + this.scale 
                        + "*vals[," + (j+1) + "]/sum(vals[," + (j+1) + "])");
                }
            }


            // Perform PCA
            r.eval("pca.vals<-prcomp(vals, xret=TRUE, scale=TRUE)");
        
            // Calculate how many eigenvectors should be used to 
            // explain variance
            final double vars[] = r.evalDoubleArray("pca.vals$sdev^2");
            final double totalVar = r.evalDouble("sum(pca.vals$sdev^2)");
            int index = 0;
            double explainedVar = 0;
            while((explainedVar/totalVar)<this.minimumExplainedVariance) {
                explainedVar = explainedVar + vars[index];
                index++;
            }
            r.eval("index<-" + index);

            // Use eigenvectors in a linear combination
            r.eval("A<-pca.vals$x[,1:index]");
            r.eval("Q<-A %*% solve( t(A) %*% A ) %*% t(A)");
// @@@ Change this when pluggable core were programmed.
            r.eval("pred.vals <- Q %*% vals[,1]");

            // Shift to get desired expectance
            if(this.shiftToScale) {
                r.eval("pred.vals<-pred.vals + "
                        + this.scale + "/length(pred.vals) "
                        + " - mean(pred.vals)");
            }

            // Create answer.
            final double core[] = r.evalDoubleArray("pred.vals"); 
            final double characteristic[] = emptyAnswerVector();
            for(int i=0; i<n2; i++) {
                characteristic[i+maxIndexOfZerosAtTheBegining] = core[i];
            }
        
            return characteristic;
        } catch(REngineException ree) {
            return emptyAnswerVector();
        } finally {
            r.release();
        }
    }


    public void learnVector(final Calendar when, final double[] vals) {
        boolean somethingIsNotNaN = false;
        for(int i=0; i<vals.length; i++) {
            if(!Double.isNaN(vals[i])) {
                somethingIsNotNaN = true;
                break;
            }
        }
        if(!somethingIsNotNaN) return;

        final int currentInstant = this.instantGenerator.generate(when);
        if(currentInstant>=this.matrix.length) return;

        for(int i=this.lastInstant+1; i<currentInstant; i++) {
            this.matrix[i] = new double[vals.length];
            for(int j=0; j<this.matrix[i].length; j++) {
                this.matrix[i][j] = Double.NaN;
            }
        }
        this.matrix[currentInstant] = new double[vals.length];

        for(int i=0; i<vals.length; i++) {
            this.matrix[currentInstant][i] = vals[i];
        }

        this.lastInstant = currentInstant;
        this.lastLearningTime = (Calendar) when.clone();
    }


    /**
     * Method less efficient than the other one with an array as argument.
     */
    public void learnVector(final Calendar when, final List<Double> vals) {
        final double vals2[] = new double[vals.size()];
        for(int i=0; i<vals2.length; i++) vals2[i] = vals.get(i);
        this.learnVector(when, vals2);
    }


    /**
     * Not supported, only several values can be learnt at same time.
     */
    public void learnValue(final Calendar ignored, final double val) {
        throw new UnsupportedOperationException("PCA cannot be trained with "
                + "only one value per bin");
    }


    public void reset() {
    }


    public String toString() {
        return "PCAPredictor";
    }


    //
    // Private stuff ---------------------------------
    //

    private double[] emptyAnswerVector() {
        final double characteristic[] = new double[this.matrix.length];
        for(int i=0; i<characteristic.length; i++) {
            characteristic[i] = Double.NaN;
        }
        return characteristic;
    }

}
