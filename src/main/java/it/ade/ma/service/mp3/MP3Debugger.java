package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class MP3Debugger {

    void debugMp3File(Mp3File mp3File) {
        log.debug("Filename: {}", mp3File.getFilename());
        log.debug("LengthInSeconds: {}", mp3File.getLengthInSeconds());
        log.debug("Bitrate: {}", mp3File.getBitrate());
        log.debug("Version: {}", mp3File.getVersion());
    }

    void debugID3v1(Mp3File mp3File) {
        debugID3v1(mp3File, "v1");
    }

    private void debugID3v1(Mp3File mp3File, String prefix) {
        if (!mp3File.hasId3v1Tag()) {
            return;
        }

        ID3v1 tag = mp3File.getId3v1Tag();
        log.debug("{} Artist: {}", prefix, tag.getArtist());
        log.debug("{} Track: {}", prefix, tag.getTrack());
        log.debug("{} Title: {}", prefix, tag.getTitle());
        log.debug("{} Album: {}", prefix, tag.getAlbum());
        log.debug("{} Year: {}", prefix, tag.getYear());
        log.debug("{} Genre: {} ({})", prefix, tag.getGenre(), tag.getGenreDescription());
        log.debug("{} Comment: {}", prefix, tag.getComment());
        log.debug("{} Version: {}", prefix, tag.getVersion());
    }

    void debugID3v2(Mp3File mp3File) {
        if (!mp3File.hasId3v2Tag()) {
            return;
        }

        String prefix = "v2";
        debugID3v1(mp3File, prefix);

        ID3v2 tag = mp3File.getId3v2Tag();
        log.debug("{} Padding: {}", prefix, tag.getPadding());
        log.debug("{} Footer {}", prefix, tag.hasFooter());
        log.debug("{} Unsynchronisation {}", prefix, tag.hasUnsynchronisation());
        log.debug("{} BPM {}", prefix, tag.getBPM());
        log.debug("{} Grouping {}", prefix, tag.getGrouping());
        log.debug("{} Key {}", prefix, tag.getKey());
        log.debug("{} Date {}", prefix, tag.getDate());
        log.debug("{} Composer {}", prefix, tag.getComposer());
        log.debug("{} Publisher {}", prefix, tag.getPublisher());
        log.debug("{} OriginalArtist {}", prefix, tag.getOriginalArtist());
        log.debug("{} AlbumArtist {}", prefix, tag.getAlbumArtist());
        log.debug("{} Copyright {}", prefix, tag.getCopyright());
        log.debug("{} ArtistUrl {}", prefix, tag.getArtistUrl());
        log.debug("{} CommercialUrl {}", prefix, tag.getCommercialUrl());
        log.debug("{} CopyrightUrl {}", prefix, tag.getCopyrightUrl());
        log.debug("{} AudiofileUrl {}", prefix, tag.getAudiofileUrl());
        log.debug("{} AudioSourceUrl {}", prefix, tag.getAudioSourceUrl());
        log.debug("{} RadiostationUrl {}", prefix, tag.getRadiostationUrl());
        log.debug("{} PaymentUrl {}", prefix, tag.getPaymentUrl());
        log.debug("{} PublisherUrl {}", prefix, tag.getPublisherUrl());
        log.debug("{} Url {}", prefix, tag.getUrl());
        log.debug("{} PartOfSet {}", prefix, tag.getPartOfSet());
        log.debug("{} Compilation {}", prefix, tag.isCompilation());
        log.debug("{} Chapters {}", prefix, tag.getChapters());
        log.debug("{} ChapterTOC {}", prefix, tag.getChapterTOC());
        log.debug("{} Encoder {}", prefix, tag.getEncoder());
        log.debug("{} AlbumImage {}", prefix, tag.getAlbumImage() == null ? null : tag.getAlbumImage().length);
        log.debug("{} AlbumImageMimeType {}", prefix, tag.getAlbumImageMimeType());
        log.debug("{} WmpRating {}", prefix, tag.getWmpRating());
        log.debug("{} ItunesComment {}", prefix, tag.getItunesComment());
        log.debug("{} Lyrics {}", prefix, tag.getLyrics());
        log.debug("{} DataLength {}", prefix, tag.getDataLength());
        log.debug("{} Length {}", prefix, tag.getLength());
        log.debug("{} ObseleteFormat {}", prefix, tag.getObseleteFormat());
        log.debug("{} FrameSets {}", prefix, tag.getFrameSets());
    }

    void debugCustomTag(Mp3File mp3File) {
        if (!mp3File.hasCustomTag()) {
            return;
        }

        log.debug("Custom tag: {}", mp3File.getCustomTag() == null ? null : mp3File.getCustomTag().length);
    }

}
