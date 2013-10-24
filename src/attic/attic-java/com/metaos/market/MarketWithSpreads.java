/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.market;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Adaptor to sets directly spreads to inserted prices.
 */
public class MarketWithSpreads implements MarketReadWrite {
    private final MarketReadWrite adaptee;
    private final Map<String, Double> spreads;

    /**
     * @param symbolRoots root for product names to apply spreads.
     * @param spreads array with the same length as <i>symbolRoots</i>
     * with the spreads to apply fot each symbol root.
     */
    public MarketWithSpreads(final MarketReadWrite market,
            final String symbolRoots[], final double spreads[]) {
        this.adaptee = market;
        this.spreads = new ConcurrentHashMap<String, Double>();
        for(int i=0; i<symbolRoots.length && i<spreads.length; i++) {
            this.spreads.put(symbolRoots[i], spreads[i]);
        }
    }

    public void setPrice(final Calendar when, final String what,
            final double how) {
        final double halfSpread = searchForSpread(what)/2;
        this.adaptee.setPrice(when, what, how);
        this.adaptee.setBid(when, what, how - halfSpread);
        this.adaptee.setAsk(when, what, how + halfSpread);
    }

    public void setBid(final Calendar when,final String what,final double how) {
        final double halfSpread = searchForSpread(what)/2;
        this.adaptee.setBid(when, what, how);
        this.adaptee.setPrice(when, what, how + halfSpread);
        this.adaptee.setAsk(when, what, how + halfSpread*2);
    }

    public void setAsk(final Calendar when, final String what,
            final double how) {
        final double halfSpread = searchForSpread(what)/2;
        this.adaptee.setAsk(when, what, how);
        this.adaptee.setBid(when, what, how - 2*halfSpread);
        this.adaptee.setPrice(when, what, how - halfSpread);
    }

    public double getPrice(final Calendar when, final String what) {
        return this.adaptee.getPrice(when, what);
    }

    public double getLastPrice(final int delay, final String what) {
        return this.adaptee.getLastPrice(delay, what);
    }

    public double getBid(final Calendar when, final String what) {
        return this.adaptee.getBid(when, what);
    }

    public double getAsk(final Calendar when, final String what) {
        return this.adaptee.getAsk(when, what);
    }

    public double getLastBid(final int delay, final String what) {
        return this.adaptee.getLastBid(delay, what);
    }

    public double getLastAsk(final int delay, final String what) {
        return this.adaptee.getLastAsk(delay, what);
    }

    public double getTransactionCosts(final String what) {
        return this.adaptee.getTransactionCosts(what);
    }


    // Private stuff
    private double searchForSpread(final String what) {
        final char[] chars = what.toCharArray();
        int i=0;
        while(i<chars.length) {
            if(!Character.isLetter(chars[i]) && !Character.isDigit(chars[i])) {
                break;
            }
            i++;
        }
        if(i==0) return 0;
        final String root = new String(chars, 0, i);
        final Double d = this.spreads.get(root);
        if(d==null) return 0;
        else return d.doubleValue();
    }
}
