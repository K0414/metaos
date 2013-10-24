/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.spot;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.metaos.market.*;

/**
 * Equity.
 */
public class Equity extends Spot {
    public double getPrice(final Calendar date) {
        throw new UnsupportedOperationException();
    }

    public double getAcquisitionPrice() {
        throw new UnsupportedOperationException();
    }

    public double getAcquisitionCosts() {
        throw new UnsupportedOperationException();
    }

    public double getReleaseCosts(final Calendar date) {
        throw new UnsupportedOperationException();
    }

}

