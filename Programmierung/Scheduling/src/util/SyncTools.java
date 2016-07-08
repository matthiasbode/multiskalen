/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author bode
 */
public class SyncTools {

    public static void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }
    
      public static void safePrinterr(String s) {
        synchronized (System.err) {
            System.err.println(s);
        }
    }
}
