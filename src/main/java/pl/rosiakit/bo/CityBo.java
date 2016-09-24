package pl.rosiakit.bo;

import pl.rosiakit.model.City;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-24
 */
public interface CityBo {

    void saveCity(City city);

   City findCityByName(String name);

}
