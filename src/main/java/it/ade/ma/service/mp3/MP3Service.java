package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.entities.dto.MP3FolderDTO;
import it.ade.ma.service.AlbumService;
import it.ade.ma.service.path.CoversPathService;
import it.ade.ma.service.path.MP3PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MP3Service {

    private final AlbumService albumService;
    private final MP3PathService mp3PathService;
    private final CoversPathService coversPathService;

    private final MP3Util mp3Util;

    public MP3FolderDTO loadFolder(long albumId) {
        log.info("loadFolder({})", albumId);

        // get the album
        AlbumDTO albumDTO = albumService.findById(albumId);
        log.debug("{}", albumDTO);

        // generate the cover path
        String cover = coversPathService.name(albumDTO);

        // get all the mp3 on the album folders
        Map<String, List<MP3DTO>> cdMP3Map = getCDMP3ByAlbum(albumDTO);

        return new MP3FolderDTO(albumDTO, cover, cdMP3Map);
    }

    private Map<String, List<MP3DTO>> getCDMP3ByAlbum(AlbumDTO albumDTO) {
        // get all the mp3 on the album folders
        Map<String, List<String>> cdMP3Map = mp3PathService.allByAlbum(albumDTO);

        Map<String, List<MP3DTO>> cdMP3DTOMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> cdMP3Entry : cdMP3Map.entrySet()) {
            String cd = cdMP3Entry.getKey();
            List<String> mp3s = cdMP3Entry.getValue();

            // convert
            List<MP3DTO> mp3DTOs = new ArrayList<>();
            mp3s.forEach(mp3 -> mp3DTOs.add(convert(mp3)));
            log.debug("for '{}' CD '{}' {} mp3s found", albumDTO, cd, mp3DTOs.size());

            cdMP3DTOMap.put(cd, mp3DTOs);
        }

        return cdMP3DTOMap;
    }

    private MP3DTO convert(String mp3) {
        log.debug("convert({})", mp3);

        try {
            Mp3File mp3File = new Mp3File(mp3);

            // FIXME normalizer
            // mp3Util.debug(mp3File);
            // mp3Normalizer.check(cdMP3Map, albumDTO);

            // add to list
            return convert(mp3File);
        } catch (Exception e) {
            return new MP3DTO(mp3);
        }
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
