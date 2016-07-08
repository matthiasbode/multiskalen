/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment;

import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.FuzzyInterval;
import ga.Parameters;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import math.LongValue;
import org.apache.commons.io.output.TeeOutputStream;

/**
 *
 * @author bode
 */
public class TransshipmentParameter {

    public static enum FuzzyMode {

        crisp,
        fuzzy,
        fuzzyCapacity
    }

    public static int numberOfRoutes = 5;
    public static final Logger logger = Parameters.logger;
    public static Level analysisLevel = Level.INFO;
    public static boolean DEBUG = false;

    public static boolean USE_POLY_LCS = false;
    public static FuzzyMode fuzzyMode = FuzzyMode.crisp;
    public static boolean exactSetupTime = false;

    public static double defaultCapacityUncertainty = 0.1;
    public static long defaultUncertainty = 3*60* 1000;

    public static DecimalFormat doubleFormat = new DecimalFormat("#0.00000", new DecimalFormatSymbols(Locale.US));
    /**
     * Anzahl an Lagermöglichkeiten, die beim Einplanen getestet werden sollen.
     */
    public static final int MAX_DESTINATIONTEST = 1;

    public static LongValue minTransshipDuration = new LongValue(25 * 60 * 1000); //Diese Dauer ist mindestens vorgesehen für einen Direktumschlag
    public static boolean ignoreNotDirectTrasportableAtRouting = false;

    public static double transportOperation_TimeMovement_DurationWeight = 1.;
    public final static HashMap<Class<?>, Double> factors = new HashMap<>();

    public static boolean allowInsert = true;
    public static boolean TimeStepBasedPriorityDetermination = false;
    public static boolean legacy = false;

    static {
        factors.put(Crane.class, 2.0);
        factors.put(LCSystem.class, 0.7);

    }

    public static void initializeLogger(Level l) {
        logger.setLevel(l);
        for (Handler handler : logger.getHandlers()) {
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return record.getMessage() + "\n";
                }
            });
            handler.setLevel(l);
        }
    }

    public static FuzzyInterval getCapacity(double value) {
        return FuzzyFactory.createLinearInterval(value, TransshipmentParameter.defaultCapacityUncertainty);
    }

    public static void initializeLogger(Level l, File folder) {
        logger.setLevel(l);
        try {
            File f = new File(folder, "Log.txt");
            TransshipmentParameter.logger.addHandler(new FileHandler(f.getAbsolutePath()));
            TransshipmentParameter.logger.addHandler(new ConsoleHandler());

            for (Handler h : logger.getHandlers()) {
                h.setLevel(l);
                h.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        if (!(record.getLoggerName().equals("sun.awt.windows.WDesktopProperties"))) {
                            return record.getMillis() + "\t" + record.getMessage() + "\n";
                        } else {
                            return "";
                        }
                    }
                });
            }
            FileOutputStream fos = new FileOutputStream(f, true);
            TeeOutputStream myOut = new TeeOutputStream(System.out, fos);
            PrintStream outPS = new PrintStream(myOut);

            System.setErr(outPS);
            System.setOut(outPS);
        } catch (IOException ex) {
            Logger.getLogger(TransshipmentParameter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TransshipmentParameter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
