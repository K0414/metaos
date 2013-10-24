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
import java.util.logging.Logger;

/**
 * Market price of prodcuts compossed by several other markets.
 */
public class CompossedMarket implements Market {
    private final List<Market> markets;
    private final Map<String, Market> sticky;

    public CompossedMarket(final List<Market> markets) {
        this.markets = markets;
        this.sticky = new HashMap<String, Market>();
    }

    /**
     * Gets prices in the given moment.
     */
    public double getPrice(final Calendar when, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getPrice(when, what);
            if(p!=0) return p;
        }
        for(final Market m : markets) {
            final double p = m.getPrice(when, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }

    /**
     * Gets prices N moments before the last recognized time (usually NOW).
     * This method is optionally implemented and is faced to get more 
     * performance.
     */
    public double getLastPrice(final int delay, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getLastPrice(delay, what);
            if(p!=0) return p;
        }
        for(final Market m : markets) {
            final double p = m.getLastPrice(delay, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }



    /**
     * Gets bid prices in the given moment.
     */
    public double getBid(final Calendar when, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getBid(when, what);
            if(p!=0) return p;
        }

        for(final Market m : markets) {
            final double p = m.getBid(when, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }


    /**
     * Gets bid prices n moments before.
     */
    public double getLastBid(final int delay, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getLastBid(delay, what);
            if(p!=0) return p;
        }

        for(final Market m : markets) {
            final double p = m.getLastBid(delay, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }



    /**
     * Gets ask prices in the given moment.
     */
    public double getAsk(final Calendar when, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getAsk(when, what);
            if(p!=0) return p;
        }

        for(final Market m : markets) {
            final double p = m.getAsk(when, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }

    /**
     * Gets ask prices n moments before.
     */
    public double getLastAsk(final int delay, final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getLastAsk(delay, what);
            if(p!=0) return p;
        }

        for(final Market m : markets) {
            final double p = m.getLastAsk(delay, what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }


    /**
     * Gets transaction costs.
     */
    public double getTransactionCosts(final String what) {
        final Market m2 = sticky.get(what);
        if(m2!=null) {
            final double p = m2.getTransactionCosts(what);
            if(p!=0) return p;
        }

        for(final Market m : markets) {
            final double p = m.getTransactionCosts(what);
            if(p!=0) {
                sticky.put(what, m);
                return p;
            }
        }
        return 0;
    }


}
