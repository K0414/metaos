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
import java.util.logging.Logger;


/**
 * Prices source.
 */
public interface LineScanner {
    /**
     * Starts running scanner to get all prices from source.
     */
    public void run();

    /**
     * Resets reader to the start of source.
     */
    public void reset();

    /**
     * Gets the next set of prices.
     */
    public boolean next();

    /**
     * Gets the first set of prices.
     * Optional method: maybe has no sense for realtime sources.
     */
    public boolean first();

    /**
     * Gets the last set of prices.
     * Optional method: maybe has no sense for realtime sources.
     */
    public boolean last();

    /*
     * Search for data just at the desired time.
     * @return true if there is something at desired moment, false otherwise.
     *
    public boolean search(final Calendar time);

    /*
     * Search for data at the moment just before the desired time.
     * @return true if there is something before the desired moment, 
     * false otherwise. (Eq., true if the given time is greater than the
     * first moment in souce.)
     *
    public boolean searchClosestBefore(final Calendar time);

    /**
     * Search for data at the moment just after the desired time.
     * @return true if there is something after the desired moment, 
     * false otherwise. (Eq., true if the given moment is less than the
     * last moment.)
     *
    public boolean searchClosestAfter(final Calendar time);
    */

    /**
     * Closes the source.
     * After calling this method, every invokation to <i>next()</i>,
     * <i>first()</i> or <i>last()</i> will return false.
     */
    public void close();
}
