package it.ade.ma.repository;

import it.ade.ma.entities.Album;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends CrudRepository<Album, Long> {

    List<Album> findAllByBandIdOrderByPosition(long bandId);

}
