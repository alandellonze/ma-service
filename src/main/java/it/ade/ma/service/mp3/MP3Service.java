package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.entities.dto.MP3FolderDTO;
import it.ade.ma.service.AlbumService;
import it.ade.ma.service.path.PathService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MP3Service {

    private final AlbumService albumService;

    private final PathService pathService;

    private final MP3Util mp3Util;

    public MP3FolderDTO loadFolder(long albumId) {
        log.info("loadFolder({})", albumId);

        // get the album
        AlbumDTO albumDTO = albumService.findById(albumId);
        log.debug("{}", albumDTO);

        // get all the mp3 on the album folder
        Stream<String> mp3sByAlbum = pathService.mp3sByAlbum(albumDTO);

        // convert and add to the album
        List<MP3DTO> mp3DTOs = new ArrayList<>();
        mp3sByAlbum.forEachOrdered(mp3 -> mp3DTOs.add(convert(mp3)));
        log.debug("for '{}' {} mp3s found", albumDTO, mp3DTOs.size());

        // generate the cover path
        String cover = pathService.coverName(albumDTO);

        return new MP3FolderDTO(albumDTO, mp3DTOs, cover);
    }

    // UTIL

    @SneakyThrows
    private MP3DTO convert(String mp3) {
        Mp3File mp3File = new Mp3File(mp3);

        // debug
        mp3Util.debug(mp3File);

        // add to list
        return convert(mp3File);
    }

    private MP3DTO convert(Mp3File mp3File) {
        MP3DTO mp3DTO = new MP3DTO();
        mp3DTO.setFileName(mp3File.getFilename());
        mp3DTO.setDuration(mp3Util.mmss(mp3File.getLengthInMilliseconds()));
        mp3DTO.setBitrate(mp3File.getBitrate());
        copyProperties(mp3File.getId3v2Tag(), mp3DTO);
        return mp3DTO;
    }

}
