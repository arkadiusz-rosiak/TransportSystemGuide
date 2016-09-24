package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import pl.rosiakit.dao.DepartureDao;
import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.LinkedList;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
@Component("departureBo")
@Transactional
public class DepartureBoImpl implements DepartureBo {

    private final DepartureDao departureDao;

    public DepartureBoImpl(DepartureDao departureDao) {
        this.departureDao = departureDao;
    }

    @Override
    public Departure findLineDepartureAfter(Platform platform, int dayType, Line line, LocalTime time) {
        return departureDao.findTop1ByPlatformAndDayTypeAndLineAndDepartureTimeGreaterThanOrderByDepartureTimeAsc
                (platform, dayType, line, time);
    }

    @Override
    public Departure findLineDepartureAbout(Platform platform, int dayType, Line line, LocalTime time) {
         return departureDao.findTop1ByPlatformAndDayTypeAndLineAndDepartureTimeGreaterThanOrderByDepartureTimeAsc
                 (platform, dayType, line, time.minusMinutes(3));
    }

    @Override
    public void saveDeparture(Departure departure) {
        departureDao.save(departure);
    }

    @Override
    public void saveAll(LinkedList<Departure> departures) {
        departureDao.save(departures);
    }

    @Override
    public void deleteDeparture(Departure departure) {
        departureDao.delete(departure);
    }
}
