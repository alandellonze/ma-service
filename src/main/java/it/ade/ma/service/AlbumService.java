package it.ade.ma.service;

import it.ade.ma.entities.Album;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;

    public List<AlbumDTO> findAllByBandId(long bandId) {
        return albumRepository.findAllByBandIdOrderByPosition(bandId)
                .stream().map(this::convertToDTO).collect(toList());
    }

    private AlbumDTO convertToDTO(Album album) {
        AlbumDTO albumDTO = new AlbumDTO();
        copyProperties(album, albumDTO);
        albumDTO.setBandId(album.getBand().getId());
        albumDTO.setBandName(album.getBand().getName());
        return albumDTO;
    }

}
