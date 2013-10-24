/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

import java.util.*;

/**
 * Valid when trades are not spread across lines. It's the most trivial case.
 */
public class TransparentSTMgr implements SpreadTradesMgr {
    private final List<Listener> listeners;

    /**
     * Creates a zero capacity accumulator.
     */
    public TransparentSTMgr() {
        this.listeners = new ArrayList<Listener>();
    }

    /**
     * Memorizes the result and consider if "end of accumulation" event
     * should be notified.
     */
    public void accumulate(final ParseResult result) {
        for(final Listener listener : this.listeners) {
            listener.notify(result);
        }
    }

    /**
     * Resets accumulator.
     */
    public void reset() {
        this.listeners.clear();
    }

    /**
     * Ends forced accumulation process and notifies to listeners.
     */
    public void endAccumulation() {
    }

    /**
     * Subscribes a listener to "end of accumulation" events.
     */
    public void addListener(final Listener listener) {
        this.listeners.add(listener);
    }
}
