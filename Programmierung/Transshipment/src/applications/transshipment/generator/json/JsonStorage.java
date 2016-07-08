/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.json;

import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.resources.storage.simpleStorage.SimpleStorageRow;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.Wagon;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author behrensd
 */
public class JsonStorage {

    private String type;
    @SerializedName("train")
    private JsonTrain train;
    private int number;
    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonTrain getTrain() {
        return train;
    }

    public void setTrain(JsonTrain train) {
        this.train = train;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public JsonStorage() {
    }

    public JsonStorage(LoadUnitStorage storage) {

        if (storage instanceof Slot) {
            Slot s = (Slot) storage;
            this.type = "Slot";
            this.train = new JsonTrain();
            if (s.getSuperResource() instanceof Wagon) {
                Wagon w = (Wagon) s.getSuperResource();
                this.train.setNumber(w.getSuperResource().getNumber());
                this.number = w.getSuperResource().getStorageLocations().indexOf(s);
            }
        }
        if (storage instanceof SimpleStorageRow) {
            SimpleStorageRow row = (SimpleStorageRow) storage;
            this.type = "Storage";
            this.name = row.getID();
        }

       if (storage instanceof Train) {
            this.type = "Train";
            this.train = new JsonTrain();
            Train t = (Train) storage;
            this.number = t.getNumber();
        }
    }

    @Override
    public String toString() {
        return "JsonStorage{" + "type=" + type + ", train=" + train + ", number=" + number + ", name=" + name + '}';
    }
    
    
}
