package it.ade.ma.controller;

import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.service.mp3.MP3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mp3s")
public class MP3Controller {

    private final MP3Service mp3Service;

    @GetMapping("/{albumId}")
    public ResponseEntity<List<MP3DTO>> findAll(@PathVariable long albumId) {
        return ok().body(mp3Service.findAll(albumId));
    }

}