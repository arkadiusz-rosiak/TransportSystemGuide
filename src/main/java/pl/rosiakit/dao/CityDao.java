package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rosiakit.model.City;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-24
 */
public interface CityDao extends JpaRepository<City, Long> {

    City findTop1ByName(String name);
}
