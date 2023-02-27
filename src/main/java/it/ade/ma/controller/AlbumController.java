package it.ade.ma.controller;

import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody AlbumDTO albumDTO) {
        albumService.save(albumDTO);
        return ok().build();
    }

    @DeleteMapping("/{bandId}/{albumId}")
    public ResponseEntity<Void> delete(@PathVariable long bandId, @PathVariable long albumId) {
        albumService.delete(bandId, albumId);
        return ok().build();
    }

}
