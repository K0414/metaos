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
 * Maps of historical stock data from Yahoo.
 */
public class YahooHistorical implements Historical {
    private static final Logger log = Logger.getLogger(YahooHistorical.class
            .getPackage().getClass().getName());

    private final Map<String, Map<Calendar, DayPosition>> data;
    private final int yearFrom;
    private static final SimpleDateFormat yahooDate 
            = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * @param yearFrom minimum year to get data (to avoid overload).
     */
    public YahooHistorical(final int yearFrom) {
        this.yearFrom = yearFrom;
        this.data = new HashMap<String, Map<Calendar, DayPosition>>();
    }


    /**
     * @param day should be normalized to 12:0:0.0 PM.
     */
    public DayPosition getPosition(final String underlying, 
            final Calendar day) {
        try {
            Map<Calendar, DayPosition> history = data.get(underlying);
            if(history==null) {
                history = new HashMap<Calendar, DayPosition>();
                final SimpleHttpGet getter = new SimpleHttpGet(
                        "http://ichart.finance.yahoo.com/table.csv?s="
                        + underlying + "&d=30&e=12&f=3010&g=d&a=0&b=1&c="
                        + yearFrom + "&ignore=.csv");
                getter.refresh();
                final String lines[] = getter.toString().split("\\n");
                for(int i=1; i<lines.length; i++) {
                    final String parts[] = lines[i].split(",");
                    final Calendar d = Calendar.getInstance();
                    d.setTime(yahooDate.parse(parts[0]));
                    CalUtils.normalizeCalendar(d);
                    final DayPosition p = new DayPosition(
                            Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5]),
                            Double.parseDouble(parts[6]));
                    history.put(d, p);
                }
                data.put(underlying, history);
            }
            return history.get(day);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
