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

/**
 * Day positions: max, min, volume, open, close and adjusted close.
 */
public class DayPosition {
    public final double open, high, low, close, adjustedClose;
    public final int volume;

    public DayPosition(final double open, final double high, final double low,
            final double close, final int volume, final double adjustedClose) {
        this.open = open; 
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjustedClose = adjustedClose;
        this.volume = volume;
    }
}
