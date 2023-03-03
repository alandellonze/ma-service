package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Slf4j
@Component
class MP3Util {

    private final SimpleDateFormat mmss = new SimpleDateFormat("mm:ss");

    void debug(Mp3File mp3File) {
        // debug file
        debugProp(mp3File);

        // debug tag v1
        if (mp3File.hasId3v1Tag()) {
            getID3v1Tag(mp3File.getId3v1Tag());
        }

        // debug tag v2
        if (mp3File.hasId3v2Tag()) {
            getID3v2Tag(mp3File.getId3v2Tag());
        }
    }

    private void debugProp(Mp3File mp3File) {
        log.debug("Filename: {}", mp3File.getFilename());
        log.debug("LengthInSeconds: {}", mp3File.getLengthInSeconds());
        log.debug("Bitrate: {}", mp3File.getBitrate());
        log.debug("Version: {}", mp3File.getVersion());
    }

    private void getID3v1Tag(ID3v1 tag) {
        log.debug("Artist: {}", tag.getArtist());
        log.debug("Track: {}", tag.getTrack());
        log.debug("Title: {}", tag.getTitle());
        log.debug("Album: {}", tag.getAlbum());
        log.debug("Year: {}", tag.getYear());
        log.debug("Genre: {} ({})", tag.getGenre(), tag.getGenreDescription());
        log.debug("Comment: {}", tag.getComment());
        log.debug("Version: {}", tag.getVersion());
    }

    private void getID3v2Tag(ID3v2 tag) {
        getID3v1Tag(tag);

        log.debug("Padding: {}", tag.getPadding());
        log.debug("Footer {}", tag.hasFooter());
        log.debug("Unsynchronisation {}", tag.hasUnsynchronisation());
        log.debug("BPM {}", tag.getBPM());
        log.debug("Grouping {}", tag.getGrouping());
        log.debug("Key {}", tag.getKey());
        log.debug("Date {}", tag.getDate());
        log.debug("Composer {}", tag.getComposer());
        log.debug("Publisher {}", tag.getPublisher());
        log.debug("OriginalArtist {}", tag.getOriginalArtist());
        log.debug("AlbumArtist {}", tag.getAlbumArtist());
        log.debug("Copyright {}", tag.getCopyright());
        log.debug("ArtistUrl {}", tag.getArtistUrl());
        log.debug("CommercialUrl {}", tag.getCommercialUrl());
        log.debug("CopyrightUrl {}", tag.getCopyrightUrl());
        log.debug("AudiofileUrl {}", tag.getAudiofileUrl());
        log.debug("AudioSourceUrl {}", tag.getAudioSourceUrl());
        log.debug("RadiostationUrl {}", tag.getRadiostationUrl());
        log.debug("PaymentUrl {}", tag.getPaymentUrl());
        log.debug("PublisherUrl {}", tag.getPublisherUrl());
        log.debug("Url {}", tag.getUrl());
        log.debug("PartOfSet {}", tag.getPartOfSet());
        log.debug("Compilation {}", tag.isCompilation());
        log.debug("Chapters {}", tag.getChapters());
        log.debug("ChapterTOC {}", tag.getChapterTOC());
        log.debug("Encoder {}", tag.getEncoder());
        log.debug("AlbumImage {}", tag.getAlbumImage() == null ? null : tag.getAlbumImage().length);
        log.debug("AlbumImageMimeType {}", tag.getAlbumImageMimeType());
        log.debug("WmpRating {}", tag.getWmpRating());
        log.debug("ItunesComment {}", tag.getItunesComment());
        log.debug("Lyrics {}", tag.getLyrics());
        log.debug("DataLength {}", tag.getDataLength());
        log.debug("Length {}", tag.getLength());
        log.debug("ObseleteFormat {}", tag.getObseleteFormat());
        log.debug("FrameSets {}", tag.getFrameSets());
    }


    String mmss(long millis) {
        try {
            return mmss.format(new Timestamp(millis));
        } catch (Exception e) {
            return null;
        }
    }

}
