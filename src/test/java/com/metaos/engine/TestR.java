/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.engine;

import java.io.*;
import java.util.*;
import org.rosuda.JRI.*;
import org.python.core.PyList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Testing case for R engine adaptor.
 */
public class TestR {
    @Test
    public void testVectorAssignation() throws Exception {
        final R r = new R(new String[0]);
        final double vector[] = new double[] {0.0,1.0,2.0,3.0,4.0,5.0};
        r.set("x", vector);

        assertEquals(r.evalDouble("x[1]"), vector[0], 0.0d);
        assertEquals(r.evalDouble("x[2]"), vector[1], 0.0d);
        assertEquals(r.evalDouble("x[3]"), vector[2], 0.0d);
        assertEquals(r.evalDouble("x[4]"), vector[3], 0.0d);

        r.end();
    }


    public static void main(final String args[]) throws Exception {
        final TestR testR = new TestR();
        testR.testVectorAssignation();
        System.out.println("ok");
    }
}
