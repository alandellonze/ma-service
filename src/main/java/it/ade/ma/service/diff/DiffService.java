package it.ade.ma.service.diff;

import it.ade.ma.entities.Band;
import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.ItemDiffDTO;
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
import static it.ade.ma.service.path.PathService.generateAlbumName;
import static it.ade.ma.service.path.PathService.generateCoverName;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiffService {

    private final BandRepository bandRepository;

    private final AlbumService albumService;
    private final RipperService ripperService;
    private final AlbumDiffService albumDiffService;
    private final ItemDiffService itemDiffService;
    private final PathService pathService;

    public DiffResponse getDiff(long bandId) {
        log.info("getDiff({})", bandId);

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
            String path = pathService.mp3Path(album);
            if (exist(path)) {
                album.setStatusMP3(PRESENT);
            } else {
                path = pathService.mp3TmpPath(album);
                album.setStatusMP3(exist(path) ? TMP : NOT_PRESENT);
            }

            // cover + tmp
            path = pathService.coverPath(album);
            if (exist(path)) {
                album.setStatusCover(PRESENT);
            } else {
                path = pathService.coverTmpPath(album);
                album.setStatusCover(exist(path) ? TMP : NOT_PRESENT);
            }

            // scans
            path = pathService.scansPath(album);
            if (exist(path)) {
                album.setStatusScans(PRESENT);
            } else {
                path = pathService.scansTmpPath(album);
                album.setStatusScans(exist(path) ? TMP : NOT_PRESENT);
            }
        });
    }

    private DiffResponse buildResponse(Band band, List<AlbumDTO> albums) {
        DiffResponse diffResponse = new DiffResponse();

        // diffs from web
        diffResponse.setAlbums(diffWeb(band, albums));

        // diffs mp3
        diffResponse.setMp3(diffMP3(band.getName(), albums));

        // diffs covers
        diffResponse.setCovers(diffCovers(band.getName(), albums));

        // diffs scans
        diffResponse.setScans(diffScans(band.getName(), albums));

        return diffResponse;
    }

    private DiffResult<AlbumDTO> diffWeb(Band band, List<AlbumDTO> albums) {
        log.info("diffWeb({}, {})", band, albums.size());

        // get album from web
        List<AlbumDTO> albumsFromWeb = ripperService.execute(band.getId(), band.getMaKey());

        // diff album
        return albumDiffService.execute(albums, albumsFromWeb);
    }

    private DiffResult<ItemDiffDTO> diffMP3(String bandName, List<AlbumDTO> albums) {
        log.info("diffMP3({}, {})", bandName, albums.size());

        // convert album to mp3 names
        List<ItemDiffDTO> albumsToString = albums.stream()
                .map(a -> new ItemDiffDTO(a.getId(), generateAlbumName(a)))
                .sorted().collect(toList());

        // get mp3 list from disk
        List<ItemDiffDTO> mp3s = pathService.mp3sByBand(bandName)
                .map(a -> new ItemDiffDTO(null, a))
                .collect(toList());

        // diff mp3
        return itemDiffService.execute(albumsToString, mp3s);
    }

    private DiffResult<ItemDiffDTO> diffCovers(String bandName, List<AlbumDTO> albums) {
        log.info("diffCovers({}, {})", bandName, albums.size());

        // convert album to cover names
        List<ItemDiffDTO> albumsToString = albums.stream()
                .map(a -> new ItemDiffDTO(a.getId(), generateCoverName(a)))
                .sorted().collect(toList());

        // get cover files from disk
        List<ItemDiffDTO> covers = pathService.coversByBand(bandName)
                .map(a -> new ItemDiffDTO(null, a))
                .collect(toList());

        // diff covers
        return itemDiffService.execute(albumsToString, covers);
    }

    private DiffResult<ItemDiffDTO> diffScans(String bandName, List<AlbumDTO> albums) {
        log.info("diffScans({}, {})", bandName, albums.size());

        // convert album to scans names
        List<ItemDiffDTO> albumsToString = albums.stream()
                .map(a -> new ItemDiffDTO(a.getId(), generateAlbumName(a)))
                .sorted().collect(toList());

        // get scan files from disk
        List<ItemDiffDTO> scans = pathService.scansByBand(bandName)
                .map(a -> new ItemDiffDTO(null, a))
                .collect(toList());

        // diff scans
        return itemDiffService.execute(albumsToString, scans);
    }

}
