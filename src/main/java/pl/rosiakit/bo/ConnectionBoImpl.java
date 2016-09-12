
package pl.rosiakit.bo;

import pl.rosiakit.dao.ConnectionDao;
import pl.rosiakit.dao.ConnectionDaoImpl;
import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class ConnectionBoImpl implements ConnectionBo{

    private final ConnectionDao dao = new ConnectionDaoImpl();

    @Override
    public Connection findConnectionBySourceTarget(Platform source, Platform target){
        return dao.findBySourceTarget(source, target);
    }

    @Override
    public void saveConnection(Connection connection){
        dao.save(connection);
    }
    
    @Override
    public void deleteConnection(Connection connection){
        dao.delete(connection);
    }
    
}