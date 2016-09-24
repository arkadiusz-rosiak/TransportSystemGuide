package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.time.LocalTime;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface DepartureDao extends JpaRepository<Departure, Long> {

    Departure findTop1ByPlatformAndDayTypeAndLineAndDepartureTimeGreaterThanOrderByDepartureTimeAsc
            (Platform platform, int dayType, Line line, LocalTime time);

    @Modifying
    @Query("DELETE FROM Departure this WHERE this.line = ?1")
    void deleteLinesDeparture(Line line);
}
