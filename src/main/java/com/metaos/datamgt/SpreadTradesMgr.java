/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

/**
 * Strategy to manage trades spread into several lines.
 * 
 * Usage protocol:
 *  <ul>
 *      <li>LineParser parses a line</li>
 *      <li>Parsed line is communicated to SpreadTradesMgr</li>
 *      <li>SpreadTradesMgr decides if the line is referring to the same
 *          trade the line before or is a new trade information</li>
 *      <li>And communicates the set of lines to listeners.</li>
 *  </ul>
 */
public interface SpreadTradesMgr {
    /**
     * Memorizes the result and consider if "end of accumulation" event
     * should be notified.
     */
    public void accumulate(final ParseResult result);

    /**
     * Ends forced accumulation process and notifies to listeners.
     */
    public void endAccumulation();

    /**
     * Subscribes a listener to "end of accumulation" events.
     */
    public void addListener(final Listener listener);


    /**
     * Empties internal memory.
     */
    public void reset();
}
