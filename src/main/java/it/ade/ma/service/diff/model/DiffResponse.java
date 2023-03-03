package it.ade.ma.service.diff.model;

import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.entities.dto.ItemDiffDTO;
import lombok.Data;

@Data
public class DiffResponse {

    private DiffResult<AlbumDTO> albums;
    private DiffResult<ItemDiffDTO> mp3;
    private DiffResult<ItemDiffDTO> covers;
    private DiffResult<ItemDiffDTO> scans;

}
