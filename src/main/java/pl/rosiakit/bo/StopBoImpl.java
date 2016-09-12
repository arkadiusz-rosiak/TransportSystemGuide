
package pl.rosiakit.bo;

import pl.rosiakit.dao.StopDao;
import pl.rosiakit.dao.StopDaoImpl;
import pl.rosiakit.model.Stop;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class StopBoImpl implements StopBo{

    private final StopDao dao = new StopDaoImpl();
    
    @Override
    public Stop findStopById(int id){
        return dao.findById(id);
    }

    @Override
    public Stop findSingleStopByName(String name) {
        return dao.findSingleByName(name);
    }

    @Override
    public List<Stop> findStopsByName(String name){
        return dao.findByName(name);
    }
        
    @Override
    public void saveStop(Stop stop){
        dao.saveStop(stop);
    }
    
    @Override
    public void deleteStop(Stop stop){
        dao.deleteStop(stop);
    }

    @Override
    public List<Stop> findAllStops(){
        return dao.findAllStops();
    }
    
}
