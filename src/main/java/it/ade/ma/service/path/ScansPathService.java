package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static it.ade.ma.service.path.FileService.getFolderContent;
import static it.ade.ma.service.path.PathService.generateAlbumName;
import static it.ade.ma.service.path.PathService.normalize;

@Component
@RequiredArgsConstructor
public class ScansPathService {

    private final PathConfiguration pathConfiguration;

    public String name(AlbumDTO albumDTO) {
        return normalize(albumDTO.getBandName()) + "/" + generateAlbumName(albumDTO);
    }

    public String nameTmp(AlbumDTO albumDTO) {
        return name(albumDTO);
    }

    public String path(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getScans() + name(albumDTO);
    }

    public String pathTmp(AlbumDTO albumDTO) {
        return pathConfiguration.getRoot() + pathConfiguration.getScansTmp() + nameTmp(albumDTO);
    }

    public Stream<String> allByBand(String bandName) {
        // TODO looks into tmp
        return folderContent(bandName).sorted();
    }

    private Stream<String> folderContent(String bandName) {
        return getFolderContent(pathConfiguration.getRoot() + pathConfiguration.getScans() + "/" + normalize(bandName));
    }

}
