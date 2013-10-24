/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.pricer.options;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.metaos.deriva.options.*;

/**
 * Interface for options price calculator models.
 */
public interface PriceCalculator {
    public double calculate(final Option option, final double underlyingPrice,
            final Calendar when);
}
