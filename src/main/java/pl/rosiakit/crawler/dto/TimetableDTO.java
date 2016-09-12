
package pl.rosiakit.crawler.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Container class that contains timetable. Each timetable have separated departure lists for weekdays, saturdays and
 * holidays.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class TimetableDTO {
    public List<DepartureDTO> weekdays = new ArrayList<>();
    public List<DepartureDTO> saturdays = new ArrayList<>();
    public List<DepartureDTO> holidays = new ArrayList<>();
    public String legend = "";    
}
