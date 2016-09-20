package pl.rosiakit.bo;

import pl.rosiakit.model.Platform;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-17
 */
public interface PlatformBo {

    Platform findById(String id);

    List<Platform> findPlatformsNearestToPoint(float lat, float lng, int maxDistanceInMeters);

    List<Platform> findPlatformsInRectangle(float lat, float lng, int maxDistanceInMeters);

    void savePlatform(Platform platform);

}
