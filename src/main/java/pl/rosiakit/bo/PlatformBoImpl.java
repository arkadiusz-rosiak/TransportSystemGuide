package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.rosiakit.dao.PlatformDao;
import pl.rosiakit.model.Platform;
import pl.rosiakit.utils.Haversine;

import java.util.Collections;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-17
 */
@Component("platformBo")
@Transactional
public class PlatformBoImpl implements PlatformBo {

    private final PlatformDao platformDao;

    public PlatformBoImpl(PlatformDao platformDao) {
        this.platformDao = platformDao;
    }

    @Override
    public Platform findById(String id) {
        Assert.notNull(id, "ID must not be null");
        return platformDao.findById(id);
    }

    @Override
    public List<Platform> findPlatformsNearestToPoint(float lat, float lng, int maxDistanceInMeters) {
        Assert.notNull(lat, "Latitude must not be null");
        Assert.notNull(lng, "Longitude must not be null");
        Assert.notNull(maxDistanceInMeters, "Max distance must not be null");

        List<Platform> platformsInRectangle = findPlatformsInRectangle(lat, lng, maxDistanceInMeters);

        for(Platform p : platformsInRectangle){
            p.setDistance((int) Math.round(Haversine.calculateDistanceInMeters(p.getLat(), p.getLng(), lat, lng)));
        }

        Collections.sort(platformsInRectangle, (p1, p2) -> p1.getDistance() - p2.getDistance());

        return platformsInRectangle;
    }

    @Override
    public List<Platform> findPlatformsInRectangle(float lat, float lng, int maxDistanceInMeters) {
        Assert.notNull(lat, "Latitude must not be null");
        Assert.notNull(lng, "Longitude must not be null");
        Assert.notNull(maxDistanceInMeters, "Max distance must not be null");

        // one lat degree is about 110575 meters
        float lat1 = lat - (maxDistanceInMeters/110575f);
        float lat2 = lat + (maxDistanceInMeters/110575f);

        // one lng degree is about cos(lat)*110575 meters
        float lng1 = lng - maxDistanceInMeters / (float) Math.abs(Math.cos(Math.toRadians(lat))*110575.0f);
        float lng2 = lng + maxDistanceInMeters / (float) Math.abs(Math.cos(Math.toRadians(lat))*110575.0f);

        return platformDao.findInRectangle(lat1, lat2, lng1, lng2);
    }

    @Override
    public void savePlatform(Platform platform) {

    }
}
