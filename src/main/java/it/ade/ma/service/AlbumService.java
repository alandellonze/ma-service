package it.ade.ma.service;

import it.ade.ma.entities.Album;
import it.ade.ma.entities.Band;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumDTO findById(long albumId) {
        return convert(albumRepository.findById(albumId).orElseThrow());
    }

    public List<AlbumDTO> findAllByBandId(long bandId) {
        return albumRepository.findAllByBandIdOrderByPosition(bandId)
                .stream().map(this::convert).collect(toList());
    }

    public void save(AlbumDTO albumDTO) {
        // save or update the album
        Album album = albumRepository.save(convert(albumDTO));
        log.info("{} {}", album, albumDTO.getId() == null ? "saved" : "updated");

        // adjust positions
        adjustPositions(albumDTO.getBandId());
    }

    public void delete(long bandId, long albumId) {
        // delete the album
        albumRepository.deleteById(albumId);
        log.info("album with id: {} deleted", albumId);

        // adjust positions
        adjustPositions(bandId);
    }

    private void adjustPositions(long bandId) {
        log.info("adjustPositions({})", bandId);

        // get all the Albums
        List<Album> albums = albumRepository.findAllByBandIdOrderByPosition(bandId);

        // adjust position
        for (int i = 0; i < albums.size(); i++) {
            Album album = albums.get(i);
            int position = i + 1;
            if (!Objects.equals(position, album.getPosition())) {
                log.debug("{}, {}: {} -> {} - {}", album.getBand().getName(), i, album.getPosition(), position, album.getName());
                album.setPosition(position);
                albumRepository.save(album);
            }
        }
    }

    // UTIL

    private AlbumDTO convert(Album album) {
        AlbumDTO albumDTO = new AlbumDTO();
        copyProperties(album, albumDTO);
        albumDTO.setBandId(album.getBand().getId());
        albumDTO.setBandName(album.getBand().getName());
        return albumDTO;
    }

    private Album convert(AlbumDTO albumDTO) {
        Album album = new Album();
        copyProperties(albumDTO, album);
        album.setBand(new Band(albumDTO.getBandId()));
        return album;
    }

}
