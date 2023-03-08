package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.entities.dto.MP3FolderDTO;
import it.ade.ma.service.AlbumService;
import it.ade.ma.service.path.CoversPathService;
import it.ade.ma.service.path.MP3PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static it.ade.ma.service.mp3.MP3Util.createID3v2Template;
import static it.ade.ma.service.path.CoversPathService.name;
import static it.ade.ma.service.path.FileService.loadFile;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

@Slf4j
@Service
@RequiredArgsConstructor
public class MP3Service {

    private final AlbumService albumService;
    private final MP3PathService mp3PathService;
    private final CoversPathService coversPathService;

    private final MP3Normalizer mp3Normalizer;

    public MP3FolderDTO loadFolder(long albumId, boolean apply) {
        log.info("loadFolder({}, apply: {})", albumId, apply);

        // get the album
        AlbumDTO albumDTO = albumService.findById(albumId);
        log.debug("{}", albumDTO);

        // generate the cover path
        String cover = name(albumDTO);

        // get all the mp3 on the album folders
        Map<String, List<MP3DTO>> cdMP3Map = getCDMP3ByAlbum(albumDTO, apply);

        return new MP3FolderDTO(albumDTO, cover, cdMP3Map);
    }

    private Map<String, List<MP3DTO>> getCDMP3ByAlbum(AlbumDTO albumDTO, boolean apply) {
        // get cover path
        byte[] cover = loadFile(coversPathService.path(albumDTO));

        // convert all the cds and mp3s
        return mp3PathService.allByAlbum(albumDTO).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> convertAll(albumDTO, cover, e.getKey(), e.getValue(), apply)));
    }

    private List<MP3DTO> convertAll(AlbumDTO albumDTO, byte[] cover, String cd, List<String> mp3s, boolean apply) {
        // create the id3 tag template
        ID3v2 id3v2Template = createID3v2Template(albumDTO, cover, cd);

        // convert all the mp3s
        return range(0, mp3s.size())
                .mapToObj(i -> convert(id3v2Template, i + 1, mp3s.get(i), apply))
                .collect(toList());
    }

    private MP3DTO convert(ID3v2 id3v2Template, int i, String mp3, boolean apply) {
        try {
            // set the track
            id3v2Template.setTrack(format("%02d", i));

            // normalize
            return mp3Normalizer.checkAndApply(id3v2Template, mp3, apply);
        } catch (Exception e) {
            return new MP3DTO(mp3);
        }
    }

}
