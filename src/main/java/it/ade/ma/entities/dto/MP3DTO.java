package it.ade.ma.entities.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.util.StringUtils.*;

@Data
@NoArgsConstructor
public class MP3DTO {

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

    private boolean issueCover;
    private byte[] originalCover;

    private String okFilename;
    private String okArtist;
    private String okTrack;
    private String okTitle;
    private String okAlbum;
    private String okYear;
    private Integer okGenre;
    private String okGenreDescription;
    private String itemsToBeCleared;

    public MP3DTO(String fileName) {
        this.fileName = fileName;
    }

    public boolean haveId3v2TagChanges() {
        return issueCover
                || okFilename != null
                || okArtist != null
                || okTrack != null
                || okTitle != null
                || okAlbum != null
                || okYear != null
                || okGenre != null
                || okGenreDescription != null
                || hasLength(itemsToBeCleared);
    }

}
