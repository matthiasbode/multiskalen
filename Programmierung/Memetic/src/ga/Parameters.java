/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
 *
 * @author bode
 */
public class Parameters {

    public static int maxStagnation = 3;
    public static int NUMBER_OF_THREADS = 8;
    private static long seed = 14484118L;//System.currentTimeMillis(); //58L;//
    public static DecimalFormat doubleFormat = new DecimalFormat("#0.00000", new DecimalFormatSymbols(Locale.US));
    public static final Logger logger = Logger.getLogger("");
    private static ExecutorService threadPool;

    public static int NUMBER_OF_RECOMBINATION_THREADS = 2;

    private static final ThreadLocal<Random> randomPerThread = new ThreadLocal<Random>() {

        @Override
        protected Random initialValue() {
            return new Random(seed);
        }
    };

    public static Random getRandom() {
        return randomPerThread.get();
    }

    public static ExecutorService getThreadPool() {
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = Executors.newCachedThreadPool(); //newFixedThreadPool(Parameters.NUMBER_OF_THREADS);
        }
        return threadPool;
    }

    public static void initializeLogger(Level l) {
        logger.setLevel(l);
        logger.getHandlers()[0].setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        logger.getHandlers()[0].setLevel(l);
    }
}
