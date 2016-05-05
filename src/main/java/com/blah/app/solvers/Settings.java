package com.blah.app.solvers;

import java.util.LinkedList;
import java.io.Writer;

import com.blah.app.primitives.*;

public class Settings {

    public boolean debug;
    public boolean printToScreen;
    public Writer out;
    public LinkedList<Board> results;

    public Settings(boolean debug, boolean printToScreen, Writer out, LinkedList<Board> results) {
        this.debug = debug;
        this.printToScreen = printToScreen;
        this.out = out;
        this.results = results;
    }
}
