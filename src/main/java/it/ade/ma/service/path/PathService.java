package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static it.ade.ma.service.path.FileService.concatStream;
import static it.ade.ma.service.path.FileService.getFolderContent;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class PathService {

    private final PathConfiguration pathConfiguration;

    public String mp3(AlbumDTO album) {
        return pathConfiguration.getRoot() + pathConfiguration.getMp3() + normalize(album.getBandName()) + "/" + generateAlbumName(album);
    }

    private Stream<String> mp3Folder(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3() + "/" + normalize(bandName));
    }

    public String tmpMp3(AlbumDTO album) {
        return pathConfiguration.getRoot() + pathConfiguration.getTmpMp3() + normalize(album.getBandName()) + " - " + generateAlbumName(album);
    }

    private Stream<String> tmpMp3Folder(String bandName) {
        String normalizeBandName = normalize(bandName) + " - ";
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getTmpMp3())
                .filter(f -> f.startsWith(normalizeBandName))
                .map(p -> p.substring(normalizeBandName.length()));
    }

    public List<String> mp3All(String bandName) {
        return concatStream(mp3Folder(bandName), tmpMp3Folder(bandName));
    }

    public String cover(AlbumDTO album) {
        return pathConfiguration.getRoot() + pathConfiguration.getCovers() + normalize(album.getBandName()) + "/" + generateCoverName(album);
    }

    private Stream<String> coversFolder(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getCovers() + "/" + normalize(bandName));
    }

    public List<String> coversAll(String bandName) {
        // TODO looks into covers tmp
        return coversFolder(bandName).sorted().collect(toList());
    }

    public String scan(AlbumDTO album) {
        return pathConfiguration.getRoot() + pathConfiguration.getScans() + normalize(album.getBandName()) + "/" + generateAlbumName(album);
    }

    private Stream<String> scansFolder(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getScans() + "/" + normalize(bandName));
    }

    public List<String> scansAll(String bandName) {
        // TODO looks into scans tmp
        return scansFolder(bandName).sorted().collect(toList());
    }

    private static String normalize(String name) {
        return name.replace(":", " -").replace("/", "-");
    }

    public static String generateAlbumName(AlbumDTO album) {
        // add album type
        String type = album.getMaType() != null ? album.getMaType() : album.getType();
        type = (type == null || "FULLLENGTH".equals(type)) ? "" : type;
        StringBuilder folderName = new StringBuilder(type);

        // add album typeCount
        Integer typeCount = album.getMaTypeCount() != null ? album.getMaTypeCount() : album.getTypeCount();
        folderName.append(format("%02d", typeCount));

        // add name
        String name = album.getMaName() != null ? album.getMaName() : album.getName();
        folderName.append(" - ").append(normalize(name));

        return folderName.toString();
    }

    public static String generateCoverName(AlbumDTO album) {
        return generateAlbumName(album) + ".jpg";
    }

}
