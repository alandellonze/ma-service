package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.MP3DTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static it.ade.ma.service.mp3.MP3Util.extractTitleFromMp3File;
import static it.ade.ma.service.mp3.MP3Util.normalizeTitle;

@Slf4j
@Component
@RequiredArgsConstructor
class MP3Normalizer {

    private final MP3Debugger mp3Debugger;

    public void apply(ID3v2 id3v2Template, int position, Mp3File mp3File, MP3DTO mp3DTO) {
        log.info("apply(id3v2Template, {}, {}, {})", position, mp3File.getFilename(), mp3DTO);

        try {
            // debug mp3File
            mp3Debugger.debugMp3File(mp3File);

            // id3v1 tag
            mp3DTO.setId3v1TagPresent(mp3File.hasId3v1Tag());
            mp3Debugger.debugID3v1(mp3File);
            // FIXME mp3File.removeId3v1Tag();

            // id3v2 tag
            mp3DTO.setId3v2TagPresent(mp3File.hasId3v2Tag());
            mp3Debugger.debugID3v2(mp3File);
            apply(id3v2Template, position, mp3File);
            // FIXME mp3File.removeId3v2Tag();
            // FIXME mp3File.setId3v2Tag(id3v2Template);

            // custom tag
            mp3DTO.setCustomTagPresent(mp3File.hasCustomTag());
            mp3Debugger.debugCustomTag(mp3File);
            // FIXME mp3File.removeCustomTag();

            // FIXME save and normalize file name
            // FIXME updateMP3File(mp3File);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void apply(ID3v2 id3v2Template, int position, Mp3File mp3File) {
        // FIXME apply method
        ID3v2 tag = null;
        if (mp3File.hasId3v2Tag()) {
            tag = mp3File.getId3v2Tag();

            // log changes
            fieldsToBeChanged(tag, id3v2Template);
        }

        // normalize track
        track(position, id3v2Template, tag);

        // normalize title
        title(mp3File, id3v2Template, tag);
    }

    private void fieldsToBeChanged(ID3v2 id3v2Tag, ID3v2 id3v2Template) {
        if (id3v2Tag.getArtist() == null || !id3v2Tag.getArtist().equals(id3v2Template.getArtist())) {
            log.info("Artist to be changed: {} => {}", id3v2Tag.getArtist(), id3v2Template.getArtist());
        }

        if (id3v2Tag.getAlbum() == null || !id3v2Tag.getAlbum().equals(id3v2Template.getAlbum())) {
            log.info("Album to be changed: {} => {}", id3v2Tag.getAlbum(), id3v2Template.getAlbum());
        }

        if (id3v2Tag.getYear() == null || !id3v2Tag.getYear().equals(id3v2Template.getYear())) {
            log.info("Year to be changed: {} => {}", id3v2Tag.getYear(), id3v2Template.getYear());
        }

        if (id3v2Tag.getGenre() != id3v2Template.getGenre()) {
            log.info("Genre to be changed: {} => {}", id3v2Tag.getGenre(), id3v2Template.getGenre());
        }

        if (id3v2Tag.getGenreDescription() == null || !id3v2Tag.getGenreDescription().equals(id3v2Template.getGenreDescription())) {
            log.info("GenreDescription to be changed: {} => {}", id3v2Tag.getGenreDescription(), id3v2Template.getGenreDescription());
        }

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

        if (id3v2Tag.getAlbumArtist() != null) {
            log.info("AlbumArtist to be changed: {} => TO EMPTY", id3v2Tag.getAlbumArtist());
        }

        if (id3v2Tag.getComment() != null) {
            log.info("Comment to be changed: {} => TO EMPTY", id3v2Tag.getComment());
        }

        if (id3v2Tag.getComposer() != null) {
            log.info("Composer to be changed: {} => TO EMPTY", id3v2Tag.getComposer());
        }

        if (id3v2Tag.getCopyright() != null) {
            log.info("Copyright to be changed: {} => TO EMPTY", id3v2Tag.getCopyright());
        }

        if (id3v2Tag.getEncoder() != null) {
            log.info("Encoder to be changed: {} => TO EMPTY", id3v2Tag.getEncoder());
        }

        if (id3v2Tag.getLyrics() != null) {
            log.info("Lyrics to be changed: {} => TO EMPTY", id3v2Tag.getLyrics());
        }

        if (id3v2Tag.getOriginalArtist() != null) {
            log.info("OriginalArtist to be changed: {} => TO EMPTY", id3v2Tag.getOriginalArtist());
        }

        if (id3v2Tag.getPublisher() != null) {
            log.info("Publisher to be changed: {} => TO EMPTY", id3v2Tag.getPublisher());
        }

        if (id3v2Tag.getUrl() != null) {
            log.info("Url to be changed: {} => TO EMPTY", id3v2Tag.getUrl());
        }
    }

    private void track(int position, ID3v2 id3v2Template, ID3v2 id3v2Tag) {
        // get original track
        String originalTrack = id3v2Tag == null ? null : id3v2Tag.getTrack();

        // prepare track
        String track = String.format("%02d", position);

        // log changes
        if (!track.equals(originalTrack)) {
            log.info("Track to be changed: {} => {}", originalTrack, track);
        }

        // set track to template
        id3v2Template.setTrack(track);
    }

    private void title(Mp3File mp3File, ID3v2 id3v2Template, ID3v2 id3v2Tag) {
        // get original title
        String originalTitle = id3v2Tag == null ? null : id3v2Tag.getTitle();

        // get title from original tag or from file name
        String title = StringUtils.isNotBlank(originalTitle) ? originalTitle : extractTitleFromMp3File(mp3File);

        // normalize title
        title = normalizeTitle(title);

        // log changes
        if (!title.equals(originalTitle)) {
            log.info("Title to be changed: {} => {}", originalTitle, title);
        }

        // set title to template
        id3v2Template.setTitle(title);
    }

}
