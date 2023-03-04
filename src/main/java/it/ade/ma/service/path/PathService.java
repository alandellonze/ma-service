package it.ade.ma.service.path;

import it.ade.ma.entities.dto.AlbumDTO;

import static java.lang.String.format;

public interface PathService {

    static String normalize(String name) {
        return name.replace(":", " -").replace("/", "-");
    }

    static String generateAlbumName(AlbumDTO albumDTO) {
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

    static String generateCoverName(AlbumDTO albumDTO) {
        return generateAlbumName(albumDTO) + ".jpg";
    }

}
