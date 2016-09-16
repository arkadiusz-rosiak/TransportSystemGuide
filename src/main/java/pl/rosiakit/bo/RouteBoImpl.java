
package pl.rosiakit.bo;

import pl.rosiakit.dao.RouteDao;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Route;

import java.util.*;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
class RouteBoImpl implements RouteBo{

    private final RouteDao dao = RouteDao.getInstance();

    @Override
    public Map<Integer, List<Route>> findLineRoutesSplitByDirection(Line line) {
        Map<Integer, List<Route>> routesWithDirection = new HashMap<>();
        List<Route> lineRoutes = findLineRoutes(line);

        for(Route part : lineRoutes){
            List<Route> route;

            if(routesWithDirection.containsKey(part.getDirection())){
                route = routesWithDirection.get(part.getDirection());
            }
            else{
                 route = new ArrayList<>();
            }

            route.add(part);
            routesWithDirection.put(part.getDirection(), route);
        }

        return routesWithDirection;
    }

    @Override
    public List<Route> findLineRoutes(Line line){
        return dao.findLineRoutes(line);
    }
    
    @Override
    public void saveLineRoute(Route route){
        dao.save(route);
    }

    @Override
    public void saveAllLineRoutes(LinkedList<Route> routes) {
        dao.saveAll(routes);
    }
    
    @Override
    public void delete(Route route){
        dao.delete(route);
    }    
    
    @Override
    public void deleteLineRoutes(Line line){
        dao.deleteLineRoutes(line);
    }
}
