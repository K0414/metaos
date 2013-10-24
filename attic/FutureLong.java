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
package com.luisfcanals.deriva.options;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.luisfcanals.deriva.Product;
import com.luisfcanals.deriva.market.Market;

/**
 * Future Long: in a fixed time, underlying should be delivered.
 */
public class FutureLong extends Spot {
    final Calendar paymentDate;

    public FutureLong(final Calendar paymentDate, final String underlying, 
            final int size) {
        super(0.0, underlying, size);
        this.paymentDate = paymentDate;
    }

    public double getPrice(final Calendar date) {
        if(date.before(this.paymentDate)) {
            final double price = super.market.getPrice(date, underlying);
            return -price * size;
        } else return 0;
    } 
}
