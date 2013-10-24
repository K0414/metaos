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
package com.luisfcanals.techan.data;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Gets forecasts from finanzas.com/analisistecnico
 */
public class FinanzasDotComForecast {
    private static final Logger log = Logger.getLogger(
            FinanzasDotComForecast.class.getPackage().getClass().getName());

    private final SimpleHttpGet getter;
    private final Map<String, Double> lastPrices, targetPrices, supports,
            resistances;


    public FinanzasDotComForecast() throws MalformedURLException {
        this.getter = new SimpleHttpGet(
                "http://www.finanzas.com/analisistecnico");
        this.lastPrices = new HashMap<String, Double>();
        this.targetPrices = new HashMap<String, Double>();
        this.supports = new HashMap<String, Double>();
        this.resistances = new HashMap<String, Double>();
    }

    public void refresh() throws IOException {
        this.getter.refresh();
        
        throw new UnsupportedOperationException("PROCESS!!!!");
    }

    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     */
    public double estimatedRelativeProfit(final String underlying) {
        try {
            final double a = this.lastPrices.get(underlying);
            final double b = this.targetPrices.get(underlying);
            return (b-a) / a;
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }


    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     */
    public double getLastPrice(final String underlying) {
        try {
            return this.lastPrices.get(underlying);
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }

    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     */
    public double getTargetPrice(final String underlying) {
        try {
            return this.targetPrices.get(underlying);
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }

    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     */
    public double getSupport(final String underlying) {
        try {
            return this.supports.get(underlying);
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }

    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     */
    public double getResistance(final String underlying) {
        try {
            return this.resistances.get(underlying);
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }


    /**
     * Underlyings from Mercado Continuo should be appended with ".MC".
     *
    public Tendence getTendence(final String underlying) {
        try {
            return this.tendences.get(underlying);
        } catch(NullPointerException npe) {
            throw new NoSuchElementException("No underlying " + underlying);
        }
    }
    */
}
