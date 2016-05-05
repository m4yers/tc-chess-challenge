package com.blah.app.utils;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }

    public A getFirst() {
        return this.first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return this.second;
    }

    public void setSecond(B second) {
        this.second = second;
    }
}
