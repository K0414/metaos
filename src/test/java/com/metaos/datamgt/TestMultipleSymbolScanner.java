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
import static org.junit.Assert.assertEquals;

/**
 * Testing case for Multiple Symbol scanner.
 */
public class TestMultipleSymbolScanner {
    // Utility class for tests.
    private class LineMemory implements Listener {
        private List<ParseResult> results = new ArrayList<ParseResult>();

        public void notify(final ParseResult result) {
            results.add((ParseResult) (result.clone()));
        }
    }

    @Test
    public void testABEandSABE() throws Exception {
        final SpreadTradesMgr accumulator = new TransparentSTMgr();
        final LineMemory lineMemory = new LineMemory();
        accumulator.addListener(lineMemory);
        final MultipleSymbolScanner mss = new MultipleSymbolScanner(
                "../../src/testdata/ABE+SABE.MC-test.csv", 
                new String[] { "ABE.MC", "SABE.MC" },
                new ReutersCSVLineParser(
                        "../../src/testdata/ABE+SABE.MC-test.csv"), 
                new SpreadTradesMgr[] { accumulator });
        mss.run();
        for(int i=0; i<lineMemory.results.size(); i++) {
            final Calendar t = lineMemory.results.get(i).getLocalTimestamp(0);
            if(t.get(Calendar.DAY_OF_MONTH)==28 && t.get(Calendar.MONTH)==9 
                    && t.get(Calendar.YEAR)==2011
                    && t.get(Calendar.HOUR_OF_DAY)==10
                    && t.get(Calendar.MINUTE)==44) {
                assertEquals(12.00, lineMemory.results.get(i).values(0).get(
                        new Field.OPEN()), 0);
                assertEquals(12.00, lineMemory.results.get(i).values(0).get(
                        new Field.HIGH()), 0);
                assertEquals(12.00, lineMemory.results.get(i).values(0).get(
                        new Field.LOW()), 0);
                assertEquals(4124.0, lineMemory.results.get(i).values(0).get(
                        new Field.VOLUME()), 0);
                assertEquals(2.00, lineMemory.results.get(i).values(1).get(
                        new Field.OPEN()), 0);
                assertEquals(2.00, lineMemory.results.get(i).values(1).get(
                        new Field.HIGH()), 0);
                assertEquals(2.00, lineMemory.results.get(i).values(1).get(
                        new Field.LOW()), 0);
                assertEquals(1894.0, lineMemory.results.get(i).values(1).get(
                        new Field.VOLUME()), 0);
            }
        }
        System.out.println("Ok");
    }



    @Test
    public void testIBEX27and28OCT() throws Exception {
        final SpreadTradesMgr accumulator = new TransparentSTMgr();
        final LineMemory lineMemory = new LineMemory();
        accumulator.addListener(lineMemory);
        final MultipleSymbolScanner mss = new MultipleSymbolScanner(
                "../../src/testdata/IBEX-27-OCT-2011.csv", 
                new String[] { "ABE.MC", "ABG.MC", "ACS.MC", "ACX.MC", "AMA.MC", "ANA.MC", "BBVA.MC", "BKIA.MC", "BKT.MC", "BME.MC", "CABK.MC", "EBRO.MC", "ELE.MC", "ENAG.MC", "FCC.MC", "FER.MC", "GAM.MC", "GAS.MC", "GRLS.MC", "IBE.MC", "ICAG.MC", "IDR.MC", "ITX.MC", "MAP.MC", "MTS.MC", "OHL.MC", "POP.MC", "REE.MC", "REP.MC", "SABE.MC", "SAN.MC", "SVO.MC", "TEF.MC", "TL5.MC", "TRE.MC" },
                new ReutersCSVLineParser(
                        "../../src/testdata/ABE+SABE.MC-test.csv"), 
                new SpreadTradesMgr[] { accumulator });
        mss.run();

        assertEquals("27-10-2011 16:49",new SimpleDateFormat("dd-MM-yyyy HH:mm")
            .format(lineMemory.results.get(800)
                    .getLocalTimestamp(0).getTime()));
        assertEquals(12.00, lineMemory.results.get(800).values(0)
                .get(new Field.OPEN()), 0);
        assertEquals(6.0, lineMemory.results.get(800).values(6)
                .get(new Field.HIGH()), 0);        
        assertEquals(1, lineMemory.results.get(lineMemory.results.size()-1)
                .getLocalTimestamp(0).get(Calendar.HOUR_OF_DAY));
        assertEquals(59, lineMemory.results.get(lineMemory.results.size()-1)
                .getLocalTimestamp(0).get(Calendar.MINUTE));
        System.out.println("Ok");
    }


    public static void main(final String args[]) throws Exception {
        final TestMultipleSymbolScanner test = new TestMultipleSymbolScanner();
        test.testABEandSABE();
        test.testIBEX27and28OCT();
    }
}
