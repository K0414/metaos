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
package com.luisfcanals.deriva.spot;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.luisfcanals.deriva.Product;
import com.luisfcanals.deriva.market.Market;

/**
 * Spot base for products.
 */
public abstract class Spot implements Product {
    protected final String underlying;
    protected final double acquisitionPrice;
    protected final int size;

    protected Market market;

    public Spot(final double acquisitionPrice, final String underlying, 
            final int size) {
        this.underlying = underlying;
        this.acquisitionPrice = acquisitionPrice;
        this.size = size;
    }

    public void setMarket(final Market m) {
        this.market = m;
    }

    public double getAcquisitionPrice() {
        return this.acquisitionPrice;
    }

    public double getReleasePrice(final Calendar cal) {
        throw new NoSuchMethodError("Not implemented yet");
    }
}
