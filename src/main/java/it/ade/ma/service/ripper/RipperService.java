package it.ade.ma.service.ripper;

import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.service.ripper.model.WebPageAlbum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;

import static it.ade.ma.configuration.CacheConfiguration.CACHE_NAME;
import static it.ade.ma.service.ripper.AlbumTypeService.calculateCount;
import static it.ade.ma.service.ripper.AlbumTypeService.normalize;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

@Slf4j
@Component
@RequiredArgsConstructor
public class RipperService {

    private final WebPageContentService webPageContentService;

    @Cacheable(CACHE_NAME)
    public List<AlbumDTO> execute(long bandId, Long bandMAKey) {
        log.info("execute({}, {})", bandId, bandMAKey);

        // empty list when the MA key is not present
        if (isNull(bandMAKey)) {
            return emptyList();
        }

        // retrieve the album list from the web page
        List<WebPageAlbum> webPageAlbums = webPageContentService.parsePage(bandMAKey);

        // convert WebPageAlbum into Album
        Map<String, Integer> typeCounts = new HashMap<>();
        List<AlbumDTO> albums = new ArrayList<>();
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
            albums.add(new AlbumDTO(bandId, i + 1, type, typeCount, name, year));
        }

        return albums;
    }

}
