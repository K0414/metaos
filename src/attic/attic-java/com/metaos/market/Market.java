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
 * Market price of prodcuts.
 */
public interface Market {
    /**
     * Gets prices in the given moment.
     */
    public double getPrice(final Calendar when, final String what);

    /**
     * Gets prices N moments before the last recognized time (usually NOW).
     * This method is optionally implemented and is faced to get more 
     * performance.
     */
    public double getLastPrice(final int delay, final String what);


    /**
     * Gets bid prices in the given moment.
     */
    public double getBid(final Calendar when, final String what);

    /**
     * Gets bid prices n moments before.
     */
    public double getLastBid(final int delay, final String what);


    /**
     * Gets ask prices in the given moment.
     */
    public double getAsk(final Calendar when, final String what);

    /**
     * Gets ask prices n moments before.
     */
    public double getLastAsk(final int delay, final String what);


    /**
     * Gets transaction costs.
     */
    public double getTransactionCosts(final String what);
}
