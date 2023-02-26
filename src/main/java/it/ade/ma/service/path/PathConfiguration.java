package it.ade.ma.service.path;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ma.path")
public class PathConfiguration {

    private String root;

    private String covers;
    private String mp3;
    private String scans;

    private String tmpCovers;
    private String tmpMp3;
    private String tmpScans;

}
