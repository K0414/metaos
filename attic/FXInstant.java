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
package com.luisfcanals.techan.data;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.luisfcanals.util.*;

/**
 * Gets inmediate FX data for most common forex.
 */
public class FXInstant {
    private static final Logger log = Logger.getLogger(FXInstant.class
            .getPackage().getClass().getName());
    
    final SimpleHttpGet getter;

    public FXInstant() {
        this.getter = new SimpleHttpGet("http://www.howthemarketworks.com/"
                + "includes/Trading/ajax_forex-quotes.php");
    }


    /**
     * Gets the ist of forex prices for this moment.
     */
    public Map<String, Double> getPositions() {
        try {
            this.getter.refresh();

            // It's 'coded'
            final String line = getter.toString();
            final Map<String, Double> forex = new HashMap<String, Double>();
            final StringBuffer result = new StringBuffer();
            for(int i=0; i<line.length(); i++) {
                short chr;
                if(line.charAt(i)==66) {
                    chr = 66;
                } else {
//                    chr = 32 + (char) ((126 - (short) line.charAt(i)));
                }
                result.append((char) chr);
            }
            System.out.println(result);
            return forex;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
