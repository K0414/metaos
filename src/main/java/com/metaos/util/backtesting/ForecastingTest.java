/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.util.*;
import com.metaos.datamgt.*;
import com.metaos.signalgrt.predictors.*;

/**
 * Forecasting tester for backtesting.
 */
public interface ForecastingTest extends Listener {
    /**
     * Evaluates forecasting.
     */
    public void evaluate(final Calendar when, final double[] prediction);
}
