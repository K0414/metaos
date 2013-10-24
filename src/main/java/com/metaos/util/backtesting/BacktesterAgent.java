/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.util.backtesting;

import java.text.*;
import java.util.*;
import java.util.logging.*;
import com.metaos.datamgt.*;
import com.metaos.util.*;
import com.metaos.signalgrt.predictors.*;

/**
 * Agent to perform a backtesting for a predictor.
 *
 * Usage: add as listener to SpreadTradesManager. Be careful to avoid:
 *  <ol>
 *    <li>add the production listeners to SpreadTradesManager attached
 *          directly to the LineScanner</li>
 *  </ol>
 *
 * To create this class, two different type of events are considered:
 * <ul>
 *      <li><i>present events</i></li>
 *      <li><i>future events</i></li>
 * </ul>
 * The source is asked for more elements, getting to events. These events
 * are taken thanks to <i>notify</i> callback function.<br/>
 * Events are, by default, <i>future events</i>. <i>Future events</i> are
 * memorized by <code>VolumeProfileBacktester</code>. <br/>
 * When several <i>future events</i> have been memorized (the mean of 'several'
 * depends on the authority of <code>ForecastingTime</code> object)
 * they are used to test the predictors and then notified to subscribed 
 * listener as <i>present events</i>.
 *
 */
public class BacktesterAgent implements Listener {
    private static final Logger log = Logger.getLogger(BacktesterAgent.class
            .getPackage().getName());
    private final PredictorListener predictor;
    private final ForecastingTime forecastingTime;
    private final ForecastingTest forecastingTest;
    private final List<Listener> listeners;
    private final List<ParseResult> futureParseResults;
    private double[] lastForecast;
    private int numberOfForecastings;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a backtesting for volume.
     */
    public BacktesterAgent(
            final PredictorListener predictor, 
            final ForecastingTime forecastingTime,
            final ForecastingTest forecastingTest) {
        this.listeners = new ArrayList<Listener>();
        this.futureParseResults = new ArrayList<ParseResult>();

        this.predictor = predictor;
        this.forecastingTest = forecastingTest;
        this.forecastingTime = forecastingTime;

        this.addListener(forecastingTest);
        this.addListener(predictor);
    }


    /**
     * Adds listener.
     */
    private void addListener(final Listener listener) {
        this.listeners.add(listener);
        log.info("Adding listener : " + listener);
    }


    /**
     * Receives notification signals as time goes on.
     */
    public void notify(final ParseResult parseResult) {
        final Calendar currentDay = parseResult.getLocalTimestamp(0);

        // Is it the moment to test previous forecast?
        if(this.forecastingTime.shouldOnlyEvaluatePrediction(currentDay)) {
            if(this.lastForecast != null) {
                log.finest("Evaluating previous prediction "
                    + dateFormatter.format(currentDay.getTime()));
                this.forecastingTest.evaluate(currentDay, this.lastForecast);
                this.numberOfForecastings++;
            }
        }

        // Is it the moment to generate a new prediction?
        if(this.forecastingTime.shouldOnlyPredict(currentDay)) {
            log.finest("Asking for new forecast " 
                    + dateFormatter.format(currentDay.getTime()));
            this.lastForecast = (double[]) this.predictor
                    .predictVector(currentDay).clone();
        }


        // Is it the moment to predict and then test prediction?
        // (a little fake...)
        if(this.forecastingTime.shouldPredictAndEvaluate(currentDay)) {
            log.finest("Asking for new forecast " 
                    + dateFormatter.format(currentDay.getTime()));
            this.lastForecast = (double[]) this.predictor
                    .predictVector(currentDay).clone();
            log.finest("Evaluating recent prediction "
                    + dateFormatter.format(currentDay.getTime()));
            this.forecastingTest.evaluate(currentDay, this.lastForecast);
            this.numberOfForecastings++;
        }

        // Is it the moment to test previous prediction and calculate new one?
        if(this.forecastingTime.shouldEvaluatePreviousPredictionAndPredict(
                    currentDay)) {
            if(this.lastForecast != null) {
                log.finest("Evaluating previous prediction "
                    + dateFormatter.format(currentDay.getTime()));
                this.forecastingTest.evaluate(currentDay, this.lastForecast);
                this.numberOfForecastings++;
            }
            log.finest("Asking for new forecast " 
                    + dateFormatter.format(currentDay.getTime()));
            this.lastForecast = (double[]) this.predictor
                    .predictVector(currentDay).clone();
        }

        // Notifies (maybe to predictors among others) event.
        for(final Listener l : this.listeners) l.notify(parseResult);
    }


    /**
     * Gets the number of realized forecastings.
     */
    public int numberOfTests() {
        return this.numberOfForecastings;
    }
}
