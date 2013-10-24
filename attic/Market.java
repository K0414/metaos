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
package com.luisfcanals.deriva.market;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Gets current prices and stores it.
 */
public class Market {
    private final Map<Calendar, Map<String, Double>> prices;

    public Market() {
        this.prices = new HashMap<Calendar, Map<String, Double>>();
    }

    public void setPrice(final Calendar when, final String what,
            final double how) {
        Map<String, Double> moment = this.prices.get(when);
        if(moment==null) {
            moment = new HashMap<String, Double>();
            this.prices.put(when, moment);
        }
        moment.put(what, how);
    }


    /**
     * Returns stored price or -1 if no data is available for this position.
     */
    public double getPrice(final Calendar when, final String what) {
        Map<String, Double> moment = this.prices.get(when);
        if(moment==null) {
            return -1;
        } else {
            return moment.get(what);
        }
    }
}
