/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.engine;

import java.io.*;
import java.util.*;
import org.python.core.*;
import org.python.util.*;

public class Engine {

    private final PythonInterpreter interpreter;

    public Engine() throws IOException {
        this(null);
    }

    public Engine(final String pyFile) throws IOException {
        // TODO: execfile, please...
        if(pyFile!=null) {
            final FileReader reader = new FileReader(pyFile);
            final char[] pyBuffer = new char[(int) new File(pyFile).length()];
            reader.read(pyBuffer);
            final String pyCode = new String(pyBuffer);

            this.interpreter = new PythonInterpreter();
            this.interpreter.exec(pyCode);
        } else {
            this.interpreter = new PythonInterpreter();
        }

        final R interpreteR = new R(System.getProperty("RCONSOLE")!=null);
        Engine.setR(interpreteR);
        this.interpreter.set("interpreteR", interpreteR);
        System.out.println("Engine started up");
    }


    /**
     * Executes Python file with given arguments passed as "args" array to
     * Python script.
     *
     * @param pyFile
     * @param args arguments to python file
     * @return "ok" ig everything goes fine
     * @throws IOException 
     */
    public String execute(final String pyFile, final String args[]) 
            throws IOException {
        this.interpreter.set("args", args);
        try {
            final FileReader reader = new FileReader(pyFile);
            final char[] pyBuffer = new char[(int) new File(pyFile).length()];
            reader.read(pyBuffer);
            final String pyCode = new String(pyBuffer);
            this.interpreter.exec(pyCode);
            return "ok";
        } catch(Exception e) {
            e.printStackTrace();
            return "failed!";
        } finally {     
            interpreteR.end();
        }
    }

    

    private static R interpreteR;
    private static void setR(final R r) { interpreteR = r; }

    /**
     * Gets the interpreter for R scripts assigned to the current Classloader
     * instance.
     */
    public static R getR() { return interpreteR; }



    /**
     * Entry point.
     */
    public static void main(final String args[]) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        final String[] argsRest = new String[args.length-2];
        for(int i=0; i<argsRest.length; i++) argsRest[i] = args[i+2];
        final Engine engine = new Engine(args[0]);
        engine.execute(args[1], argsRest);
    }
}
