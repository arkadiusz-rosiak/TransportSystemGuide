package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rosiakit.model.Stop;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */
public interface StopDao extends JpaRepository<Stop, Long> {

    Stop findById(int id);

    Stop findByName(String name);

    List<Stop> findByNameStartingWithOrderByNameAsc(String name);

    List<Stop> findByNameContainingOrderByNameAsc(String name);

    List<Stop> findByOrderByNameAsc();

}
