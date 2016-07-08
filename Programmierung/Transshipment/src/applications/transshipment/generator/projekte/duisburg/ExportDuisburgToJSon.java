/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.duisburg;

import applications.transshipment.generator.json.JsonTerminal;
import applications.transshipment.model.structs.Terminal;
import java.io.File;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author bode
 */
public class ExportDuisburgToJSon {

    public static void main(String[] args) {
        DuisburgTerminalGenerator tg = new DuisburgTerminalGenerator();
        Terminal generateTerminal = tg.generateTerminal(new DuisburgInputParameters());
        JsonTerminal jsonTerminal = new JsonTerminal(generateTerminal);
        JSONSerialisierung.exportJSON(new File("DuisburgTerminal.json"), jsonTerminal, true);
    }
}
