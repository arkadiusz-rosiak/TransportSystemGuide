
package pl.rosiakit.bo;

import pl.rosiakit.dao.LineDao;
import pl.rosiakit.dao.LineDaoImpl;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.VehicleType;

import java.util.List;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class LineBoImpl implements LineBo {

    private final LineDao dao = new LineDaoImpl();
    
    @Override
    public Line findById(int id){
        return dao.findById(id);
    }

    @Override
    public List<Line> findLinesByAgency(String agency) {
        return dao.findByAgency(agency);
    }

    @Override
    public List<Line> findLinesByName(String name) {
        return dao.findByName(name);
    }

    @Override
    public List<Line> findLinesByType(String type) {
        VehicleType vehicleType = VehicleType.valueOf(type);

        try{
            return dao.findByType(vehicleType);
        }
        catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Wrong vehicle Type");
        }
    }

    @Override
    public Line findLineByAgencyAndName(String agency, String name) {
        return dao.findByAgencyAndName(agency, name);
    }
    
    @Override
    public void saveLine(Line line){
        dao.saveLine(line);
    }

    @Override
    public List<Line> findAllLines(){
        return dao.findAllLines();
    }
    
}
