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
 * Open Position data.
 */
public abstract class OpenPosition {
    public final Calendar when;
    public final double price;
    public final double cost;
    public final double netPrice;
    public final boolean isLong;
    
    protected OpenPosition(final Calendar when, final double price, 
            final double cost, final double netPrice, boolean isLong) {
        this.when = when;
        this.price = price;
        this.cost = cost;
        this.netPrice = netPrice;
        this.isLong = isLong;
    }


    public static class Long extends OpenPosition {
        public Long(final Calendar when,final double price,final double cost) {
            super(when, price, cost, price+cost, true);
        }
    }

    public static class Short extends OpenPosition {
        public Short(final Calendar when,final double price,final double cost) {
            super(when, price, cost, price-cost, false);
        }
    }
}
