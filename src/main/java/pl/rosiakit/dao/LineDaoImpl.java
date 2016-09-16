
package pl.rosiakit.dao;

import pl.rosiakit.model.Line;
import pl.rosiakit.model.VehicleType;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
class LineDaoImpl extends AbstractDao<Integer, Line> implements LineDao{

    @Override
    public Line findById(int id){
        return this.getByKey(id);
    }

    @Override
    public Line findByAgencyAndName(String agency, String name) {
        TypedQuery<Line> query = getEM().createQuery(
        "SELECT this FROM Line this WHERE this.name = :name and this.agencyName = :agency", Line.class);
        return query.setParameter("name", name).setParameter("agency", agency).getSingleResult();
    }

    @Override
    public List<Line> findByAgency(String agency) {
        TypedQuery<Line> query = getEM().createQuery(
                "SELECT this FROM Line this WHERE this.agencyName = :agency", Line.class);

        query.setParameter("agency", agency);
        return query.getResultList();
    }

    @Override
    public List<Line> findByName(String name) {
        TypedQuery<Line> query = getEM().createQuery(
                "SELECT this FROM Line this WHERE this.name = :name", Line.class);

        query.setParameter("name", name);
        return query.getResultList();
    }

    @Override
    public List<Line> findByType(VehicleType type) {
        TypedQuery<Line> query = getEM().createQuery(
                "SELECT this FROM Line this WHERE this.type = :type", Line.class);

        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    public void saveLine(Line line){
        this.persist(line);
    }

    @Override
    public List<Line> findAllLines(){
        return this.findAll();
    }
    
}
