/*
 * Copyright Luis F. Canals luisf.canals@gmail.com , 2010 - 2011
 * Reservados todos los derechos.
 * Este documento es material confidencial propiedad de 
 * Luis F. Canals luisf.canals@gmail.com 
 * Se prohibe la divulgación o revelación de su contenido
 * sin el permiso previo y por escrito del propietario.
 *
 * Copyright Luis F. Canals luisf.canals@gmail.com , 2010 - 2011
 * All rights reserved.
 * This document consists of confidential information property of 
 * Luis F. Canals luisf.canals@gmail.com
 * Its content may not be used or disclosed without
 * prior written permission of the owner.
 */
package com.luisfcanals.techan.maths;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

import org.apache.commons.math.stat.descriptive.moment.*;

/**
 * Volatility interface.
 */
public interface Volatility {
    /**
     * Gets volatility for given values.
     */
    public double getVolatility();

    /**
     * Gets the list of values.
     */
    public double[] getValues();
}
