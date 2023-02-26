package it.ade.ma.controller;

import it.ade.ma.entities.Band;
import it.ade.ma.repository.BandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bands")
public class BandController {

    private final BandRepository bandRepository;

    @GetMapping
    public ResponseEntity<List<Band>> findAll(@RequestParam(required = false) String name) {
        return ok().body(bandRepository.findAll(name));
    }

}
