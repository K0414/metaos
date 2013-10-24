/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.datamgt;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;
import net.tasecurity.taslib.util.CacheMap;

/**
 * Random access cache for prices.
 */
public class RandomAccessCache implements CacheReadable, CacheWriteable {
    private final Map<Calendar, Map<String, Double>> prices;
    private final Map<Calendar, Map<String, Double>> bids;
    private final Map<Calendar, Map<String, Double>> asks;
    private final Map<String, Double> lastPrices;
    private final Map<String, Double> lastAsks;
    private final Map<String, Double> lastBids;

    //
    // Concurrency lockers
    //
    private final ReadWriteLock pricesLock = new ReentrantReadWriteLock();
    private final ReadWriteLock bidsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock asksLock = new ReentrantReadWriteLock();


    public RandomAccessCache(final int size) {
        this.prices = new CacheMap<Calendar, Map<String, Double>>(size);
        this.bids = new CacheMap<Calendar, Map<String, Double>>(size);
        this.asks = new CacheMap<Calendar, Map<String, Double>>(size);
        this.lastPrices = new HashMap<String, Double>(size);
        this.lastAsks = new HashMap<String, Double>(size);
        this.lastBids = new HashMap<String, Double>(size);
    }

    public void set(final Calendar when, final Field field,
            final String what, final double how) {
        pricesLock.writeLock().lock();
        try {
            Map<String, Double> moment = this.prices.get(when);
            if(moment==null) {
                moment = new HashMap<String, Double>();
                this.prices.put(when, moment);
            }
            this.lastPrices.put(field + "-" + what, how);
            moment.put(field + "-" + what, how);
        } finally {
            pricesLock.writeLock().unlock();
        }
    }


    public void setBid(final Calendar when, final Field field, 
            final String what, final double how) {
        bidsLock.writeLock().lock();
        try {
            Map<String, Double> moment = this.bids.get(when);
            if(moment==null) {
                moment = new HashMap<String, Double>();
                this.bids.put(when, moment);
            }
            this.lastBids.put(field + "-" + what, how);
            moment.put(field + "-" + what, how);
        } finally {
            bidsLock.writeLock().unlock();
        }
    }

    public void setAsk(final Calendar when, final Field field,
            final String what, final double how) {
        asksLock.writeLock().lock();
        try {
            Map<String, Double> moment = this.asks.get(when);
            if(moment==null) {
                moment = new HashMap<String, Double>();
                this.asks.put(when, moment);
            }
            this.lastAsks.put(field + "-" + what, how);
            moment.put(field + "-" + what, how);
        } finally {
            asksLock.writeLock().unlock();
        }
    }

    public double get(final Calendar when, final Field field,
            final String what) {
        pricesLock.readLock().lock();
        try {
            Map<String, Double> moment = this.prices.get(when);
            if(moment==null) {
                throw new NoSuchElementException();
            } else {
                return moment.get(field + "-" + what);
            }
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getLast(final int delay, final Field field,
            final String what) {
        pricesLock.readLock().lock();
        try {
            if(delay!=0) {
                throw new UnsupportedOperationException(
                    "Method only available for last prices (delay=0)");
            }
            final Double how = this.lastPrices.get(field + "-" + what);
            if(how==null) {
                throw new NoSuchElementException();
            } else {
                return how.doubleValue();
            }
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getBid(final Calendar when, final Field field,
            final String what) {
        bidsLock.readLock().lock();
        try {
            Map<String, Double> moment = this.bids.get(when);
            if(moment==null) {
                throw new NoSuchElementException();
            } else {
                return moment.get(field + "-" + what);
            }
        } finally {
            pricesLock.readLock().unlock();
        }     
    }

    public double getAsk(final Calendar when, final Field field,
            final String what) {
        asksLock.readLock().lock();
        try {
            Map<String, Double> moment = this.asks.get(when);
            if(moment==null) {
                throw new NoSuchElementException();
            } else {
                return moment.get(field + "-" + what);
            }
        } finally {
            asksLock.readLock().unlock();
        }
    }

    public double getLastBid(final int delay, final Field field,
            final String what) {
        bidsLock.readLock().lock();
        try {
            if(delay!=0) {
                throw new UnsupportedOperationException(
                    "Method only available for last prices (delay=0)");
            }
            final Double how = this.lastBids.get(field + "-" + what);
            if(how==null) {
                throw new NoSuchElementException();
            } else {
                return how.doubleValue();
            }
        } finally {
            bidsLock.readLock().unlock();
        }
    }

    public double getLastAsk(final int delay, final Field field,
            final String what) {
        asksLock.readLock().lock();
        try {
            if(delay!=0) {
                throw new UnsupportedOperationException(
                    "Method only available for last prices (delay=0)");
            }
            final Double how = this.lastAsks.get(field + "-" + what);
            if(how==null) {
                throw new NoSuchElementException();
            } else {
                return how.doubleValue();
            }
        } finally {
            asksLock.readLock().unlock();
        }
    }
}
