/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.deriva.options.vanilla;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.metaos.deriva.options.*;
import com.metaos.pricer.options.PriceCalculator;
import com.metaos.*;

/**
 * Long Put implementation.
 */
public class EuropeanLongPut extends EuropeanPut {
    public EuropeanLongPut(final double prime, final double strikePrice,
            final Calendar strike,final Instrument underlying,final int size,
            final PriceCalculator calculator) {
        super(prime, strikePrice, strike, underlying, size, calculator);
    }

    public double getPrice(final Calendar when) {
        return super.getPrice(when);
    }

    protected double getPrice(final Calendar when, 
            final double underlyingPrice) {
        return super.getPrice(when, underlyingPrice);
    }

    public double getVega(final Calendar when) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
    }

    public double getRho(final Calendar when) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
    }

    public double getAcquisitionCosts() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
    }
}
