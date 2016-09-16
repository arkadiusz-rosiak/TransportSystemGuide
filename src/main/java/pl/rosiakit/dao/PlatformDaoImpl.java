
package pl.rosiakit.dao;

import pl.rosiakit.model.Platform;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
class PlatformDaoImpl extends AbstractDao<String, Platform> implements PlatformDao{
    
    @Override
    public Platform findById(String id){
        return this.getByKey(id);
    }

    @Override
    public List<Platform> findInRectangle(float lat1, float lat2, float lng1, float lng2) {
        TypedQuery<Platform> query = getEM().createQuery(
                "SELECT this FROM Platform this where " +
                        "this.lat between :lat1 and :lat2 and " +
                        "this.lng between :lng1 and :lng2", Platform.class);

        query.setParameter("lat1", lat1);
        query.setParameter("lat2", lat2);
        query.setParameter("lng1", lng1);
        query.setParameter("lng2", lng2);

        return query.getResultList();
    }

    @Override
    public void savePlatform(Platform platform){
        this.persist(platform);
    }

    @Override
    public void deletePlatformById(String id){
        Platform platform = this.getByKey(id);
        this.delete(platform);
    }

}
