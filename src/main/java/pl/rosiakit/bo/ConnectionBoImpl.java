package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.rosiakit.dao.ConnectionDao;
import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
@Component("connectionBo")
@Transactional
public class ConnectionBoImpl implements ConnectionBo {

    private final ConnectionDao connectionDao;

    public ConnectionBoImpl(ConnectionDao connectionDao) {
        this.connectionDao = connectionDao;
    }

    @Override
    public Connection findConnectionBySourceTarget(Platform source, Platform target) {
        return connectionDao.findBySourceTarget(source, target);
    }

    @Override
    public void saveConnection(Connection connection) {
        connectionDao.save(connection);
    }

    @Override
    public void deleteConnection(Connection connection) {
        connectionDao.delete(connection);
    }
}
