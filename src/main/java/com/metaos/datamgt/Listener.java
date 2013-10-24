/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.metaos.datamgt;

/**
 * Listener for new prices and data for one or several instruments.
 */
public interface Listener {
    /**
     * Receives notification signals.
     */
    public void notify(final ParseResult result);
}
