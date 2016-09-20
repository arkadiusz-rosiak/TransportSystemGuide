package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.rosiakit.model.Platform;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-17
 */
public interface PlatformDao extends JpaRepository<Platform, String> {

    Platform findById(String id);

    @Query("SELECT p FROM Platform p where p.lat between ?1 and ?2 and p.lng between ?3 and ?4")
    List<Platform> findInRectangle(float lat1, float lat2, float lng1, float lng2);

}
