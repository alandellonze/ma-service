package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.entities.dto.MP3FolderDTO;
import it.ade.ma.service.AlbumService;
import it.ade.ma.service.path.CoversPathService;
import it.ade.ma.service.path.MP3PathService;
import it.ade.ma.util.ConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.ade.ma.service.mp3.MP3Util.createID3v2Template;
import static it.ade.ma.service.path.FileService.exist;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toMap;
import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MP3Service {

    private final AlbumService albumService;
    private final MP3PathService mp3PathService;
    private final CoversPathService coversPathService;

    private final MP3Normalizer mp3Normalizer;

    private final ConverterUtil converterUtil;

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

    @SneakyThrows
    private Map<String, List<MP3DTO>> getCDMP3ByAlbum(AlbumDTO albumDTO) {
        // get all the mp3 on the album folders
        Map<String, List<String>> cdMP3Map = mp3PathService.allByAlbum(albumDTO);

        // get cover path
        String coverPath = coversPathService.path(albumDTO);
        byte[] cover = exist(coverPath) ? readAllBytes(get(coverPath)) : null;

        // convert all the cds and mp3s
        return cdMP3Map.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> convert(albumDTO, cover, e.getKey(), e.getValue())));
    }

    private List<MP3DTO> convert(AlbumDTO albumDTO, byte[] cover, String cd, List<String> mp3s) {
        List<MP3DTO> mp3DTOs = new ArrayList<>();

        // create the id3 tag template
        ID3v2 id3v2Template = createID3v2Template(albumDTO, cover, cd);

        // convert all the mp3s
        for (int i = 0; i < mp3s.size(); i++) {
            mp3DTOs.add(convert(id3v2Template, i + 1, mp3s.get(i)));
        }
        log.debug("for '{}' CD '{}' {} mp3s found", albumDTO, cd, mp3DTOs.size());

        return mp3DTOs;
    }

    private MP3DTO convert(ID3v2 id3v2Template, int position, String mp3) {
        log.debug("convert({}, {}, {})", id3v2Template, position, mp3);

        try {
            Mp3File mp3File = new Mp3File(mp3);

            // add to list
            MP3DTO mp3DTO = convert(mp3File);

            // normalizer
            mp3Normalizer.apply(id3v2Template, position, mp3File, mp3DTO);

            return mp3DTO;
        } catch (Exception e) {
            return new MP3DTO(mp3);
        }
    }

    private MP3DTO convert(Mp3File mp3File) {
        MP3DTO mp3DTO = new MP3DTO();
        mp3DTO.setFileName(mp3File.getFilename());
        mp3DTO.setDuration(converterUtil.mmss(mp3File.getLengthInMilliseconds()));
        mp3DTO.setBitrate(mp3File.getBitrate());
        copyProperties(mp3File.getId3v2Tag(), mp3DTO);
        return mp3DTO;
    }

}
