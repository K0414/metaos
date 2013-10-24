/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

import java.util.*;
import com.metaos.datamgt.Field.*;

/**
 * Acumulates sequential trades to create N-minutes trades.
 * Typical usages are Open-Close-Hifg-Low-TotalVolume information for
 * a block of minutes (usuarlly 1,5,10,30,60 minutes).
 *
 * As an example, in this situation:
 * <pre>
 *   10:00 line 1 of data
 *   10:01 line 2 of data
 *   10:02 line 3 of data
 *   10:03 line 4 of data
 *   10:04 line 5 of data
 *   10:05 line 6 of data
 *   10:08 line 7 of data
 *   10:09 line 8 of data
 *   10:11 line 9 of data
 *   10:12 line 10 of data
 *   10:13 line 1 of data
 * </pre>
 * this accumulator tunned to create five minutes blocks, will emit two 
 * signals, one for trades of lines 1 to 5 and another one for lines 6,7,8,9,10.
 * The first one covers from 10:00 to 10:04 and the second one from 10:05 
 * to 10:12.
 * <br/>
 * If you want to cover equaly sized bands, use 
 * <code>DiscreteMinutesSTMgr</code>.
 */
public class BlocksOfMinutesSTMgr extends AccumulatorSTMgrBase {
    private long lastTimestamp;
    private final long accumulationWindow;

    /**
     * Creates accumulator of windows of given size in minutes.
     * @param minutesWindowSize size of window in minutes. 
     */
    public BlocksOfMinutesSTMgr(final int minutesWindowSize) {
        this.lastTimestamp = -1;
        this.accumulationWindow = minutesWindowSize * 60 * 1000;
    }

    /**
     * Memorizes the result and consider if "end of accumulation" event
     * should be notified.
     */
    public void accumulate(final ParseResult result) {
        if(this.lastTimestamp == -1) {
            this.lastTimestamp = result.getUTCTimestampCopy().getTimeInMillis();
        }

        if(result.getUTCTimestampCopy().getTimeInMillis()-this.lastTimestamp 
                >= this.accumulationWindow) {
            this.endAccumulation();
            this.lastTimestamp = result.getUTCTimestampCopy().getTimeInMillis();
        }
        this.memory.add(result);
    }
}
