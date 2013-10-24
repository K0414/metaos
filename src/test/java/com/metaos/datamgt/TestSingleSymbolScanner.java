/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.datamgt;

import java.io.*;
import java.text.*;
import java.util.*;
import org.junit.Test;
import com.metaos.util.*;
import static org.junit.Assert.assertEquals;

/**
 * Testing case for Single Symbol scanner.
 */
public class TestSingleSymbolScanner {
    // Utility class for tests.
    private class LineMemory implements Listener {
        private List<ParseResult> results = new ArrayList<ParseResult>();

        public void notify(final ParseResult result) {
            results.add((ParseResult) (result.clone()));
        }
    }

    /**
     * Tests if read lines without information on time zone are set to GMT,
     * thus, timezone is not set to default locale.
     */
    @Test
    public void testNoTimeZone() throws Exception {
        final SpreadTradesMgr accumulator = new DiscreteMinutesSTMgr(5, false);
        final LineMemory lineMemory = new LineMemory();
        accumulator.addListener(lineMemory);
        final SingleSymbolScanner sss = new SingleSymbolScanner(
                "../../src/testdata/EURUSD1.csv", 
                "EURUSD", new NoSymbolCSVLineParser(
                    new Format[] { CalUtils.getDateFormat("yyyy.MM.dd"),
                                      CalUtils.getDateFormat("HH:mm"),
                                      new DecimalFormat("#.#####"), 
                                      new DecimalFormat("#.#####"), 
                                      new DecimalFormat("#.#####"), 
                                      new DecimalFormat("#.#####"),
                                      new DecimalFormat("#.#####") },
                    new Field[] { null, null, new Field.OPEN(), 
                                  new Field.HIGH(), new Field.LOW(), 
                                  new Field.CLOSE(), new Field.VOLUME()}, 
                    "EURUSD", new int[] {0,1}),
                new SpreadTradesMgr[] { accumulator });
        sss.run();
        assertEquals(lineMemory.results.get(0).getLocalTimestamp(0)
                .get(Calendar.HOUR_OF_DAY), 0);
        assertEquals(lineMemory.results.get(0).getLocalTimestamp(0)
                .get(Calendar.MINUTE), 0);
        assertEquals(lineMemory.results.get(0).getLocalTimestamp(0)
                .get(Calendar.SECOND), 0);
        System.out.println("Ok");
    }


    public static void main(final String args[]) throws Exception {
        final TestSingleSymbolScanner test = new TestSingleSymbolScanner();
        test.testNoTimeZone();
    }
}
