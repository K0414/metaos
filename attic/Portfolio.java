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
package com.luisfcanals.deriva.portfolio;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.luisfcanals.deriva.*;
import com.luisfcanals.deriva.market.*;

/**
 * Set of products and other
 */
public class Portfolio {
    private final List<Product> products;
    private double cost;

    public Portfolio() {
        this.products = new ArrayList<Product>();
        this.cost = 0.0f;
    }


    /**
     * Adds a new product to portfolio.
     */
    public void add(final Product p) {
        this.products.add(p);
        this.cost += p.getAcquisitionPrice();
    }


    /**
     * Sets market status for this portfolio.
     */
    public void setMarket(final Market market) {
        for(final Product p : products) {
            p.setMarket(market);
        }
    }


    /**
     * Returns net profit for the given date and with set market.
     */
    public double getProfit(final Calendar when) {
        double t = 0;
        for(final Product p : products) {
            t += p.getProfit(when);
        }
        return t;
    }


    /**
     * Gets total cost of this portfolio.
     */
    public double totalCost() {
        return this.cost;
    }
}
