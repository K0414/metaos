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
import com.metaos.util.*;

/**
 * Cache optimized for sequential accesses from now to past prices.
 */
public class SequentialAccessCache implements CacheReadable, CacheWriteable {
    /* @TODO: test if the use of circular fixed-size arrays improves
       performance. */
    private final LinkedList<Calendar> moments;
    private final LinkedList<Map<String, Double>> bids;
    private final LinkedList<Map<String, Double>> asks;
    private final LinkedList<Map<String, Double>> prices;
    private final int size;
    private Calendar lastInsert;

    /* Afterburner */
    private int lastDelay;
    private Map<String,Double> lastReadAsk;
    private Map<String,Double> lastReadBid;
    private Map<String,Double> lastReadPrices;
    private Map<String,Double> lastWriteAsk;
    private Map<String,Double> lastWriteBid;
    private Map<String,Double> lastWritePrices;


    //
    // Concurrency lockers
    //
    private final ReadWriteLock pricesLock = new ReentrantReadWriteLock();
    private final ReadWriteLock bidsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock asksLock = new ReentrantReadWriteLock();


    public SequentialAccessCache(final int size) {
        this.moments = new LinkedList<Calendar>();
        this.prices = new LinkedList<Map<String, Double>>();
        this.bids = new LinkedList<Map<String, Double>>();
        this.asks = new LinkedList<Map<String, Double>>();
        this.size = size;
        this.lastInsert = CalUtils.getZeroCalendar();
    }


    public void set(final Calendar when, final Field field, final String what,
            final double how) {
        pricesLock.writeLock().lock();
        try {
            if(when.before(this.lastInsert)) {
                throw new UnsupportedOperationException("Time only goes in "
                        + "one way");
            }

            if(!this.lastInsert.equals(when)) {
                createNewBucket();
            }
            this.lastWritePrices.put(field + "-" + what, how);
            this.prices.getLast().put(field + "-" + what, how);
            this.lastInsert = when;
        } finally {
            pricesLock.writeLock().unlock();
        }
    }


    public void setBid(final Calendar when, final Field field, 
            final String what, final double how) {
        bidsLock.writeLock().lock();
        try {
             if(when.before(this.lastInsert)) {
                throw new UnsupportedOperationException("Time only goes in "
                        + "one way");
            }

            if(!this.lastInsert.equals(when)) {
                createNewBucket();
            }
            this.lastWriteBid.put(field + "-" + what, how);
            this.bids.getLast().put(field + "-" + what, how);
            this.lastInsert = when;
        } finally {
            bidsLock.writeLock().unlock();
        }
    }


    public void setAsk(final Calendar when, final Field field, 
            final String what, final double how) {
        asksLock.writeLock().lock();
        try {
            if(when.before(this.lastInsert)) {
                throw new UnsupportedOperationException("Time only goes in "
                        + "one way (and " + when + " is before " + lastInsert);
            }

            if(!this.lastInsert.equals(when)) {
                createNewBucket();
            }
            this.lastWriteAsk.put(field + "-" + what, how);
            this.asks.getLast().put(field + "-" + what, how);
            this.lastInsert = when;
        } finally {
            asksLock.writeLock().unlock();
        }
    }


    public double get(final Calendar when, final Field field, 
            final String what) {
        pricesLock.readLock().lock();
        try {
            final Iterator<Map<String, Double>> itPrices = prices.iterator();
            final Iterator<Calendar> itMoments = moments.iterator();
            while(itPrices.hasNext() && itMoments.hasNext()) {
                final Calendar moment = itMoments.next();
                final Map<String, Double> data = itPrices.next();
                if(moment.equals(when)) {
                    return data.get(field + "-" + what);
                } else if(when.after(moment)) {
                    return 0;
                }
            }
            return 0;
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getLast(final int delay, final Field field, 
            final String what) {
        pricesLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWritePrices.get(field + "-" + what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.prices.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(field + "-" + what);
            }
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getBid(final Calendar when, final Field field, 
            final String what) {
        throw new UnsupportedOperationException("not implemented yet");
        /*
        bidsLock.readLock().lock();
        try {
        } finally {
            pricesLock.readLock().unlock();
        }
        */
    }

    public double getAsk(final Calendar when, final Field field,
            final String what) {
        throw new UnsupportedOperationException("not implemented yet");
        /*
        asksLock.readLock().lock();
        try {
        } finally {
            asksLock.readLock().unlock();
        }
        */
    }

    public double getLastBid(final int delay, final Field field, 
            final String what) {
        bidsLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWriteBid.get(field + "-" + what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.bids.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(field + "-" + what);
            }
        } finally {
            bidsLock.readLock().unlock();
        }
    }

    public double getLastAsk(final int delay, final Field field,
            final String what) {
        asksLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWriteAsk.get(field + "-" + what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.asks.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(field + "-" + what);
            }
        } finally {
            asksLock.readLock().unlock();
        }
    }



    //
    // Private stuff ---------------------------------
    //
    synchronized private void createNewBucket() {
        this.lastWritePrices = new HashMap<String, Double>();
        this.lastWriteBid = new HashMap<String, Double>();
        this.lastWriteAsk = new HashMap<String, Double>();

        this.prices.add(this.lastWritePrices);
        this.bids.add(this.lastWriteBid);
        this.asks.add(this.lastWriteAsk);

        if(this.prices.size()>this.size) {
            this.prices.removeFirst();
            this.bids.removeFirst();
            this.asks.removeFirst();
        }
    }
}
