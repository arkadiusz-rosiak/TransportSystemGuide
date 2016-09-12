
package pl.rosiakit.bo;

import pl.rosiakit.dao.PlatformDao;
import pl.rosiakit.dao.PlatformDaoImpl;
import pl.rosiakit.model.Platform;
import pl.rosiakit.utils.Haversine;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class PlatformBoImpl implements PlatformBo{

    private final PlatformDao dao = new PlatformDaoImpl();
    
    @Override
    public Platform findById(String id){
        return dao.findById(id);
    }

    @Override
    public List<Platform> findPlatformsNearestToPoint(float lat, float lng, int maxDistanceInMeters) {

        List<Platform> platformsInRectangle = findPlatformsInRectangle(lat, lng, maxDistanceInMeters);

        Collections.sort(platformsInRectangle,
                (p1, p2) -> (int) Haversine.calculateDistanceInMeters(p1.getLat(), p1.getLng(), lat, lng)
                - (int) Haversine.calculateDistanceInMeters(p2.getLat(), p2.getLng(), lat, lng));

        return platformsInRectangle;
    }

    public List<Platform> findPlatformsInRectangle(float lat, float lng, int maxDistanceInMeters){

        // one lat degree is about 110575 meters
        float lat1 = lat - (maxDistanceInMeters/110575f);
        float lat2 = lat + (maxDistanceInMeters/110575f);

        // one lng degree is about cos(lat)*110575 meters
        float lng1 = lng - maxDistanceInMeters / (float) Math.abs(Math.cos(Math.toRadians(lat))*110575.0f);
        float lng2 = lng + maxDistanceInMeters / (float) Math.abs(Math.cos(Math.toRadians(lat))*110575.0f);

        return dao.findInRectangle(lat1, lat2, lng1, lng2);
    }

    @Override
    public void savePlatform(Platform platform){
        dao.savePlatform(platform);
    }

    @Override
    public void deletePlatformById(String id){
        dao.deletePlatformById(id);
    }

}
