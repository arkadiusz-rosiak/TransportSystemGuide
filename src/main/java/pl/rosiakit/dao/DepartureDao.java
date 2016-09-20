package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query("SELECT this FROM Departure this WHERE this.platform = ?1 and this.dayType = ?2 "
            + "and this.departureTime >= ?4 and this.line = ?3")
    List<Departure> findLineDeparturesAfter(Platform platform, int dayType, Line line, LocalTime time);


}
