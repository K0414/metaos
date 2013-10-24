/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.engine;

/**
 * Exception for R Engine errors.
 */
public class REngineException extends RuntimeException {
    public REngineException(final String what) {
        super("R error evaluating " + what);
    }
}
