/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.market;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Tactic enumeration
 */
public enum Tactic {
    Long(true), Short(false);

    private final boolean isLong;
    Tactic(final boolean isLong) {
        this.isLong = isLong;
    }

    public boolean isLong() {
        return this.isLong;
    }
}
