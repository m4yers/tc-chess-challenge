package com.blah.app.solvers;

import java.util.LinkedList;
import java.io.Writer;

import com.blah.app.primitives.*;

public class Settings {

    public final boolean debug;
    public final boolean printToScreen;
    public final Writer printToFile;
    public final int poolSize;
    public final LinkedList<Board> result;

    private Settings(Builder builder) {
        this.debug = builder.debug;
        this.printToScreen = builder.printToScreen;
        this.printToFile = builder.printToFile;
        this.poolSize = builder.poolSize;
        this.result = builder.result;
    }

    public static class Builder {
        private boolean debug;
        private boolean printToScreen;
        private Writer printToFile;
        private int poolSize;
        private LinkedList<Board> result;

        public Builder debug(boolean value) {
            this.debug = value;
            return this;
        }

        public Builder printToScreen(boolean value) {
            this.printToScreen = value;
            return this;
        }

        public Builder printToFile(Writer value) {
            this.printToFile = value;
            return this;
        }

        public Builder poolSize(int value) {
            this.poolSize = value;
            return this;
        }

        public Builder result(LinkedList<Board> out) {
            this.result = out;
            return this;
        }

        public Settings build() {
            return new Settings(this);
        }
    }
}
