
package pl.rosiakit.dao;

import pl.rosiakit.model.Stop;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class StopDaoImpl extends AbstractDao<Integer, Stop> implements StopDao {

    @Override
    public Stop findById(int id){
        return this.getByKey(id);
    }

    @Override
    public Stop findSingleByName(String name) {
        TypedQuery<Stop> query = getEM().createQuery(
                "SELECT this FROM Stop this WHERE this.name = :name", Stop.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public List<Stop> findByName(String name){
        TypedQuery<Stop> query = getEM().createQuery(
        "SELECT this FROM Stop this WHERE this.name LIKE :name ORDER BY this.name", Stop.class);
        query.setParameter("name", name+"%");
        return query.getResultList();
    }
    
    @Override
    public void saveStop(Stop stop){
        this.persist(stop);
    }

    @Override
    public void deleteStop(Stop stop){
        this.delete(stop);
    }
        
    @Override
    public List<Stop> findAllStops(){
        TypedQuery<Stop> query = getEM().createQuery(
                "SELECT this FROM Stop this ORDER BY this.name", Stop.class);
        return query.getResultList();
    }
    
}
