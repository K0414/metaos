/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.datamgt;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * Field from a source with actions to perform over a listener.
 */
public abstract class Field {
    /**
     * Qualifiers for a field.
     */
    public static enum Qualifier {
        BID, ASK, NONE;
    }


    protected final Qualifier qualifier;
    protected final String field;

    // Private to avoid external extensions.
    private Field(final Qualifier qualifier, final String field) {
        this.qualifier = qualifier;
        this.field = field;
    }

    /**
     * Notifies to market the value of this field.
     */
    public void notify(final CacheWriteable listener, final Calendar moment,
            final String symbol, final double val) {
        switch(qualifier) {
            case BID:
                listener.setBid(moment, this, symbol, val);
                break;
            case ASK:
                listener.setAsk(moment, this, symbol, val);
                break;
            case NONE:
                listener.set(moment, this, symbol, val);
                break;
        }
    }

    public String toString() {
        if(this.field==null) return "";
        if(this.qualifier!=Qualifier.NONE) {
            return this.field + " " + this.qualifier.toString();
        } else {
            return this.field;
        }
    }

    public int hashCode() {
        return this.qualifier.hashCode();
    }

    public boolean equals(final Object o) {
        final Field other = (Field) o;
        if(this.field==null) return other.qualifier==this.qualifier;
        else return other.qualifier==this.qualifier
                && this.field.equals(other.field);
    }


    public static final class NONE extends Field {
        public NONE(final Qualifier qualifier) { super(qualifier, null); }
    }

    public static final class OPEN extends Field {
        public OPEN(final Qualifier qualifier) { super(qualifier, "OPEN"); }
        public OPEN() { this(Qualifier.NONE); }
    }

    public static final class CLOSE extends Field {
        public CLOSE(final Qualifier qualifier) { super(qualifier, "CLOSE"); }
        public CLOSE() { this(Qualifier.NONE); }
    }

    public static final class HIGH extends Field {
        public HIGH(final Qualifier qualifier) { super(qualifier, "HIGH"); }
        public HIGH() { this(Qualifier.NONE); }
    }

    public static final class LOW extends Field {
        public LOW(final Qualifier qualifier) { super(qualifier, "LOW"); }
        public LOW() { this(Qualifier.NONE); }
    }

    public static final class VOLUME extends Field {
        public VOLUME(final Qualifier qualifier) { super(qualifier, "VOLUME"); }
        public VOLUME() { this(Qualifier.NONE); }
    }

    public static final class EXTENDED extends Field {
        public EXTENDED(final Qualifier qualifier, final String extension) {
            super(qualifier, extension);
        }
    }
}
