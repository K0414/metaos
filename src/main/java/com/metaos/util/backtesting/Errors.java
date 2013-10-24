/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.util.backtesting;

import java.util.*;
import com.metaos.datamgt.*;
import com.metaos.util.*;

/**
 * Collects approximation error information from predictos.
 */
public class Errors<T> {
    private Map<T,List<Double>> errors;


    /**
     * Creates a new empty errors management object.
     */
    public Errors() {
        this.errors = new HashMap<T, List<Double>>();
    }


    /**
     * Empties memory.
     */
    public void reset() {
        this.errors.clear();
    }


    /**
     * Adds an error notification associated to the prediction at a moment.
     */
    public void addError(final T moment, final double error) {
        List<Double> es = this.errors.get(moment);
        if(es==null) {
            es = new ArrayList<Double>();
        }
        es.add(error);
        this.errors.put(moment, es);
    }


    /**
     * Adds a set of errors notification associated to the prediction 
     * at a moment.
     */
    public void addErrors(final T moment, final double[] errors) {
        List<Double> es = this.errors.get(moment);
        if(es==null) {
            es = new ArrayList<Double>();
        }
        for(final double e : errors) es.add(e);
        this.errors.put(moment, es);
    }



    /**
     * Reports memorized erros associated to a moment.
     */
    public void report(final T moment, final Statistics statistics) {
        List<Double> es = this.errors.get(moment);
        if(es!=null) {
            for(final double e : es) {
                statistics.addValue(e);
            }
        }
    }


    /**
     * Reports all memorized erros associated to any moment.
     */
    public void report(final Statistics statistics) {
        for(final List<Double> es : this.errors.values()) {
            for(final double e : es) {
                statistics.addValue(e);
            }
        }
    }


    /**
     * Gets index set for vectors of errors.
     */
    public Set<T> indexes() {
        return this.errors.keySet();
    }


    /**
     * Gets stored errors for given moment.
     */
    public List<Double> getErrors(final T moment) {
        return this.errors.get(moment);
    }
}
