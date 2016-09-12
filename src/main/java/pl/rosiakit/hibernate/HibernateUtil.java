package pl.rosiakit.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class HibernateUtil
{   
    @PersistenceUnit
    private static EntityManager em = buildEntityManager();
    
    private static EntityManagerFactory emf;
    
    private static EntityManager buildEntityManager(){

        Map<String, String> properties = new HashMap<>();
        properties.putAll(loginCredentials());
        emf = Persistence.createEntityManagerFactory("czymDojade", properties);

        return emf.createEntityManager();
    }

    private static Map<String, String> loginCredentials(){
        Map<String, String> credentials = new HashMap<>();

        String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        String password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        String name = System.getenv("OPENSHIFT_GEAR_NAME");

        credentials.put("javax.persistence.jdbc.user", user);
        credentials.put("javax.persistence.jdbc.password", password);
        credentials.put("javax.persistence.jdbc.url", "jdbc:mysql://"+host+":"+port+"/"+name);

        return credentials;
    }

    public static EntityManager getEntityManager(){
        return em;
    }
    
    public static void shutdown() {        
        em.close();
        emf.close();
    }
}
