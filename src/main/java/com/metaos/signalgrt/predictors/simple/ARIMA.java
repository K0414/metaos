/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.simple;

import com.metaos.signalgrt.predictors.*;
import com.metaos.engine.*;
import java.util.*;

/**
 * ARIMA predictor for the next value.
 * ATTENTION: Not thread safe.
 */
public class ARIMA implements Predictor {
    private final List<Double> memory;
    private final int p,q,d;
    private final R interpreteR;
    private final int forecastSize;

    /**
     * Creates an ARIMA predictor with given parameters.
     *
     * @param p first argument for ARIMA(p,d,q) model
     * @param d second argument for ARIMA(p,d,q) model
     * @param q thrid argument for ARIMA(p,d,q) model
     * @param forecastSize number of elements to calculate in advance when
     * <code>predictVector</code> method was called.
     */
    public ARIMA(final int p,final int d,final int q,final int forecastSize) {
        this.p = p;
        this.q = q;
        this.d = d;
        this.interpreteR = Engine.getR();
        this.memory = new ArrayList<Double>();
        this.forecastSize = forecastSize;
    }

    /**
     * Creates an ARIMA predictor with given parameters to forecast next value.
     *
     * @param p first argument for ARIMA(p,d,q) model
     * @param d second argument for ARIMA(p,d,q) model
     * @param q thrid argument for ARIMA(p,d,q) model
     * <code>predictVector</code> method was called.
     */
    public ARIMA(final int p,final int d,final int q) {
        this(p,d,q,1);
    }


    public double predict(final Calendar ignored) {
        if(this.memory.size()<2) return 0;
        final double[] memoryArray = new double[this.memory.size()];
        for(int i=0; i<this.memory.size(); i++) {
            memoryArray[i] = this.memory.get(i);
        }
        try {
            this.interpreteR.lock();
            this.interpreteR.set("x", memoryArray);
            this.interpreteR.eval("ar<-arima(x=x,order=c(" + p + "," + d + ","
                + q + "))");
            this.interpreteR.eval("f<-predict(ar, n.ahead=1)");
            return this.interpreteR.evalDouble("f$pred[1]");
        } catch(Exception e) {
            return Double.NaN;
        } finally {
            this.interpreteR.release();
        }
    }


    public double[] predictVector(final Calendar ignored) {
        final double[] memoryArray = new double[this.memory.size()];
        if(this.memory.size()<2) return memoryArray;
        for(int i=0; i<this.memory.size(); i++) {
            memoryArray[i] = this.memory.get(i);
        }
        try {
            this.interpreteR.lock();
            this.interpreteR.set("x", memoryArray);
            this.interpreteR.eval("ar<-arima(x=x,order=c(" + p + "," + d + ","
                    + q + "))");
            this.interpreteR.eval("f<-predict(ar,n.ahead=" 
                    + this.forecastSize +  ")");
            final double forecast[] = new double[this.forecastSize];
            for(int i=0; i<this.forecastSize; i++) {
                forecast[i] = this.interpreteR.evalDouble("f$pred[" + i + "]");
            }
            return forecast;
        } finally {
            this.interpreteR.release();
        }
    }


    public void learnVector(final Calendar ignored, final double[] vals) {
        for(int i=0; i<vals.length; i++) {
            this.memory.add(vals[i]);
        }
    }


    public void learnVector(final Calendar ignored, final List<Double> vals) {
        this.memory.addAll(vals);
    }


    public void learnValue(final Calendar ignored, final double val) {
        this.memory.add(val);
    }


    public void reset() {
        this.memory.clear();
    }


    public String toString() {
        return "ARIMA(" + p + "," + d + "," + q + ")";
    }
}
