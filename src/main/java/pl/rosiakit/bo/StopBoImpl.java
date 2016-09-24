package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.rosiakit.dao.StopDao;
import pl.rosiakit.model.City;
import pl.rosiakit.model.Stop;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */

@Component("stopBo")
@Transactional
public class StopBoImpl implements StopBo {

    private final StopDao stopDao;

    StopBoImpl(StopDao stopDao) {
        this.stopDao = stopDao;
    }

    @Override
    public Stop findStopById(int id) {
        Assert.notNull(id, "ID must not be null");
        return stopDao.findById(id);
    }

    @Override
    public Stop findSingleStopByNameAnCity(String name, City city) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(city, "City must not be null");
        return stopDao.findByNameAndCity(name, city);
    }

    @Override
    public List<Stop> findStopsByName(String name) {
        Assert.notNull(name, "Name must not be null");
        return stopDao.findByNameStartingWithOrderByNameAsc(name);
    }

    @Override
    public List<Stop> findStopsContainingName(String name) {
        Assert.notNull(name, "Name must not be null");
        return stopDao.findByNameContainingOrderByNameAsc(name);
    }

    @Override
    public List<Stop> findStopsInCity(City city) {
        Assert.notNull(city, "City must not be null");
        return stopDao.findByCityOrderByNameAsc(city);
    }

    @Override
    public void saveStop(Stop stop) {
        Assert.notNull(stop, "Stop must not be null");
        stopDao.save(stop);
    }

    @Override
    public void deleteStop(Stop stop) {
        Assert.notNull(stop, "Stop must not be null");
        stopDao.delete(stop);
    }

    @Override
    public List<Stop> findAllStops() {
        return stopDao.findByOrderByNameAsc();
    }
}
