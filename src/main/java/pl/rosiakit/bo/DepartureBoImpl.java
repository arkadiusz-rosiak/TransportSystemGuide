
package pl.rosiakit.bo;

import pl.rosiakit.dao.DepartureDao;
import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
class DepartureBoImpl implements DepartureBo {

    private final DepartureDao dao = DepartureDao.getInstance();

    @Override
    public List<Departure> findLineDeparturesAfter(Platform platform, int dayType, Line line, LocalTime time){
        return dao.findLineDeparturesAfter(platform, dayType, line, time);
    }

    @Override
    public void saveDeparture(Departure departure){
        dao.saveDeparture(departure);
    }

    @Override
    public void saveAll(LinkedList<Departure> departures) {
        dao.saveAll(departures);
    }
    
    @Override
    public void deleteDeparture(Departure departure){
        dao.deleteDeparture(departure);
    }

}
