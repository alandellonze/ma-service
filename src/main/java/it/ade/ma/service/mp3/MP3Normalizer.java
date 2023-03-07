package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.MP3DTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static it.ade.ma.service.mp3.MP3Util.*;
import static it.ade.ma.util.ReflectionUtil.getValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
class MP3Normalizer {

    private final MP3Debugger mp3Debugger;

    public void check(ID3v2 id3v2Template, Mp3File mp3File, MP3DTO mp3DTO) {
        log.info("check(id3v2Template, mp3File, {})", mp3DTO);

        try {
            // debug mp3File
            // mp3Debugger.debugMp3File(mp3File);

            // id3v1 tag
            // mp3Debugger.debugID3v1(mp3File);
            mp3DTO.setIssueId3v1Tag(mp3File.hasId3v1Tag());
            // FIXME mp3File.removeId3v1Tag();

            // id3v2 tag
            // mp3Debugger.debugID3v2(mp3File);
            mp3DTO.setIssueId3v2Tag(mp3File.hasId3v2Tag());
            checkId3v2(id3v2Template, mp3File, mp3DTO);
            // FIXME mp3File.removeId3v2Tag();
            // FIXME mp3File.setId3v2Tag(id3v2Template);

            // custom tag
            // mp3Debugger.debugCustomTag(mp3File);
            mp3DTO.setIssueCustomTag(mp3File.hasCustomTag());
            // FIXME mp3File.removeCustomTag();

            // FIXME save and normalize file name
            // FIXME updateMP3File(mp3File);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkId3v2(ID3v2 id3v2Template, Mp3File mp3File, MP3DTO mp3DTO) {
        // check title and cover
        mp3DTO.setOkTitle(checkTitle(mp3File));
        // FIXME checkCover
        checkCover(id3v2Template, mp3File.getId3v2Tag());

        // check main fields
        mp3DTO.setOkArtist((String) checkItem(id3v2Template, mp3File, "artist"));
        mp3DTO.setOkTrack((String) checkItem(id3v2Template, mp3File, "track"));
        mp3DTO.setOkAlbum((String) checkItem(id3v2Template, mp3File, "album"));
        mp3DTO.setOkYear((String) checkItem(id3v2Template, mp3File, "year"));
        mp3DTO.setOkGenre((Integer) checkItem(id3v2Template, mp3File, "genre"));
        mp3DTO.setOkGenreDescription((String) checkItem(id3v2Template, mp3File, "genreDescription"));

        // check other fields
        if (mp3File.hasId3v2Tag()) {
            mp3DTO.setItemsToBeCleared(checkItemsToBeCleared(mp3File));
        }
    }

    private String checkTitle(Mp3File mp3File) {
        // format titles
        String originalTitle = mp3File.getId3v2Tag() == null ? null : mp3File.getId3v2Tag().getTitle();
        String title = normalizeTitle(isNotBlank(originalTitle) ? originalTitle : extractTitleFromMp3File(mp3File));

        // compare them
        if (Objects.equals(originalTitle, title)) {
            return null;
        } else {
            log.info("to be changed Title: '{}' => '{}'", originalTitle, title);
            return title;
        }
    }

    private void checkCover(ID3v2 id3v2Template, ID3v2 id3v2Tag) {
        // FIXME
        byte[] albumImageData = id3v2Tag.getAlbumImage();
        byte[] albumImageDataTemplate = id3v2Template.getAlbumImage();
        if (albumImageData != null) {
            if (albumImageDataTemplate != null) {
                if (albumImageData.length != albumImageDataTemplate.length || !id3v2Tag.getAlbumImageMimeType().equals(id3v2Template.getAlbumImageMimeType())) {
                    log.info("AlbumImage to be changed: {}, {} => {}, {}", id3v2Tag.getAlbumImage().length, id3v2Tag.getAlbumImageMimeType(), id3v2Template.getAlbumImage().length, id3v2Template.getAlbumImageMimeType());
                }
            }
        } else {
            if (albumImageDataTemplate != null) {
                log.info("AlbumImage to be set: {}, {}", id3v2Template.getAlbumImage().length, id3v2Template.getAlbumImageMimeType());
            }
        }
    }

    private Object checkItem(ID3v2 id3v2Template, Mp3File mp3File, String fieldName) {
        // get values
        Object original = mp3File.getId3v2Tag() == null ? null : getValue(mp3File.getId3v2Tag(), fieldName);
        Object revised = getValue(id3v2Template, fieldName);

        // compare them
        if (Objects.equals(original, revised)) {
            return null;
        } else {
            log.info("to be changed {}: '{}' => '{}'", fieldName, original, revised);
            return revised;
        }
    }

    private String checkItemsToBeCleared(Mp3File mp3File) {
        StringBuilder itemsToBeCleared = new StringBuilder();
        MP3_TAG_FIELDS_TO_BE_CLEARED.forEach(fieldName -> {
            Object value = checkItemToBeCleared(mp3File, fieldName);
            if (value != null) {
                itemsToBeCleared.append(fieldName).append("=").append(value).append("<br/>");
            }
        });
        return itemsToBeCleared.toString();
    }

    private Object checkItemToBeCleared(Mp3File mp3File, String fieldName) {
        // get values
        Object original = mp3File.getId3v2Tag() == null ? null : getValue(mp3File.getId3v2Tag(), fieldName);

        // compare them
        if (original == null) {
            return null;
        } else {
            log.info("to be changed {}: '{}' => TO EMPTY", fieldName, original);
            return original;
        }
    }

}
