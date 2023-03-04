package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static it.ade.ma.service.path.FileService.*;
import static it.ade.ma.service.path.PathService.generateAlbumName;
import static it.ade.ma.service.path.PathService.normalize;
import static java.lang.String.join;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

@Component
@RequiredArgsConstructor
public class MP3PathService {

    private final PathConfiguration pathConfiguration;

    public String name(AlbumDTO albumDTO) {
        return normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public String nameTmp(AlbumDTO albumDTO) {
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
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3() + "/" + normalize(bandName));
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
            return getFoldersContent(folderName);
        }
        return emptyMap();
    }

    @SneakyThrows
    private Map<String, List<String>> getFoldersContent(String folderName) {
        Map<String, List<String>> map = new LinkedHashMap<>();

        Files.walk(get(folderName))
                // FIXME put - flac and .mp3 in configurations
                .filter(path -> (!path.toString().contains("- flac") && path.toString().endsWith(".mp3")))
                .sorted()
                .forEach(path -> {
                    String cd = "";

                    // get the sub folder (if exists)
                    int folderNamePartsSize = folderName.split("/").length;
                    List<String> dirParts = asList(path.toString().split("/"));
                    if (folderNamePartsSize < dirParts.size() - 1) {
                        cd = join(" - ", dirParts.subList(folderNamePartsSize, dirParts.size() - 1));
                    }

                    // insert the mp3 name grouped by sub folders
                    List<String> files = map.computeIfAbsent(cd, f -> new LinkedList<>());
                    files.add(path.toString());
                });

        return map;
    }

}
