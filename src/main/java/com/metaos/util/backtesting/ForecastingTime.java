/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.util.*;
import com.metaos.datamgt.*;

/**
 * Strategy to decide the length in time needed to backtests.
 */
public interface ForecastingTime extends Listener {
    /**
     * Tests if it's moment to evaluate a previous prediction, usually at
     * the end of considered periods.
     */
    public boolean shouldOnlyEvaluatePrediction(final Calendar when);

    /**
     * Tests if it's moment to predict, usually at the begining of considered
     * periods.
     */
    public boolean shouldOnlyPredict(final Calendar when);

    /**
     * Tests if it's moment to evaluate a previous prediction and
     * then calculate a new one.
     */
    public boolean shouldEvaluatePreviousPredictionAndPredict(
		    final Calendar when);

    /**
     * Tests if it's moment to calculate a new prediction and evaluate it
     * (it's a little fictitious...)
     */
    public boolean shouldPredictAndEvaluate(final Calendar when);

}
