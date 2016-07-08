/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte;

import applications.transshipment.model.structs.Terminal;

/**
 *
 * @author bode
 */
public interface TerminalGenerator {
    public Terminal generateTerminal(ParameterInputFile parameters);
}
