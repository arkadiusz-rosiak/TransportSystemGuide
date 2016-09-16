
package pl.rosiakit.dao;

import pl.rosiakit.model.Line;
import pl.rosiakit.model.Route;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface RouteDao {

    static RouteDao getInstance(){
        return new RouteDaoImpl();
    }

    List<Route> findLineRoutes(Line line);
    
    void save(Route route);

    void saveAll(LinkedList<Route> routes);
    
    void delete(Route route);
    
    int deleteLineRoutes(Line line);
}
