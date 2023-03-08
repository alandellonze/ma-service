package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.MP3DTO;
import it.ade.ma.util.ConverterUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static it.ade.ma.service.mp3.MP3Util.MP3_TAG_FIELDS_TO_BE_CLEARED;
import static it.ade.ma.service.mp3.MP3Util.normalizeTitle;
import static it.ade.ma.service.path.FileService.renameFile;
import static it.ade.ma.service.path.MP3PathService.*;
import static it.ade.ma.util.ReflectionUtil.getValue;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.beans.BeanUtils.copyProperties;

@Slf4j
@Component
@RequiredArgsConstructor
class MP3Normalizer {

    private final ConverterUtil converterUtil;

    MP3DTO checkAndApply(ID3v2 id3v2Template, String mp3, boolean apply) {
        log.info("checkAndApply(id3v2Template, {}, apply: {})", mp3, apply);

        // apply the default changes
        if (apply) {
            mp3 = apply(id3v2Template, mp3);
        }

        // check the file
        return check(id3v2Template, mp3);
    }

    @SneakyThrows
    private MP3DTO check(ID3v2 id3v2Template, String mp3) {
        Mp3File mp3File = new Mp3File(mp3);

        // check id3v2 tag
        MP3DTO mp3DTO = checkId3v2(id3v2Template, mp3File);
        mp3DTO.setIssueId3v2Tag(!mp3File.hasId3v2Tag());

        // check id3v1 tag
        mp3DTO.setIssueId3v1Tag(mp3File.hasId3v1Tag());

        // check custom tag
        mp3DTO.setIssueCustomTag(mp3File.hasCustomTag());

        return mp3DTO;
    }

    private MP3DTO checkId3v2(ID3v2 id3v2Template, Mp3File mp3File) {
        MP3DTO mp3DTO = convert(mp3File);

        // check filename
        mp3DTO.setOkFilename(checkFilename(id3v2Template, mp3File));

        // check title
        mp3DTO.setOkTitle(checkTitle(mp3File));

        // check title and cover
        Optional<byte[]> coverOpt = checkCover(id3v2Template, mp3File);
        mp3DTO.setIssueCover(coverOpt.isPresent());
        coverOpt.ifPresent(mp3DTO::setOriginalCover);

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

        return mp3DTO;
    }

    private MP3DTO convert(Mp3File mp3File) {
        MP3DTO mp3DTO = new MP3DTO();
        mp3DTO.setFileName(mp3File.getFilename());
        mp3DTO.setDuration(converterUtil.mmss(mp3File.getLengthInMilliseconds()));
        mp3DTO.setBitrate(mp3File.getBitrate());
        copyProperties(mp3File.getId3v2Tag(), mp3DTO);
        return mp3DTO;
    }

    private String checkFilename(ID3v2 id3v2Template, Mp3File mp3File) {
        // get values
        String original = extractFilename(mp3File.getFilename());
        String revised = buildFilename(id3v2Template.getTrack(), normalizeTitle(extractTitleFromFilename(mp3File.getFilename())));

        // compare them
        if (Objects.equals(original, revised)) {
            return null;
        } else {
            log.info("to be changed Filename: '{}' => '{}'", original, revised);
            return revised;
        }
    }

    private String checkTitle(Mp3File mp3File) {
        // get values
        String original = mp3File.getId3v2Tag() == null ? null : mp3File.getId3v2Tag().getTitle();
        String revised = normalizeTitle(isNotBlank(original) ? original : extractTitleFromFilename(mp3File.getFilename()));

        // compare them
        if (Objects.equals(original, revised)) {
            return null;
        } else {
            log.info("to be changed Title: '{}' => '{}'", original, revised);
            return revised;
        }
    }

    private Optional<byte[]> checkCover(ID3v2 id3v2Template, Mp3File mp3File) {
        // original file has no cover
        byte[] original = mp3File.getId3v2Tag() == null ? null : mp3File.getId3v2Tag().getAlbumImage();
        if (original == null) {
            return of(new byte[0]);
        }

        // standard cover is not present
        byte[] revised = id3v2Template.getAlbumImage();
        if (revised == null) {
            return of(original);
        }

        // cover is different
        if (original.length != revised.length || !Objects.equals(id3v2Template.getAlbumImageMimeType(), mp3File.getId3v2Tag().getAlbumImageMimeType())) {
            log.info("to be changed AlbumImage: {} ({}) => {} ({})", original.length, mp3File.getId3v2Tag().getAlbumImageMimeType(), revised.length, id3v2Template.getAlbumImageMimeType());
            return of(original);
        }

        // cover is the same
        return empty();
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
                itemsToBeCleared.append(fieldName).append("=").append(value).append(",");
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

    @SneakyThrows
    private String apply(ID3v2 id3v2Template, String mp3) {
        Mp3File mp3File = new Mp3File(mp3);
        boolean isToBeUpdated = false;

        // id3v2 tag
        MP3DTO mp3DTO = checkId3v2(id3v2Template, mp3File);
        if (mp3DTO.haveId3v2TagChanges()) {
            if (mp3File.hasId3v2Tag()) {
                log.info("removeId3v2Tag");
                mp3File.removeId3v2Tag();
            }

            // fill the template with the oks
            id3v2Template.setTitle(mp3DTO.getOkTitle() != null ? mp3DTO.getOkTitle() : mp3DTO.getTitle());

            log.info("setId3v2Tag");
            mp3File.setId3v2Tag(id3v2Template);
            isToBeUpdated = true;
        }

        // id3v1 tag
        if (mp3File.hasId3v1Tag()) {
            log.info("removeId3v1Tag");
            mp3File.removeId3v1Tag();
            isToBeUpdated = true;
        }

        // custom tag
        if (mp3File.hasCustomTag()) {
            log.info("removeCustomTag");
            mp3File.removeCustomTag();
            isToBeUpdated = true;
        }

        // save the file when required
        return isToBeUpdated ? updateMP3File(mp3File, mp3DTO.getOkFilename()) : mp3;
    }

    @SneakyThrows
    private String updateMP3File(Mp3File mp3File, String normalizedFileName) {
        log.info("updateMP3File(mp3File={}, {})", mp3File.getFilename(), normalizedFileName);

        // prepare file names
        String filename = mp3File.getFilename();
        String filenameTmp = filename + "_TMP";
        String normalizedPath = normalizedFileName == null ? null : extractFilePath(filename) + normalizedFileName;

        // save the mp3 and rename the file
        mp3File.save(filenameTmp);
        renameFile(filenameTmp, filename, normalizedPath);

        // return the renamed file name
        return normalizedPath == null ? filename : normalizedPath;
    }

}
