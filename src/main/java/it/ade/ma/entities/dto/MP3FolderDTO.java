package it.ade.ma.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MP3FolderDTO {

    private AlbumDTO album;
    private List<MP3DTO> mp3s;
    private String cover;

}
