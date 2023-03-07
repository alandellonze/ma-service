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
    private Integer genre;
    private String genreDescription;

    private boolean issueId3v1Tag;
    private boolean issueId3v2Tag;
    private boolean issueCustomTag;

    private String okArtist;
    private String okTrack;
    private String okTitle;
    private String okAlbum;
    private String okYear;
    private Integer okGenre;
    private String okGenreDescription;
    private String itemsToBeCleared;

}
