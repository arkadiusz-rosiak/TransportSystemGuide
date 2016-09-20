package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Route;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface RouteDao extends JpaRepository<Route, Long>{

    @Query("SELECT this FROM Route this WHERE this.line = ?1 ORDER BY this.direction ASC, this.ordinalNumber ASC")
    List<Route> findLineRoutes(Line line);

    @Modifying
    @Query("DELETE FROM Route this WHERE this.line = ?1")
    int deleteLineRoutes(Line line);

}
