package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pl.rosiakit.dao.CityDao;
import pl.rosiakit.model.City;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-24
 */

@Component("cityBo")
@Transactional
public class CityBoImpl implements CityBo {

    private final CityDao cityDao;

    public CityBoImpl(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    @Override
    public void saveCity(City city) {
        Assert.notNull(city, "City must not be null");
        cityDao.save(city);
    }

    @Override
    public City findCityByName(String name) {
        Assert.notNull(name, "City name must not be null");
        return cityDao.findTop1ByName(name);
    }
}
