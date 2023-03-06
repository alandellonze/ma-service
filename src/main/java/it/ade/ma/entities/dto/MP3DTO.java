package it.ade.ma.entities.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MP3DTO {

    public MP3DTO(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;
    private String duration;
    private int bitrate;

    private String artist;
    private String track;
    private String title;
    private String album;
    private String year;
    private int genre;

    private boolean id3v1TagPresent;
    private boolean id3v2TagPresent;
    private boolean customTagPresent;

}
