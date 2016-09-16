
package pl.rosiakit.bo;

import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface ConnectionBo {

    static ConnectionBo getInstance(){
        return new ConnectionBoImpl();
    }

    Connection findConnectionBySourceTarget(Platform source, Platform target);

    void saveConnection(Connection connection);
    
    void deleteConnection(Connection connection);
    
}
