
package pl.rosiakit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.rosiakit.model.Connection;
import pl.rosiakit.model.Platform;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public interface ConnectionDao extends JpaRepository<Connection, Long>{

    Connection findById(int id);

    @Query("SELECT this FROM Connection this WHERE this.source = ?1 AND this.target = ?2")
    Connection findBySourceTarget(Platform source, Platform target);

}
