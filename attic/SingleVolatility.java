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

import org.apache.commons.math.stat.descriptive.moment.*;

/**
 * Calculates volatility over a set of data.
 */
public class SingleVolatility implements Volatility {
    private static final Logger log = Logger.getLogger(SingleVolatility.class
            .getPackage().getClass().getName());

    private final String underlying;
    private final Calendar firstDay, lastDay;
    private final double[] prices;
    private double vol = -1;
    private final Variance variance;

    public SingleVolatility(final String underlying, final Calendar firstDay,
            final Calendar lastDay, final double prices[]) {
        this.prices = prices;
        this.underlying = underlying;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.variance = new Variance();
    }

    /**
     * Gets volatility for given values.
     *
     * Atention: non current implementation.
     */
    public double getVolatility() {
        if(this.vol == -1) {
            this.vol = Math.sqrt(this.variance.evaluate(prices));
        }
        return this.vol;
    }


    public double[] getValues() {
        return this.prices;
    }
}
