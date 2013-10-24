/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.util.backtesting;

import java.io.*;
import java.util.*;
import com.metaos.datamgt.*;
import com.metaos.util.*;
import com.metaos.engine.*;

/**
 * Interface with R engine to deal with errors.
 */
public class Statistics {
    private final R rEngine;

    /**
     * Creates a new object wrapping <i>errors/statistics.r</i>
     * R file.
     */
    public Statistics(final R rEngine) throws IOException {
        this.rEngine = rEngine;
        this.rEngine.evalFile("util/statistics.r");
        this.rEngine.eval("statistics <- Statistics()");
    }


    /**
     * Resets statistics.
     */
    public void reset() {
        this.rEngine.eval("statistics$reset()");
    }

    /**
     * Adds an error to statistics register.
     */
    public void addValue(final double val) {
        if(val<0) return;
        this.rEngine.eval("statistics$addValue(" + val + ")");
    }

    /**
     * Gets total accumulated errors
     */
    public double accum() {
        // R-bridge doesn't work in this way:
        //      this.rEngine.evalDouble("max(statistics$listAll())")
        this.rEngine.eval("x <- statistics$listAll()");
        return this.rEngine.evalDouble("sum(x)");
    }

    /**
     * Gets mean of erros.
     */
    public double mean() {
        // R-bridge doesn't work in this way:
        //      this.rEngine.evalDouble("mean(statistics$listAll())")
        this.rEngine.eval("x <- statistics$listAll()");
        return this.rEngine.evalDouble("mean(x)");
    }


    /**
     * Gets variance of errors.
     */
    public double var() {
        // R-bridge doesn't work in this way:
        //      this.rEngine.evalDouble("var(statistics$listAll())")
        this.rEngine.eval("x <- statistics$listAll()");
        return this.rEngine.evalDouble("var(x)");
    }

    /**
     * Gets maximum value.
     */
    public double max() {
        // R-bridge doesn't work in this way:
        //      this.rEngine.evalDouble("max(statistics$listAll())")
        this.rEngine.eval("x <- statistics$listAll()");
        return this.rEngine.evalDouble("max(x)");
    }

    /**
     * Gets minimum value.
     */
    public double min() {
        this.rEngine.eval("x <- statistics$listAll()");
        return this.rEngine.evalDouble("min(x)");
    }

    /**
     * Gets the N-quantiles.
     */
    public double[] quantiles(final int N) {
        double c = 1.0/N;
        this.rEngine.eval("x <- statistics$listAll()");
        this.rEngine.eval("q <- quantile(x, probs=seq(" +c+ ",1," +c+ "), "
                + "names=FALSE)");
        return this.rEngine.evalDoubleArray("q");
    }

    /**
     * Plots errors
     */
    public void plot() {
        this.rEngine.eval("x <- statistics$listAll()");
        this.rEngine.eval("plot(x)");
    }
}
