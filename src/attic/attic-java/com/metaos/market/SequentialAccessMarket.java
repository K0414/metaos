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
import java.util.concurrent.locks.*;
import java.util.logging.Logger;
import net.tasecurity.taslib.util.CacheMap;

/**
 * Market optimized for sequential accesses from now to past prices.
 */
public class SequentialAccessMarket implements MarketReadWrite {
    /* @TODO: test if the use of circular fixed-size arrays improves
       performance. */
    private final LinkedList<Calendar> moments;
    private final LinkedList<Map<String, Double>> bids;
    private final LinkedList<Map<String, Double>> asks;
    private final LinkedList<Map<String, Double>> prices;
    private final double transactionCosts;
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


    public SequentialAccessMarket(final double transactionCosts, 
            final int size) {
        this.moments = new LinkedList<Calendar>();
        this.prices = new LinkedList<Map<String, Double>>();
        this.bids = new LinkedList<Map<String, Double>>();
        this.asks = new LinkedList<Map<String, Double>>();
        this.transactionCosts = transactionCosts;
        this.size = size;
        this.lastInsert = Calendar.getInstance();
        this.lastInsert.setTimeInMillis(0);
    }


    public void setPrice(final Calendar when, final String what,
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
            this.lastWritePrices.put(what, how);
            this.prices.getLast().put(what, how);
            this.lastInsert = when;
        } finally {
            pricesLock.writeLock().unlock();
        }
    }


    public void setBid(final Calendar when, final String what,
            final double how) {
        bidsLock.writeLock().lock();
        try {
             if(when.before(this.lastInsert)) {
                throw new UnsupportedOperationException("Time only goes in "
                        + "one way");
            }

            if(!this.lastInsert.equals(when)) {
                createNewBucket();
            }
            this.lastWriteBid.put(what, how);
            this.bids.getLast().put(what, how);
            this.lastInsert = when;
        } finally {
            bidsLock.writeLock().unlock();
        }
    }


    public void setAsk(final Calendar when, final String what,
            final double how) {
        asksLock.writeLock().lock();
        try {
            if(when.before(this.lastInsert)) {
                throw new UnsupportedOperationException("Time only goes in "
                        + "one way");
            }

            if(!this.lastInsert.equals(when)) {
                createNewBucket();
            }
            this.lastWriteAsk.put(what, how);
            this.asks.getLast().put(what, how);
            this.lastInsert = when;
        } finally {
            asksLock.writeLock().unlock();
        }
    }


    public double getPrice(final Calendar when, final String what) {
        pricesLock.readLock().lock();
        try {
            final Iterator<Map<String, Double>> itPrices = prices.iterator();
            final Iterator<Calendar> itMoments = moments.iterator();
            while(itPrices.hasNext() && itMoments.hasNext()) {
                final Calendar moment = itMoments.next();
                final Map<String, Double> data = itPrices.next();
                if(moment.equals(when)) {
                    return data.get(what);
                } else if(when.after(moment)) {
                    return 0;
                }
            }
            return 0;
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getLastPrice(final int delay, final String what) {
        pricesLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWritePrices.get(what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.prices.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(what);
            }
        } finally {
            pricesLock.readLock().unlock();
        }
    }

    public double getBid(final Calendar when, final String what) {
        throw new UnsupportedOperationException("not implemented yet");
        /*
        bidsLock.readLock().lock();
        try {
        } finally {
            pricesLock.readLock().unlock();
        }
        */
    }

    public double getAsk(final Calendar when, final String what) {
        throw new UnsupportedOperationException("not implemented yet");
        /*
        asksLock.readLock().lock();
        try {
        } finally {
            asksLock.readLock().unlock();
        }
        */
    }

    public double getLastBid(final int delay, final String what) {
        bidsLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWriteBid.get(what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.bids.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(what);
            }
        } finally {
            bidsLock.readLock().unlock();
        }
    }

    public double getLastAsk(final int delay, final String what) {
        asksLock.readLock().lock();
        try {
            if(delay==0) {
                return this.lastWriteAsk.get(what);
            } else {
                final Iterator<Map<String, Double>> it = 
                        this.asks.descendingIterator();
                for(int i=0; i<delay; i++) {
                    it.next();
                }
                return it.next().get(what);
            }
        } finally {
            asksLock.readLock().unlock();
        }
    }


    public double getTransactionCosts(final String what) {
        return this.transactionCosts;
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
