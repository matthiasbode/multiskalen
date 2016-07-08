/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bijava.math.pde.fem;

import bijava.math.pde.ModelData;

/**
 *
 * @author schierm
 */
public class TemperatureModelData implements ModelData<TemperatureModelData> {

    private double t;

    public TemperatureModelData(double t) {
        this.t = t;
    }

    @Override
    public TemperatureModelData clone() {
        return new TemperatureModelData(t);
    }

    public TemperatureModelData initialNew() {
        return new TemperatureModelData(0);
    }

    public TemperatureModelData add(TemperatureModelData md) {
        return new TemperatureModelData(t+md.t);
    }

    public TemperatureModelData mult(double scalar) {
        return new TemperatureModelData(t*scalar);
    }

    public TemperatureModelData sub(TemperatureModelData md) {
        return new TemperatureModelData(t-md.t);
    }

    public double getTemperature() {
        return t;
    }

    public String toString() {
        return "Temperatur: "+t;
    }
}
