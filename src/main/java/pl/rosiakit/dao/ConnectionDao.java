
package pl.rosiakit.dao;

import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface ConnectionDao {

    Connection findById(int id);
    
    Connection findBySourceTarget(Platform source, Platform target);

    void save(Connection connection);
    
    void delete(Connection connection);
}
