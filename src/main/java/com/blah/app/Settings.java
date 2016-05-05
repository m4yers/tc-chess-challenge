package com.blah.app;

import java.util.LinkedList;
import java.io.Writer;

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
