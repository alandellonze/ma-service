package it.ade.ma.controller;

import it.ade.ma.service.diff.DiffService;
import it.ade.ma.service.diff.model.DiffResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diff")
public class DiffController {

    private final DiffService diffService;

    @GetMapping("/{bandId}")
    public ResponseEntity<DiffResponse> findDiff(@PathVariable long bandId) {
        return ok().body(diffService.diff(bandId));
    }

}
