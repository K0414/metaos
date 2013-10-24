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
package com.luisfcanals.deriva.bond;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.luisfcanals.deriva.Product;
import com.luisfcanals.deriva.market.Market;

/**
 * USA Treasure Bonds.
 */
public class TreasureBond {
    final int yearlyPayments;
    final double yieldToMaturity;
    final int yearsToMaturity;
    final double couponRate;
    final double parValue;

    public TreasureBond(final double parValue, final double couponRate, 
                final int yearsToMaturity, final double yieldToMaturity,
                final Calendar startingDate) {
        this.yearsToMaturity = yearsToMaturity;
        this.yearlyPayments = 2;
        this.yieldToMaturity = yieldToMaturity;
        this.couponRate = couponRate;
        this.parValue = parValue;
        this.startingDate = startingDate;
    }

    /**
     * Current coupon price = S0 - PresentValue(PayedCoupons).
     *
     * For instance for a six monthly payed coupon:
     *      S0 = PV * (sum(CR / (1+r/2)^i, i=1..2N) + CR / (1+r/2)^2N)
     *
     *      where CR = coupon rate, PV = par value
     *
     *      PresentValue(PayedCoupons) = coupon * sum(CR / (1+r/2)^i, 
     *                                              i=now...2N)
     */
    public double getPrice(final Calendar date) {
        double price0 = 0;
        for(int i=1; i<=2*yearsToMaturity; i++) {
            price += couponRate / 
                    Math.pow(1 + (yieldToMaturity/2), i);
        }
        price0 += (100 + couponRate) / 
                    Math.pow(1 + (yieldToMaturity/2), 2*yearsToMaturity);

        // half years left to maturity
        final int hyltm = 
            (int) (CalUtils.differenceInYears(date, this.startingDate) * 2);
        double interests = 0;
        for(int i=1; i<=hyltm; i++) {
            interests += couponRate / 
                    Math.pow(1 + (yieldToMaturity/2), i);
        }

        return (price0 - interests) * this.parValue;
    }


    /**
     * Calculate Discount Factor usually for futures.
     * 
     * @param rate yearly rate to compare, usually CBOT, that is 0.06.
     */
    public double discountFactor(final double rate) {
        double df = 0;
        for(int i=0; i<2*yearsToMaturity; i++) {
            df += (couponRate/2) / Math.pow(1 + rate/2, i);
        }
        df += (1+couponRate/2) / Math.pow(1, + rate/2, 2*yearsToMaturity);
        return df;
    }
}
