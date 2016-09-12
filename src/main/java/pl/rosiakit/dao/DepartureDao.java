
package pl.rosiakit.dao;

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
public interface DepartureDao {

    List<Departure> findLineDeparturesAfter(Platform platform, int dayType, Line line, LocalTime time);

    void saveDeparture(Departure departure);

    void saveAll(LinkedList<Departure> departures);

    void deleteDeparture(Departure departure);

}
