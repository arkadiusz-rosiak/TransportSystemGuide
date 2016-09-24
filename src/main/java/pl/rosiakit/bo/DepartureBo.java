package pl.rosiakit.bo;

import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.time.LocalTime;
import java.util.LinkedList;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface DepartureBo {

    Departure findLineDepartureAfter(Platform platform, int dayType, Line line, LocalTime time);

    Departure findLineDepartureAbout(Platform platform, int dayType, Line line, LocalTime time);

    void saveDeparture(Departure departure);

    void saveAll(LinkedList<Departure> departures);

    void deleteDeparture(Departure departure);

}