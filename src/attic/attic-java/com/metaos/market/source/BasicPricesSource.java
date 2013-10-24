/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.market.source;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

import com.metaos.market.*;

/**
 * Basic implementation of prices source.
 */
public abstract class BasicPricesSource implements PricesSource {
    //
    // Overriden methods as not functional operations ------------
    //

    public boolean next() {
        throw new UnsupportedOperationException();
    }

    public boolean last() {
        throw new UnsupportedOperationException();
    }

    public boolean first() {
        throw new UnsupportedOperationException();
    }

    public boolean search(final Calendar time) {
        throw new UnsupportedOperationException();
    }

    public boolean searchClosestBefore(final Calendar time) {
        throw new UnsupportedOperationException();
    }

    public boolean searchClosestAfter(final Calendar time) {
        throw new UnsupportedOperationException();
    }
}
