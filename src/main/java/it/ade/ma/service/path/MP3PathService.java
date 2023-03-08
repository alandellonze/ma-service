package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.ade.ma.service.path.FileService.*;
import static it.ade.ma.service.path.PathService.*;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

@Component
@RequiredArgsConstructor
public class MP3PathService {

    private final PathConfiguration pathConfiguration;

    public static String name(AlbumDTO albumDTO) {
        return normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public static String nameTmp(AlbumDTO albumDTO) {
        return normalize(albumDTO.getBandName()) + " - " + generateAlbumName(albumDTO);
    }

    public String path(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getMp3() + name(albumDTO);
    }

    public String pathTmp(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getMp3Tmp() + nameTmp(albumDTO);
    }

    public Stream<String> allByBand(String bandName) {
        return concatAndSort(folderContent(bandName), folderContentTmp(bandName));
    }

    private Stream<String> folderContent(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3() + normalize(bandName));
    }

    private Stream<String> folderContentTmp(String bandName) {
        String normalizeBandName = normalize(bandName) + " - ";
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3Tmp())
                .filter(f -> f.startsWith(normalizeBandName))
                .map(p -> p.substring(normalizeBandName.length()));
    }

    public Map<String, List<String>> allByAlbum(AlbumDTO album) {
        String folderName = path(album);
        if (!exist(folderName)) {
            folderName = pathTmp(album);
        }
        if (exist(folderName)) {
            return getFoldersContent(folderName, MP3_EXCLUDE_LIST, MP3_FILE_EXTENSION);
        }
        return emptyMap();
    }

    public static String extractFilename(String path) {
        String filename = path.substring(path.lastIndexOf("/") + 1);
        return filename.trim();
    }

    public static String extractTitleFromFilename(String path) {
        String filename = extractFilename(path);
        filename = filename.substring(filename.indexOf("-") + 1);
        filename = filename.substring(0, filename.indexOf(MP3_FILE_EXTENSION));
        return filename.trim();
    }

    public static String buildFilename(String track, String title) {
        return format("%s - %s%s", track, title, MP3_FILE_EXTENSION);
    }

}
