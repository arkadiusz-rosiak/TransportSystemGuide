package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.VehicleType;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface LineDao extends JpaRepository<Line, Long>{

    Line findById(int id);

    Line findByAgencyNameAndName(String agency, String name);

    List<Line> findByAgencyName(String agencyName);

    List<Line> findByName(String name);

    List<Line> findByType(VehicleType type);

}