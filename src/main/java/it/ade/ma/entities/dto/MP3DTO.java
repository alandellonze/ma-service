package it.ade.ma.entities.dto;

import lombok.Data;

@Data
public class MP3DTO {

    private String fileName;
    private String duration;
    private int bitrate;

    private String artist;
    private String track;
    private String title;
    private String album;
    private String year;
    private int genre;

}
