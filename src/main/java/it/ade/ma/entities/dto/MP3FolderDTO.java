package it.ade.ma.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MP3FolderDTO {

    private AlbumDTO album;
    private String cover;
    private Map<String, List<MP3DTO>> cdMP3Map;

}
