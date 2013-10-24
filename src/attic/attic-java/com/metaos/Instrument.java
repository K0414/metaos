/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * All kind of products: basic and derivatives.
 */
public interface Instrument {
    public double getPrice(final Calendar date);
    public double getAcquisitionPrice();
    public double getAcquisitionCosts();
    public double getReleaseCosts(final Calendar date);
}
