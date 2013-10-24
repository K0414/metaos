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
 * Zero coupon bond.
 */
public class ZeroCouponBond extends Bond {
    /**
     * Creates a zero-coupon bond.
     * @param initTime when the bond is bought.
     * @param expiryTime when the bond is finished (null for non expiration
     *      times).
     * @param paymentPeriods number of periods to be considered for risk
     *      free rate.
     */
    public ZeroCouponBond(final Calendar initTime, 
            final Calendar expiryTime, final double maturityPrice, 
            final double periodicRiskFreeRate, final int ratePeriod) {
        super();
    }

    public double getPrice(final Calendar date) {
/*
        if(date.after(expiryTime)) {
            return 0;
        } else {
            double T =

            if(this.ratePeriod==0) {
                // Price = M * exp{ -r*T }
                return maturityPrice / Math.exp(periodicRiskFreeRate * T);
            } else {
                // Price = M / (1+r)^T = PresentValue(M)
                return maturityPrice / Math.pow(1+periodicRiskFreeRate, T);
            }
        }
*/
        throw new RuntimeException("NOT IMPLEMENTED");
    }    
}
