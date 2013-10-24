/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.engine;

import java.io.*;
import java.util.*;
import org.rosuda.JRI.*;
import org.python.core.PyList;

/**
 * Adaptor for JRI object.
 *
 * ATTENTION: Rengine has a very big <b>bug</b>:
 * <br/>
 * When trying to execute something like:<code><pre>
 *         rEngine.evalDouble("var(anObject$getMeAVector())");
 *     </pre></code>
 * you'll get a <code>null</code> from R. The workaround is to assign
 * desired vector a R variable and apply the external function over it:
 * <code><pre>
 *         rEngine.eval("x <- statistics$listAll()");
 *         rEngine.evalDouble("var(x)");
 * </pre></code>
 */
public class R {
    private final Rengine engine;
    private final boolean consoleActive;

    public R(final String rFiles[], final boolean consoleActive) 
            throws IOException {
        this.consoleActive = consoleActive;
        if(!Rengine.versionCheck()) {
            throw new RuntimeException("Mismatch R version");
        }
        this.engine = new Rengine(new String[] {"--vanilla"}, false, 
                new NullLoopCallbacks());
//        this.engine.setDaemon(true);
        if(!this.engine.waitForR()) {
            throw new RuntimeException("R cannot be loaded");
        }

        for(final String file : rFiles) {
            this.engine.eval("source(\"" + file + "\")");
        }
    }

    public R(final String rFiles[]) throws IOException {
        this(rFiles, false);
    }

    public R() throws IOException {
        this(new String[0], false);
    }

    public R(final boolean consoleActive) throws IOException {
        this(new String[0], consoleActive);
    }


    public R(final String rFile) throws IOException {
        this(new String[] {rFile}, false);
    }

     public R(final String rFile, final boolean consoleActive) 
            throws IOException {
        this(new String[] {rFile}, consoleActive);
    }



    /**
     * Evals R file.
     */
    public void evalFile(final String rFile) throws IOException {
        this.engine.eval("source(\"" + rFile + "\")");
    }


    /**
     * Finishes R execution.
     */
    public void end() {
        if(!this.consoleActive) this.engine.stop();
        else this.engine.startMainLoop();
    }


    /**
     * Lets the name of the variable containing given value.
     * According to object type, different kind of assignations will be made.
     */
    public void set(final String name, final Object value) {
        if(value instanceof boolean[]) {
            this.engine.assign(name, (boolean[]) value);
        } else if(value instanceof double[]) {
            this.engine.assign(name, (double[]) value);
        } else if(value instanceof int[]) {
            this.engine.assign(name, (int[]) value);
        } else if(value instanceof String) {
            final REXP x = this.engine.eval(value.toString());
            this.engine.assign(name, x);
        } else {
            System.err.println("Ignoring unknown type " + value.getClass()
                    + " to assign");
        }
    }
    
    /**
     * Lets the name of the variable containing list of numbers.
     * Specifically desinged to used from Python.
     */
    public void setDoubles(final String name, final PyList list) {
        final double[] values = new double[list.size()];
        for(int i = 0; i < values.length; i++) {
            values[i] = (Double) list.get(i);
        }
        this.engine.assign(name, values);
    }
 
    /**
     * Evals R expression.
     */
    public REXP eval(final String what) {
        return this.engine.eval(what);
    }


    /**
     * Evals R expression as a boolean expression.
     */
    public boolean evalBool(final String what) {
        final REXP x = this.engine.eval(what);
        if(x==null) throw new REngineException(what);
        return x.asBool().isTRUE();
    }


    /**
     * Evals R expression as a string expression.
     */
    public String evalString(final String what) {
        final REXP x = this.engine.eval(what);
        if(x==null) throw new REngineException(what);
        return x.asString();
    }


    /**
     * Evals R expression as a double expression.
     */
    public double evalDouble(final String what) {
        final REXP x = this.engine.eval(what);
        if(x==null) throw new REngineException(what);
        return x.asDouble();
    }


    /**
     * Evals R expression as a list of double values.
     */
    public double[] evalDoubleArray(final String what) {
        final REXP x = this.engine.eval(what);
        if(x==null) throw new REngineException(what);
        return x.asDoubleArray();
    }


    /**
     * Evals R expression as a matrix of double values.
     */
    public double[][] evalDoubleMatrix(final String what) {
        final REXP x = this.engine.eval(what);
        if(x==null) throw new REngineException(what);
        return x.asDoubleMatrix();
    }


    /**
     * Locks access to R to other threads.
     * Lock must be released in order to let other thread to execute
     * R functionallity.
     */
    public void lock() {
    }

    
    /**
     * Releases locked object for the current thread.
     */
    public void release() {
    }



    //
    // Private stuff ----------------------------------
    //

    /**
     * Null implementation for R callbacks.
     */
    private final class NullLoopCallbacks implements RMainLoopCallbacks {
        public void rWriteConsole(Rengine re, String text, int oType) {
            if(consoleActive) {
                System.err.print(text);
            }
        }
  
        public void rBusy(Rengine re, int which) {
        }
  
        public String rReadConsole(final Rengine re, final String prompt, 
                final int addToHistory) {
            if(consoleActive) {
                System.err.print(prompt);
                try {
                    final BufferedReader br = new BufferedReader(
                            new InputStreamReader(System.in));
                    final String s = br.readLine();
                    return (s == null || s.length() == 0) ? s : s + "\n";
                } catch (Exception e) {
                    System.err.println("jriReadConsole exception: " 
                        + e.getMessage());
                }
            }
            return null;
        }
  
        public void rShowMessage(Rengine re, String message) {
            if(consoleActive) {
                System.out.println("rShowMessage \"" + message + "\"");
            }
        }
  
        public String rChooseFile(Rengine re, int newFile) {
        /*
            FileDialog fd = new FileDialog(new Frame(),
            (newFile == 0) ? "Select a file" : "Select a new file",
            (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
            fd.setVisible(true);
            String res = null;
            if (fd.getDirectory() != null) res = fd.getDirectory();
            if (fd.getFile() != null) {
                res = (res == null) ? fd.getFile() : (res + fd.getFile());
            }
            return res;
        */
            return null;
        }
  
        public void rFlushConsole(final Rengine re) {
        }
       
        public void rLoadHistory(final Rengine re, final String filename) {
        }
                  
        public void rSaveHistory(final Rengine re, final String filename) {
        }
    }
}
