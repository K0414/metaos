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
 * Eurodollar Future.
 *
 * Referenced to 1M$ at LIBOR 3.
 */
public class EurodollarFuture extends Future {
    final Calendar maturityTime;
    final String underlying;

    double previousPrice;
    Calendar previousDate;

    /**
     * @param riskFreeRate discrete yearly risk free rate.
     *  Usually the same as bond yield to maturity (YTM)
     * @param futureMaturityTime third Wednesday of MAR,JUN,SEP,DEC of the year.
     * 
     */
    public EurodollarFuture(final Calendar buyingTime,
            final Calendar futureMaturityTime) {
        this.maturityTime = futureMaturityTime;
        this.underlying = "ED" + monthInitial + year;
        this.previousPrice = getPrice(buyingTime);
    }

    /**
     * Current price in USD.
     */
    public double getPrice(final Calendar date) {
        if(date.before(this.paymentDate)) {
            return 1000000 * (
                market.getPrice(date, this.underlying) * (90/360)) / 100;
        } else return 0;
    }

    
    /**
     * Liquidation price.
     */
    public double liquidationBallance(final Calendar date) {
        if(date.after(this.previousDate)) {
            final double difference = getPrice() - this.previousPrice;
            this.ballance += difference;
            this.previousPrice = getPrice();
            this.previousDate = date;
        }
        return this.ballance;
    }

    /**
     * Liquidate pending payments.
     */
    public void pay(final double payment) {
        this.ballance -= payment;
    }
}
