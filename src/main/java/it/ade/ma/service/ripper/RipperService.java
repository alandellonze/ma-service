package it.ade.ma.service.ripper;

import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.service.ripper.model.WebPageAlbum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static it.ade.ma.service.ripper.AlbumTypeService.calculateCount;
import static it.ade.ma.service.ripper.AlbumTypeService.normalize;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

@Slf4j
@Component
@RequiredArgsConstructor
public class RipperService {

    private final WebPageContentService webPageContentService;

    public List<AlbumDTO> execute(long bandMAKey) {
        log.info("execute({})", bandMAKey);

        List<AlbumDTO> albums = new ArrayList<>();

        // retrieve the album list from the web page
        List<WebPageAlbum> webPageAlbums = webPageContentService.parsePage(bandMAKey);

        // keep the types count
        Map<String, Integer> typeCounts = new HashMap<>();

        // convert WebPageAlbum into Album
        for (int i = 0; i < webPageAlbums.size(); i++) {
            WebPageAlbum webPageAlbum = webPageAlbums.get(i);

            // normalize type
            Optional<String> typeOpt = normalize(webPageAlbum.getType());
            String type = typeOpt.orElseGet(() -> {
                log.error("album type normalization not found: " + webPageAlbum.getType());
                return "<" + webPageAlbum.getType() + ">";
            });

            // assign type count
            Integer typeCount = calculateCount(typeCounts, type);

            // capitalize name
            String name = capitalize(webPageAlbum.getName());

            // convert year
            Integer year = Integer.parseInt(webPageAlbum.getYear());

            // add to the list
            albums.add(new AlbumDTO(i + 1, type, typeCount, name, year));
        }

        return albums;
    }

}
