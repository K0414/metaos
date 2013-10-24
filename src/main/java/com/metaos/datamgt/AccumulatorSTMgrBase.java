/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

import java.util.*;
import com.metaos.datamgt.Field.*;

/**
 * Base class to be inherited by acumulators of sequential trades.
 */
public abstract class AccumulatorSTMgrBase implements SpreadTradesMgr {
    protected final List<Listener> listeners;
    protected final List<ParseResult> memory;

    /**
     * Creates accumulator to discretize time in bands of given size in minutes.
     * @param minutesBandSize size of window in minutes. 
     */
    public AccumulatorSTMgrBase() {
        this.listeners = new ArrayList<Listener>();
        this.memory = new ArrayList<ParseResult>();
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
        final ParseResult accumResult = new ParseResult();
   
        for(final ParseResult result : memory) {
            for(final String symbol : result.getSymbols()) {
                if(accumResult.values(symbol)==null) {
                    accumResult.addSymbol(symbol);
                }

                for(final Map.Entry<Field,Double> val : 
                        result.values(symbol).entrySet()) {
                    if(val.getKey() instanceof LOW) {
                        final Double d = accumResult.values(symbol)
                                .get(val.getKey());
                        if(d!=null && d.doubleValue()>val.getValue()) {
                            accumResult.putValue(symbol,val.getKey(), 
                                    val.getValue());
                        }
                    } else if(val.getKey() instanceof HIGH) {
                        final Double d = accumResult.values(symbol)
                                .get(val.getKey());
                        if(d!=null && d.doubleValue()<val.getValue()) {
                            accumResult.putValue(symbol,val.getKey(), 
                                    val.getValue());
                        }
                    } else if(val.getKey() instanceof OPEN) {
                        if(accumResult.values(symbol).get(val.getKey())==null) {
                            accumResult.putValue(symbol,val.getKey(), 
                                    val.getValue());
                        }
                    } else if(val.getKey() instanceof CLOSE) {
                        accumResult.putValue(symbol,val.getKey(),
                                val.getValue());
                    } else if(val.getKey() instanceof VOLUME) {
                        final Double d = accumResult.values(symbol)
                                .get(val.getKey());
                        if(d!=null) {
                            accumResult.putValue(symbol,val.getKey(), 
                                    val.getValue() + d.doubleValue());
                        } else {
                            accumResult.putValue(symbol,val.getKey(), 
                                    val.getValue());
                        }
                    } else {
                        // Don't know what to do...
                        accumResult.putValue(symbol, val.getKey(), 
                                val.getValue());
                    }
                }
            }
        }


        accumResult.newTimestamp(0);
        if(memory.size()>0) {
            final Calendar lastMemoryTime = memory.get(memory.size()-1)
                    .getLocalTimestamp(0);

            accumResult.getLocalTimestamp(0).setTimeInMillis(
                    lastMemoryTime.getTimeInMillis());
            accumResult.getLocalTimestamp(0).setTimeZone(
                    lastMemoryTime.getTimeZone());
        
            for(final Listener listener : this.listeners) {
                listener.notify(accumResult);
            }
        }
        this.memory.clear();
    }


    /**
     * Subscribes a listener to "end of accumulation" events.
     */
    public void addListener(final Listener listener) {
        this.listeners.add(listener);
    }
}
