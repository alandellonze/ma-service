package it.ade.ma.service.diff;

import it.ade.ma.entities.Band;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.repository.BandRepository;
import it.ade.ma.service.AlbumService;
import it.ade.ma.service.diff.model.DiffResponse;
import it.ade.ma.service.diff.model.DiffResult;
import it.ade.ma.service.path.PathService;
import it.ade.ma.service.ripper.RipperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static it.ade.ma.entities.Status.*;
import static it.ade.ma.service.path.FileService.exist;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiffService {

    private final BandRepository bandRepository;

    private final AlbumService albumService;
    private final RipperService ripperService;
    private final AlbumDiffService albumDiffService;
    private final StringDiffService stringDiffService;
    private final PathService pathService;

    public DiffResponse diff(long bandId) {
        log.info("diff({})", bandId);

        // get Band from db
        Optional<Band> bandOpt = bandRepository.findById(bandId);
        if (bandOpt.isEmpty()) {
            log.info("band with id: {} wasn't found", bandId);
            return null;
        }
        Band band = bandOpt.get();

        // get Albums from db
        List<AlbumDTO> albums = albumService.findAllByBandId(band.getId());
        log.info("for band: {} {} albums found", band.getName(), albums.size());

        // check the presence of the files (mp3, covers and scans)
        checkFilesPresence(albums);

        // build response
        return buildResponse(band, albums);
    }

    private void checkFilesPresence(List<AlbumDTO> albums) {
        log.info("checkFilesPresence({})", albums.size());

        albums.forEach(album -> {
            // mp3 + tmp
            String path = pathService.mp3(album);
            if (exist(path)) {
                album.setStatusMP3(PRESENT);
            } else {
                path = pathService.tmpMp3(album);
                album.setStatusMP3(exist(path) ? TMP : NOT_PRESENT);
            }

            // cover + tmp
            path = pathService.cover(album);
            if (exist(path)) {
                album.setStatusCover(PRESENT);
            } else {
                // TODO looks into covers tmp
                album.setStatusCover(NOT_PRESENT);
            }

            // scans
            path = pathService.scan(album);
            if (exist(path)) {
                album.setStatusScans(PRESENT);
            } else {
                // TODO looks into scans tmp
                album.setStatusScans(NOT_PRESENT);
            }
        });
    }

    private DiffResponse buildResponse(Band band, List<AlbumDTO> albums) {
        DiffResponse diffResponse = new DiffResponse();

        // diffs from web
        diffResponse.setAlbums(diffWeb(band.getMaKey(), albums));

        // diffs mp3
        diffResponse.setMp3(diffMP3(band.getName(), albums));

        // diffs covers
        diffResponse.setCovers(diffCovers(band.getName(), albums));

        // diffs scans
        diffResponse.setScans(diffScans(band.getName(), albums));

        return diffResponse;
    }

    private DiffResult<AlbumDTO> diffWeb(Long maKey, List<AlbumDTO> albums) {
        log.info("diffWeb({}, {})", maKey, albums.size());

        // get album from web
        List<AlbumDTO> albumsFromWeb = isNull(maKey) ? emptyList() : ripperService.execute(maKey);

        // diff album
        return albumDiffService.execute(albums, albumsFromWeb);
    }

    private DiffResult<String> diffMP3(String bandName, List<AlbumDTO> albums) {
        log.info("diffMP3({}, {})", bandName, albums.size());

        // convert album to mp3 names
        List<String> albumsToString = albums.stream()
                .map(PathService::generateAlbumName)
                .sorted().collect(toList());

        // get mp3 list from disk
        List<String> mp3s = pathService.mp3All(bandName);

        // diff mp3
        return stringDiffService.execute(albumsToString, mp3s);
    }

    private DiffResult<String> diffCovers(String bandName, List<AlbumDTO> albums) {
        log.info("diffCovers({}, {})", bandName, albums.size());

        // convert album to cover names
        List<String> albumsToString = albums.stream()
                .map(PathService::generateCoverName)
                .sorted().collect(toList());

        // get cover files from disk
        List<String> covers = pathService.coversAll(bandName);

        // diff covers
        return stringDiffService.execute(albumsToString, covers);
    }

    private DiffResult<String> diffScans(String bandName, List<AlbumDTO> albums) {
        log.info("diffScans({}, {})", bandName, albums.size());

        // convert album to scans names
        List<String> albumsToString = albums.stream()
                .map(PathService::generateAlbumName)
                .sorted()
                .collect(toList());

        // get scan files from disk
        List<String> scans = pathService.scansAll(bandName);

        // diff scans
        return stringDiffService.execute(albumsToString, scans);
    }

}
