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
import com.metaos.deriva.options.vanilla.*;
import com.metaos.pricer.volatility.*;
import com.metaos.market.*;
import com.metaos.util.*;

/**
 * Explicit method black scholes for call and put options.
 */
public class ExplicitBlackScholesMerton implements PriceCalculator {
    private final VolatilityCalculator volatility;
    private final Market riskFreeRates;
    private final String riskFreeSymbol;


    /**
     * @param volCalc Volatility method for underlying instrument.
     * @param riskFreeRates market with risk free rates to consider.
     * @param riskFreeSymbol symbol to ask to market for risk free rates.
     */
    public ExplicitBlackScholesMerton(final VolatilityCalculator volCalc,
            final Market riskFreeRates, final String riskFreeSymbol) {
        this.volatility = volCalc;
        this.riskFreeRates = riskFreeRates;
        this.riskFreeSymbol = riskFreeSymbol;
    }


    /*
     * Implementation trends:
     *  - to avoid the intensive use of if/else if/else if ... sequences
     *    think about a pluggable strategy patterns: from the class name
     *    of the instrument, use a specific and name-related
     *    Black-Scholes-Merton class to price.
     */
    public double calculate(final Option option, final double underlyingPrice,
            final Calendar when) {
        final double r = this.riskFreeRates.getPrice(when, this.riskFreeSymbol);
        final double St = underlyingPrice;
        final double E = option.getStrikePrice();
        final double tau = CalUtils.differenceInYears(
                option.getStrike(), when);
        final double sqrtTau = Math.sqrt(tau);
        final double sigma = this.volatility.calculate(option.getUnderlying());
        final double sigma2 = sigma * sigma;
        final double d1 = (Math.log(St/E) + (r + sigma2/2)*tau)
                / (sigma*sqrtTau);
        final double d2 = d1 - sigma*sqrtTau;

        if(option instanceof EuropeanCall) {
            return St * N(d1) - E*Math.exp(-r*tau)*N(d2);
        } else if(option instanceof EuropeanPut) {
            return -St * N(-d1) + E*Math.exp(-r*tau)*N(-d2);
        } else {
            throw new IllegalArgumentException("Explicit Black-Scholes-Merton "
                    + "are only valid for European Options");
        }
    }


    // Private stuf ---------------------------------------

    private double N(final double z) {
        if(z > 6.0) return 1.0; 
        if(z < -6.0) return 0.0;
        final double b1 =  0.31938153;
        final double b2 = -0.356563782;
        final double b3 =  1.781477937;
        final double b4 = -1.821255978;
        final double b5 =  1.330274429;
        final double p  =  0.2316419;
        final double c2 =  0.3989423;
        final double a=Math.abs(z);
        final double t = 1.0/(1.0+a*p);
        final double b = c2*Math.exp((-z)*(z/2.0));
        double n = ((((b5*t+b4)*t+b3)*t+b2)*t+b1)*t;
        n = 1.0-b*n;
        if(z < 0.0) n = 1.0 - n;
        return n;
    }
}
