package it.ade.ma.controller;

import it.ade.ma.entities.dto.MP3FolderDTO;
import it.ade.ma.service.mp3.MP3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mp3s")
public class MP3Controller {

    private final MP3Service mp3Service;

    @GetMapping("/{albumId}")
    public ResponseEntity<MP3FolderDTO> loadFolder(@PathVariable long albumId) {
        return ok().body(mp3Service.loadFolder(albumId, false));
    }

    @PostMapping("/{albumId}")
    public ResponseEntity<MP3FolderDTO> applyChanges(@PathVariable long albumId) {
        return ok().body(mp3Service.loadFolder(albumId, true));
    }

}
