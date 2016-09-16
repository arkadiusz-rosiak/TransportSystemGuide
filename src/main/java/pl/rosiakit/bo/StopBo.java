
package pl.rosiakit.bo;

import pl.rosiakit.model.Stop;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface StopBo {

    static StopBo getInstance(){
        return new StopBoImpl();
    }

    Stop findStopById(int id);

    Stop findSingleStopByName(String name);

    List<Stop> findStopsByName(String name);

    void saveStop(Stop stop);
    
    void deleteStop(Stop stop);
    
    List<Stop> findAllStops();
}
