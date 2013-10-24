/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.deriva;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.metaos.Instrument;

/**
 * Derivative product type.
 */
public interface Derivative extends Instrument {
    public Instrument getUnderlying();
    public double getDelta(final Calendar when);
    public double getVega(final Calendar when);
    public double getTheta(final Calendar when);
    public double getRho(final Calendar when);
    public double getGamma(final Calendar when);
    public double getSpeed(final Calendar when);
}
