/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jsonTools;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author behrensd
 */
public class JSONSerialisierung {

    public static <E> void exportJSON(File jsonFile, E instance, boolean pretty) {

        GsonBuilder builder = new GsonBuilder();
        if (pretty) {
            builder.setPrettyPrinting();
        }
        Gson gson = builder.create();

        try {
            jsonFile.createNewFile();
            try (FileOutputStream fOut = new FileOutputStream(jsonFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut)) {
                gson.toJson(instance, myOutWriter);
                myOutWriter.close();
                fOut.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(JSONSerialisierung.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static <E> E importJSON(InputStream jsonStream, Type type) {
        Gson gson = new Gson();
        InputStreamReader myInReader = new InputStreamReader(jsonStream);
        E e = gson.fromJson(myInReader, type);
        return e;
    }

     

}
