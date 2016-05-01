package com.blah.app;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class App {

    @Option(name = "-h", aliases = {"-help"}, usage = "Print help information")
    private boolean help;

    @Option(name = "-s", aliases = {"--solver"}, metaVar = "NAME", usage = "Choose what solver to use, avaialbe: bruteforce and caching")
    private String solver = "caching";

    @Option(name = "-p", aliases = {"--print"}, usage = "Print solved boards to screen")
    private boolean printToScreen;

    @Option(name = "-f", aliases = {"--print-to-file"}, metaVar = "FILE", usage = "Print solved boards to FILE")
    private boolean printToFile;

    @Option(name = "-ps", aliases = {"--pool-size"}, metaVar = "SIZE", usage = "Pool size for caching solver")
    private int poolSize = 500;

    @Option(name = "-d", aliases = {"--debug"}, usage = "Print debug information")
    private boolean debug;

    @Option(name = "-M", metaVar = "COLUMNS", usage = "Number of columns")
    private int M;

    @Option(name = "-N", metaVar = "ROWS", usage = "Number of rows")
    private int N;

    @Option(name = "-kings", metaVar = "NUMBER", usage = "Number of kings")
    private int kings;

    @Option(name = "-queens", metaVar = "NUMBER", usage = "Number of queens")
    private int queens;

    @Option(name = "-bishops", metaVar = "NUMBER", usage = "Number of bishops")
    private int bishops;

    @Option(name = "-rooks", metaVar = "NUMBER", usage = "Number of rooks")
    private int rooks;

    @Option(name = "-knights", metaVar = "NUMBER", usage = "Number of knights")
    private int knights;

    @Argument
    private List<String> arguments = new ArrayList<String>();

    public void doStuff( String[] args ) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch ( CmdLineException e ) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
            return;
        }

        if (help) {
            parser.printUsage(System.out);
            return;
        }

        if (this.M <= 0 || this.N <= 0) {
            System.err.println("You must provide correct number rows and columns");
            parser.printUsage(System.err);
            return;
        }

        if (this.kings == 0 && this.queens == 0 && this.bishops == 0 && this.rooks == 0 && this.knights == 0) {
            System.err.println("You must specify at least on figure to place");
            parser.printUsage(System.err);
            return;
        }

        if (this.solver != "bruteforce" && this.solver != "caching") {
            System.err.println("Unknown solver");
            parser.printUsage(System.err);
            return;
        }

        HashMap<Piece, Integer> freq = new HashMap<>();
        freq.put(Piece.getKing(),   this.kings);
        freq.put(Piece.getQueen(),  this.queens);
        freq.put(Piece.getBishop(), this.bishops);
        freq.put(Piece.getRook(),   this.rooks);
        freq.put(Piece.getKnight(), this.knights);

        Solver.Settings settings = new Solver.Settings(this.debug, this.printToScreen, null, null);

        Solver solver = null;;

        switch (this.solver) {
        case "caching": {
            solver = new CachingSolver(this.M, this.N, freq, settings, this.poolSize);
            break;
        }
        case "bruteforce": {
            solver = new CachingSolver(this.M, this.N, freq, settings, this.poolSize);
            break;
        }
        }

        long time = System.nanoTime();
        solver.solve();
        time = System.nanoTime() - time;
        System.out.println("Total: " + solver.totalBoards() + ", Time: " + (time / 1000000) + "ms");
    }

    public static void main( String[] args ) {
        System.out.println( "Hello Blah!" );
        new App().doStuff(args);
    }
}
