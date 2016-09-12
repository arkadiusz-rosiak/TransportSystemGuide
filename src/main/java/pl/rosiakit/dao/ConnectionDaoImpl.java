
package pl.rosiakit.dao;

import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

import javax.persistence.TypedQuery;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class ConnectionDaoImpl extends AbstractDao<Integer, Connection> implements ConnectionDao{

    @Override
    public Connection findById(int id){
        return this.getByKey(id);
    }

    @Override
    public Connection findBySourceTarget(Platform source, Platform target){

        TypedQuery<Connection> query = getEM().createQuery(
                "SELECT this FROM Connection this WHERE this.source = :source AND this.target = :target",
                Connection.class);

        query.setParameter("source", source);
        query.setParameter("target", target);

        return query.getSingleResult();
    }

    @Override
    public void save(Connection connection){
        this.persist(connection);
    }
    
    @Override
    public void delete(Connection connection){
        super.delete(connection);
    }
    
}
