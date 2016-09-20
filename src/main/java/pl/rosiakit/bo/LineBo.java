package pl.rosiakit.bo;

import pl.rosiakit.model.Line;

import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */
public interface LineBo  {

    Line findById(int id);

    List<Line> findLinesByAgency(String agency);

    List<Line> findLinesByName(String name);

    List<Line> findLinesByType(String type);

    Line findLineByAgencyAndName(String agency, String name);

    void saveLine(Line line);

    List<Line> findAllLines();
}
