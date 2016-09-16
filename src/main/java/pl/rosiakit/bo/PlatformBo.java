
package pl.rosiakit.bo;

import pl.rosiakit.model.Platform;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface PlatformBo {

    static PlatformBo getInstance(){
        return new PlatformBoImpl();
    }

    Platform findById(String id);

    List<Platform> findPlatformsNearestToPoint(float lat, float lng, int maxDist);

    List<Platform> findPlatformsInRectangle(float lat, float lng, int maxDist);

    void savePlatform(Platform platform);
     
    void deletePlatformById(String id);
}
