
package pl.rosiakit.dao;

import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import javax.persistence.TypedQuery;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
class DepartureDaoImpl extends AbstractDao<Integer, Departure> implements DepartureDao{

    @Override
    public List<Departure> findLineDeparturesAfter(Platform platform, int dayType, Line line, LocalTime time){
        TypedQuery<Departure> query = getEM().createQuery(
                "SELECT this FROM Departure this "
                        + "WHERE this.platform = :platform and this.dayType = :dayType "
                        + "and this.departureTime >= :time and this.line = :line", Departure.class);
        
        return query.setParameter("platform", platform).setParameter("dayType", dayType)
                .setParameter("time", time).setParameter("line", line).getResultList();
    }
    
    @Override
    public void saveDeparture(Departure departure){
        this.persist(departure);
    }

    @Override
    public void saveAll(LinkedList<Departure> departures){
        super.saveAll(departures);
    }

    @Override
    public void deleteDeparture(Departure departure){
        this.delete(departure);
    }

}
