/*
 * Copyright Luis F. Canals luisf.canals@gmail.com , 2010 - 2011
 * Reservados todos los derechos.
 * Este documento es material confidencial propiedad de 
 * Luis F. Canals luisf.canals@gmail.com 
 * Se prohibe la divulgación o revelación de su contenido
 * sin el permiso previo y por escrito del propietario.
 *
 * Copyright Luis F. Canals luisf.canals@gmail.com , 2010 - 2011
 * All rights reserved.
 * This document consists of confidential information property of 
 * Luis F. Canals luisf.canals@gmail.com
 * Its content may not be used or disclosed without
 * prior written permission of the owner.
 */
package com.luisfcanals.techan.maths;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import org.apache.commons.math.*;
import org.apache.commons.math.linear.*;
import org.apache.commons.math.stat.descriptive.moment.*;
import org.apache.commons.math.stat.correlation.*;

/**
 * Calculates volatility of a given portfolio.
 */
public class PortfolioVolatility implements Volatility {
    private static final Logger log = Logger.getLogger(PortfolioVolatility
            .class.getPackage().getClass().getName());

    private final Volatility[] volatilities;
    private final Calendar firstDay, lastDay;
    private double[] weights;
    private double vol = -1;

    public PortfolioVolatility(final Volatility volatilities[], 
            final double[] weights, final Calendar firstDay, 
            final Calendar lastDay) {
        this.volatilities = volatilities;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.weights = weights;
    }

    /**
     * Sets weights and voids previous calculations.
     */
    public void setWeights(final double[] weights) {
        this.weights = weights;
        this.vol = -1;
    }

    /**
     * Gets volatility for given values.
     *
     * Atention: non concurrent implementation.
     */
    public double getVolatility() {
        if(this.vol == -1) {
            final Covariance covBuilder = new Covariance();
            final RealMatrix weightedVolatilities = 
                    new BlockRealMatrix(volatilities.length, 1);
            final RealMatrix cov = new BlockRealMatrix(
                    volatilities.length, volatilities.length);
            for(int i=0; i<volatilities.length; i++) {
                weightedVolatilities.setEntry(i, 0, 
                        weights[i] * volatilities[i].getVolatility());
                cov.setEntry(i, i, 1);
                for(int j=i+1; j<volatilities.length; j++) {
                    final double s1 = volatilities[i].getVolatility();
                    final double s2 = volatilities[j].getVolatility();
                    final double c = covBuilder.covariance(
                            volatilities[i].getValues(),
                            volatilities[j].getValues());
                    cov.setEntry(i,j, c/(s1*s2));
                    cov.setEntry(j,i, c/(s1*s2));
                }
            }
            this.vol = Math.sqrt(weightedVolatilities.transpose().multiply(
                    cov.multiply(weightedVolatilities)).getEntry(0,0));
        }
        return this.vol;
    }


    /**
     * Gets the list of values.
     */
    public double[] getValues() {
        return new double[0];
    }
}
