package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import pl.rosiakit.dao.RouteDao;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Route;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */

@Component("routeBo")
@Transactional
public class RouteBoImpl implements RouteBo {

    private final RouteDao routeDao;

    public RouteBoImpl(RouteDao routeDao) {
        this.routeDao = routeDao;
    }

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
    public List<Route> findLineRoutes(Line line) {
        return routeDao.findLineRoutes(line);
    }

    @Override
    public void saveLineRoute(Route route) {
        routeDao.save(route);
    }

    @Override
    public void saveAllLineRoutes(LinkedList<Route> routes) {
        routeDao.save(routes);
    }

    @Override
    public void delete(Route route) {
        routeDao.delete(route);
    }

    @Override
    public void deleteLineRoutes(Line line) {
        routeDao.deleteLineRoutes(line);
    }
}
