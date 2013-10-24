/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.signalgrt.predictors.simple;

import com.metaos.signalgrt.predictors.*;
import java.util.*;
import java.util.logging.*;

/**
 * Moving window predictor for the next value using injectable kernel.
 * ATTENTION: Not thread safe.
 */
public class KernelMovingWindow implements Predictor {
    protected static final Logger log = Logger.getLogger(
            KernelMovingWindow.class.getPackage().getName());
    private final double memory[];
    private int head;
    private Kernel kernel;
    private Calendar lastLearntMoment;

    public KernelMovingWindow(final int memorySize) {
        this.memory = new double[memorySize];
        this.head = 0;
    }


    public double predict(final Calendar when) {
        if(this.lastLearntMoment != null 
                && !when.after(this.lastLearntMoment)) {
            log.info("Trying to predict not after the last learning time:"
                    + "learningTime=" + lastLearntMoment + ",\nwhen=" + when);
        }
        return this.kernel.eval(this.memory, this.head);
    }


    public double[] predictVector(final Calendar when) {
        return new double[] { predict(when) };
    }


    public void learnVector(final Calendar when, final double[] vals) {
        if(vals.length>memory.length) {
            for(int i=vals.length-memory.length-1;i<vals.length; i++) {
                this.learnValue(null, vals[i]);
            }
        } else {
            for(int i=0; i<vals.length; i++) {
                this.learnValue(null, vals[i]);
            }
        }
        this.lastLearntMoment = (Calendar) when.clone();
    }


    public void learnVector(final Calendar when, final List<Double> vals) {
        if(vals.size()>memory.length) {
            for(int i=vals.size()-memory.length-1;i<vals.size(); i++) {
                if(vals.get(i)!=null) this.learnValue(null, vals.get(i));
            }
        } else {
            for(int i=0; i<vals.size(); i++) {
                if(vals.get(i)!=null) this.learnValue(null, vals.get(i));
            }
        }
        this.lastLearntMoment = (Calendar) when.clone();
    }


    public void learnValue(final Calendar when, final double val) {
        if(Double.isNaN(val)) return;
        this.memory[this.head] = val;
        this.head = this.head + 1;
        if(this.head >= this.memory.length) {
            this.head = 0;
        }
        this.lastLearntMoment = (Calendar) when.clone();
    }


    public void reset() {
        for(int i=0; i<this.memory.length; i++) {
            this.memory[i] = 0;
        }
        this.head = 0;
    }


    public String toString() {
        return "kMA(" + this.memory.length + ")";
    }

    //
    // Additional methods ------------------------------
    //

    /**
     * Returns memorized values.
     */
    public double[] getCore() {
        final double core[] = new double[this.memory.length];
        for(int i=0, j=this.head+1; i<this.memory.length; i++, j++) {
            if(j>=this.memory.length) j=0;
            core[i] = this.memory[j];
        }
        return core;
    }

    
    /**
     * Sets kernel for next prediction.
     */
    public void setKernel(final KernelMovingWindow.Kernel kernel) {
        this.kernel = kernel;
    }


    //
    // Public inner interfaces -------------------------
    //

    /**
     * Interface for kernel function.
     */
    public static interface Kernel {
        /**
         * Gets the maximum memory size the kernel may manage.
         */
        public int getKernelSize();

        /**
         * Evaluates moving average memory.
         * @param headIndex starting position for circular buffer.
         * @param memory memory as circular buffer.
         */
        public double eval(final double[] memory, final int headIndex);
    }
}
