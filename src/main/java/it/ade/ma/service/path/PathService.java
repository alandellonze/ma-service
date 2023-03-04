package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static it.ade.ma.service.path.FileService.concatAndSort;
import static it.ade.ma.service.path.FileService.getFolderContent;
import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class PathService {

    private final PathConfiguration pathConfiguration;

    public String mp3Path(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getMp3() + normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public String mp3TmpPath(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getMp3Tmp() + normalize(albumDTO.getBandName()) + " - " + generateAlbumName(albumDTO);
    }

    public Stream<String> mp3sByBand(String bandName) {
        return concatAndSort(mp3FolderContent(bandName), mp3TmpFolderContent(bandName));
    }

    private Stream<String> mp3FolderContent(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3() + "/" + normalize(bandName));
    }

    private Stream<String> mp3TmpFolderContent(String bandName) {
        String normalizeBandName = normalize(bandName) + " - ";
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getMp3Tmp())
                .filter(f -> f.startsWith(normalizeBandName))
                .map(p -> p.substring(normalizeBandName.length()));
    }

    public Stream<String> mp3sByAlbum(AlbumDTO albumDTO) {
        return concatAndSort(getFolderContent(mp3Path(albumDTO), false), getFolderContent(mp3TmpPath(albumDTO), false));
    }

    public String coverName(AlbumDTO albumDTO) {
        return normalize(albumDTO.getBandName()) + "/" + generateCoverName(albumDTO);
    }

    public String coverPath(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getCovers() + coverName(albumDTO);
    }

    public String coverTmpPath(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getCoversTmp() + coverName(albumDTO);
    }

    public Stream<String> coversByBand(String bandName) {
        // TODO looks into covers tmp
        return coversFolderContent(bandName).sorted();
    }

    private Stream<String> coversFolderContent(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getCovers() + "/" + normalize(bandName));
    }

    public String scansPath(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getScans() + normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public String scansTmpPath(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getScansTmp() + normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public Stream<String> scansByBand(String bandName) {
        // TODO looks into scans tmp
        return scansFolderContent(bandName).sorted();
    }

    private Stream<String> scansFolderContent(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getScans() + "/" + normalize(bandName));
    }

    private static String normalize(String name) {
        return name.replace(":", " -").replace("/", "-");
    }

    public static String generateAlbumName(AlbumDTO albumDTO) {
        // add album type
        String type = albumDTO.getMaType() != null ? albumDTO.getMaType() : albumDTO.getType();
        type = (type == null || "FULLLENGTH".equals(type)) ? "" : type;
        StringBuilder folderName = new StringBuilder(type);

        // add album typeCount
        Integer typeCount = albumDTO.getMaTypeCount() != null ? albumDTO.getMaTypeCount() : albumDTO.getTypeCount();
        folderName.append(format("%02d", typeCount));

        // add name
        String name = albumDTO.getMaName() != null ? albumDTO.getMaName() : albumDTO.getName();
        folderName.append(" - ").append(normalize(name));

        return folderName.toString();
    }

    public static String generateCoverName(AlbumDTO albumDTO) {
        return generateAlbumName(albumDTO) + ".jpg";
    }

}
