
package pl.rosiakit.dao;

import pl.rosiakit.model.Platform;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface PlatformDao {
    
    Platform findById(String id);

    List<Platform> findInRectangle(float lat1, float lat2, float lng1, float lng2);

    void savePlatform(Platform platform);
     
    void deletePlatformById(String id);

}
