
package pl.rosiakit.dao;

import org.hibernate.HibernateException;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Route;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class RouteDaoImpl extends AbstractDao<Integer, Route> implements RouteDao{

    @Override
    public List<Route> findLineRoutes(Line line){
        TypedQuery<Route> query = getEM().createQuery(
        "SELECT this FROM Route this WHERE this.line = :line "
        + "ORDER BY this.direction ASC, this.ordinalNumber ASC", Route.class);
        return query.setParameter("line", line).getResultList();
    }
    
    @Override
    public void save(Route route){
        super.persist(route);
    }

    @Override
    public void saveAll(LinkedList<Route> routes){
        super.saveAll(routes);
    }

    @Override
    public void delete(Route route){
        super.delete(route);
    }
    
    @Override
    public int deleteLineRoutes(Line line){
        int result = 0;
        try{
            tx = getSession().beginTransaction();
            
            Query query = getEM().createQuery(
                "DELETE FROM Route this WHERE this.line = :line ");
            result = query.setParameter("line", line).executeUpdate();
            
            tx.commit();
            
        }
        catch(HibernateException e){
            tx.rollback();
        }

        return result;
    }
    
}
