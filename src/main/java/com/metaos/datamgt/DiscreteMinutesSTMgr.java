/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

import java.util.*;
import com.metaos.datamgt.Field.*;

/**
 * Acumulates sequential trades to create a discrete vision of time, in bands
 * with a fixed size.
 *
 * Usages are typical Open-Close-Hifg-Low-TotalVolume information for
 * minute-bands (usuarlly 1,5,10,30,60 minutes).
 * <br>/
 * To keep coherence, size of band (in minutes) must divide total minutes
 * in a day (60*24).
 * <br>/
 * Optionally, data in each block can be "completed", when not every minute
 * contains trade data into a block, replicating the previous minute with data.
 * If missing data size is greater or equal to window size, the window will
 * not be considered. Time resolution is, remember, one minute.
 */
public class DiscreteMinutesSTMgr extends AccumulatorSTMgrBase {
    private long lastTimestamp;
    private final long accumulationWindow;
    private boolean autocomplete;

    /**
     * Creates accumulator to discretize time in bands of given size in minutes.
     * @param minutesBandSize size of window in minutes. 
     * @param autocomplete true to autocomplete missing trades cloning the 
     * previous known trade, false to let missing data as missing data.
     */
    public DiscreteMinutesSTMgr(final int minutesBandSize, 
            final boolean autocomplete) {
        this.lastTimestamp = -1;
        this.accumulationWindow = minutesBandSize;
        this.autocomplete = autocomplete;
    }

    /**
     * Memorizes the result and consider if "end of accumulation" event
     * should be notified.
     */
    public void accumulate(final ParseResult result) {
        if(this.lastTimestamp == -1) {
            this.lastTimestamp = result.getUTCTimestampCopy().getTimeInMillis();
        }

        
        if(this.autocomplete && 
            result.getUTCTimestampCopy().getTimeInMillis()-this.lastTimestamp
                        >= 2*(1000*60) && 
            result.getUTCTimestampCopy().getTimeInMillis()-this.lastTimestamp
                        <= this.accumulationWindow*(1000*60)) {
            long minsToLastData=(result.getUTCTimestampCopy().getTimeInMillis()
                    - this.lastTimestamp) / (1000*60);
            while(minsToLastData>=2) {
                final ParseResult missingResult = (ParseResult) result.clone();
                missingResult.getUTCTimestampCopy().add(Calendar.MINUTE, 
                        (int) (- minsToLastData + 1));
                this.accumulate(missingResult);
                minsToLastData--;
            }
        }

        if(result.getUTCTimestampCopy().getTimeInMillis()>this.lastTimestamp && 
                (result.getUTCTimestampCopy().getTimeInMillis() / (1000*60))
                        % this.accumulationWindow == 0) {
            this.endAccumulation();
        }
        this.lastTimestamp = result.getUTCTimestampCopy().getTimeInMillis();
        this.memory.add(result);
    }
}
