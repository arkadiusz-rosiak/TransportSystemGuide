package pl.rosiakit.bo;

import org.springframework.stereotype.Component;
import pl.rosiakit.dao.LineDao;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.VehicleType;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */

@Component("lineBo")
@Transactional
public class LineBoImpl implements LineBo {

    private final LineDao lineDao;

    LineBoImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    public Line findById(int id) {
        return lineDao.findById(id);
    }

    @Override
    public List<Line> findLinesByAgency(String agency) {
        return lineDao.findByAgencyName(agency);
    }

    @Override
    public List<Line> findLinesByName(String name) {
        return lineDao.findByName(name);
    }

    @Override
    public List<Line> findLinesByType(String type) {
        VehicleType vehicleType = VehicleType.valueOf(type);

        try {
            return lineDao.findByType(vehicleType);
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong vehicle Type");
        }
    }

    @Override
    public Line findLineByAgencyAndName(String agency, String name) {
        return lineDao.findByAgencyNameAndName(agency, name);
    }

    @Override
    public void saveLine(Line line) {
        lineDao.save(line);
    }

    @Override
    public List<Line> findAllLines() {
        return lineDao.findAll();
    }
}
