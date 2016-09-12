
package pl.rosiakit.dao;

import pl.rosiakit.model.Stop;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface StopDao {

    Stop findById(int id);

    Stop findSingleByName(String name);

    List<Stop> findByName(String name);
    
    void saveStop(Stop stop);
    
    void deleteStop(Stop stop);
        
    List<Stop> findAllStops();
    
}
