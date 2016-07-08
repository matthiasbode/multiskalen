package applications.transshipment.generator;

import applications.transshipment.generator.json.JsonLoadUnitJob;
import applications.transshipment.model.LoadUnitJob;
import applications.transshipment.model.loadunits.LoadUnit;
import applications.transshipment.model.loadunits.Swapbody;
import applications.transshipment.model.loadunits.TwistLockLoadUnit;
import applications.transshipment.model.resources.storage.LoadUnitStorage;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import util.jsonTools.JSONSerialisierung;

/**
 *
 * @author behrensd
 */
public class LoadUnitGeneratorFromJSON implements LoadUnitGenerator{

    
    InputStream stream;
    int limit;
    String name ="";

    public LoadUnitGeneratorFromJSON(InputStream stream, int limit) {
   
        this.stream = stream;
        this.limit = limit;
    }

    public LoadUnitGeneratorFromJSON( InputStream stream, int limit, String name) {
        this.stream = stream;
        this.limit = limit;
        this.name = name;
    }
    
    

    @Override
    public  List<LoadUnitJob> generateJobs(List<Train> trains, Terminal terminal) {
        Collection<LoadUnitStorage> lStorage = terminal.getStorages();

        ArrayList<JsonLoadUnitJob> jsonJobs = JSONSerialisierung.importJSON(stream, new TypeToken<ArrayList<JsonLoadUnitJob>>() {
        }.getType());
        List<JsonLoadUnitJob> subList = jsonJobs;
        if (limit != 0 && jsonJobs.size() > limit) {
            subList = jsonJobs.subList(0, limit - 1);
        }
        ArrayList<LoadUnitJob> jobs = new ArrayList<>();
        for (JsonLoadUnitJob job : subList) {
            LoadUnit lu;
            if ("TwistLockLoadUnit".equals(job.getTyp())) {
                lu = new TwistLockLoadUnit(job.getLength(), job.isHazardous());
            } else {
                lu = new Swapbody(job.getLength(), job.isHazardous());
            }
            lu.setId(job.getId());
            if ("Slot".equals(job.getOrigin().getType())) {
                Slot slot = trains.get(job.getOrigin().getTrain().getNumber()).getStorageLocations().get(job.getOrigin().getNumber());
                if (slot == null) {
                    throw new NullPointerException();
                }
                lu.setOrigin(slot);
            }
            if ("Slot".equals(job.getDestination().getType())) {
                Slot slot = trains.get(job.getDestination().getTrain().getNumber()).getStorageLocations().get(job.getDestination().getNumber());
                if (slot == null) {
                    throw new NullPointerException();
                }
                lu.setDestination(slot);
            }
            if ("Storage".equals(job.getOrigin().getType())) {
                LoadUnitStorage storage = null;
                for (LoadUnitStorage loadUnitStorage : lStorage) {
                    if (loadUnitStorage.getID().equals(job.getOrigin().getName())) {
                        storage = loadUnitStorage;
                    }
                }
                lu.setOrigin(storage);
            }
            if ("Storage".equals(job.getDestination().getType())) {
                LoadUnitStorage storage = null;
                for (LoadUnitStorage loadUnitStorage : lStorage) {
                    if (loadUnitStorage.getID().equals(job.getDestination().getName())) {
                        storage = loadUnitStorage;
                    }
                }
                lu.setDestination(storage);
            }
            if ("Train".equals(job.getDestination().getType())) {
                LoadUnitStorage storage = null;
                for (Train train : trains) {
                    if (train.getNumber() == job.getDestination().getNumber()) {
                        storage = train;
                    }
                }
                lu.setDestination(storage);
            }
            if (lu.getDestination() == null || lu.getOrigin() == null) {
                throw new NoSuchElementException("Kein Origin oder Destination für Job gesetzt " +job +"\n"+lu.getOrigin()+"\n"+lu.getDestination());
            }
            jobs.add(new LoadUnitJob(lu));

        }
        return jobs;
    }

    @Override
    public String toString() {
        return "LoadUnitGeneratorFromJSON{" + name + '}';
    }
    
    
}
