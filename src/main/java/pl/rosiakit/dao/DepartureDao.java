package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.time.LocalTime;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface DepartureDao extends JpaRepository<Departure, Long> {

    Departure findTop1ByPlatformAndDayTypeAndLineAndDepartureTimeGreaterThanOrderByDepartureTimeAsc
            (Platform platform, int dayType, Line line, LocalTime time);

}
