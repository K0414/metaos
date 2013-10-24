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
 * Line processor.
 */
public interface LineParser {
    /**
     * Implement this interface to get informed about unparseable objects into
     * a line and exceptions occured when parsing lines.
     */
    public static interface ErrorControl {
        /**
         * Callback invoked when a part of a line is parsed but is not possible
         * to recognize any kind of type.
         */
        public void unknownType(final String line, final int position,
                final Format formatter, final String part);

        /**
         * Callback invoked when an uncontrolled exception has occured
         * when parsing a line.
         */
        public void exception(final String line, final int position,
                final Format formatter, final String part,
                final Exception exception);
    }



     /**
      * Null object pattern to ignore problems parsing lines.
      */
     public static final ErrorControl nullErrorControl = new ErrorControl() {
         public void unknownType(final String line, final int position,
                        final Format formatter, final String part) {}
         public void exception(final String line, final int position,
                        final Format formatter, final String part,
                        final Exception exception) {}
     };




    /**
     * Parses a line and remembers it.
     * @return true if the whole line has been processed, false if some
     * fields are invalid.
     */
    public ParseResult parse(final String line);

    /**
     * Tests if provided line is valid in some sense.
     */
    public boolean isValid(final String line);

    /**
     * Subscribe a listener to concluding line events that will receive the
     * list of pairs with symbol and prices.
     * The set of <i>Listener</i>s will be notified after the set of
     * <i>CacheWriteable</i> has been invoked.
     * @see #addListener
     * @return this object to chain adding several commands.
     */
    public LineParser addCacheWriteable(final CacheWriteable listener);

    /**
     * Analyzes the line getting the symbol, but not reporting to listeners
     * the result.
     * @param index 0 for the first symbol of the line, 1 for the second...
     * @return null if there is no such symbol.
     */
    public String getSymbol(final String line, final int index);

    /**
     * Analyzes the line getting the local timestamp, 
     * but without reporting to listeners
     * the result.
     * @return null if there is no timestamp information
     */
    public Calendar getLocalTimestamp(final String line);

    /**
     * Analyzes the line getting the UTC timestamp, 
     * but without reporting to listeners
     * the result.
     * @return null if there is no timestamp information
     */
    public Calendar getUTCTimestamp(final String line);

    /**
     * Adds a filter to validate the line.
     * @return the same object to chain adding filters.
     */
    public LineParser addFilter(final Filter filter);

    /**
     * Resets parser removing memory, filters, listeners...
     */
    public void reset();

    /**
     * Sets the object to control errors when parsing lines.
     */
    public void setErrorControl(final ErrorControl errorControl);

    /**
     * Gets the object to control errors when parsing lines.
     */
    public ErrorControl getErrorControl();
}
