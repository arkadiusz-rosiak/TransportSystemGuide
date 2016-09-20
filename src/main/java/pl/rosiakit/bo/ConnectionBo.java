package pl.rosiakit.bo;

import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface ConnectionBo {

    Connection findConnectionBySourceTarget(Platform source, Platform target);

    void saveConnection(Connection connection);

    void deleteConnection(Connection connection);

}

