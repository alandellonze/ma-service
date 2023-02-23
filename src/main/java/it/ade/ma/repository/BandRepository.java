package it.ade.ma.repository;

import it.ade.ma.entities.Band;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;

@Repository
public interface BandRepository extends CrudRepository<Band, Long> {

    @NonNull
    default List<Band> findAll(String name) {
        return hasLength(name) ? findAllByName(name) : findAll();
    }

    @NonNull
    @Query(nativeQuery = true, value = ""
            + " SELECT *"
            + " FROM Band"
            + " WHERE UPPER(UNACCENT(name)) LIKE '%' || UPPER(TRIM(UNACCENT(:name)) || '%')"
            + " ORDER BY name, id")
    List<Band> findAllByName(String name);

    @NonNull
    @Override
    @Query("FROM Band ORDER BY name, id")
    List<Band> findAll();

}
