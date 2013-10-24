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
 * Decoration for a Market with operations book depth
 */
public interface MarketWithBookDepth extends Market {
    /**
     * Gets bid prices n moments before.
     * @param depth depth for operations book: 0 the top.
     */
    public double getBid(final Calendar when, final String what,
            final int depth);

    /**
     * Gets bid prices n moments before.
     * @param depth depth for operations book: 0 the top.
     */
    public double getBid(final int delay, final String what, 
            final int depth);


    /**
     * Gets ask prices in the given moment.
     * @param depth depth for operations book: 0 the top.
     */
    public double getAsk(final Calendar when, final String what, 
            final int depth);

    /**
     * Gets ask prices n moments before.
     * @param depth depth for operations book: 0 the top.
     */
    public double getAsk(final int delay, final String what,
            final int depth);
}
