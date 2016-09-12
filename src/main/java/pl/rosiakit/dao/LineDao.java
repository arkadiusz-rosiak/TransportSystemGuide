
package pl.rosiakit.dao;

import pl.rosiakit.model.Line;
import pl.rosiakit.model.VehicleType;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface LineDao {

    Line findById(int id);

    Line findByAgencyAndName(String agency, String name);

    List<Line> findByAgency(String agency);

    List<Line> findByName(String name);

    List<Line> findByType(VehicleType type);

    void saveLine(Line platform);

    List<Line> findAllLines();
    
}
