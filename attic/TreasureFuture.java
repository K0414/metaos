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
 * Treasure Future : in a fixed time, underlying bond should be delivered.
 */
public class TreasureFuture extends Future {
    final Calendar maturityTime;
    final double riskFreeRate;
    final TreasureBond bond;
    final double DF;

    /**
     * @param riskFreeRate discrete yearly risk free rate.
     *  Usually the same as bond yield to maturity (YTM)
     */
    public TreasureFuture(final Calendar futureMaturityTime, 
            final TreasureBond underlying, final double riskFreeRate) {
        this.maturityTime = futureMaturityTime;
        this.riskFreeRate = riskFreeRate;
        this.bond = underlying;

        this.DF = this.bond.discountFactor(0.06);
    }

    public double getPrice(final Calendar date) {
        if(date.before(this.paymentDate)) {
            final double continuousRf = Math.log(1 + this.riskFreeRate);
            final double price1 =
                    this.bond.getPrice(date) * Math.exp(continuousRf * T);
            return price1 / DF;
        } else return 0;
    } 
}
