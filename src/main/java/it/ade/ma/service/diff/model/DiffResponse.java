package it.ade.ma.service.diff.model;

import it.ade.ma.entities.dto.AlbumDTO;
import lombok.Data;

@Data
public class DiffResponse {

    private DiffResult<AlbumDTO> albums;
    private DiffResult<String> mp3;
    private DiffResult<String> covers;
    private DiffResult<String> scans;

}
