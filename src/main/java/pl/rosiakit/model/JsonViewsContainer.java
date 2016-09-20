package pl.rosiakit.model;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */
public class JsonViewsContainer {

    public interface ResponseView{}

    public interface LinesSummary extends ResponseView{}

    public interface StopsSummary extends ResponseView{}

    public interface StopsDetails extends StopsSummary{}

    public interface StopsWithDistances extends StopsDetails{}

    public interface JourneyView extends StopsDetails, LinesSummary{}
}
