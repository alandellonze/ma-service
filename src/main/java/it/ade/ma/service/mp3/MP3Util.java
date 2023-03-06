package it.ade.ma.service.mp3;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import it.ade.ma.entities.dto.AlbumDTO;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

interface MP3Util {

    Integer MP3_TAG_DEFAULT_GENRE = 9;
    String MP3_TAG_DEFAULT_GENRE_DESCRIPTION = "Metal";
    String MP3_TAG_COVER_MIME = "image/jpeg";

    static ID3v2 createID3v2Template(AlbumDTO album, byte[] cover, String cd) {
        // create the id3v2 template
        ID3v2 id3v2Template = new ID3v24Tag();
        id3v2Template.setArtist(album.getBandName());

        // set album name
        String name = (album.getMaName() != null) ? album.getMaName() : album.getName();
        id3v2Template.setAlbum(name + (isNotBlank(cd) ? (" - " + cd) : ""));

        id3v2Template.setYear(album.getYear().toString());
        id3v2Template.setGenre(MP3_TAG_DEFAULT_GENRE);
        id3v2Template.setGenreDescription(MP3_TAG_DEFAULT_GENRE_DESCRIPTION);

        // get cover from disk
        if (cover != null) {
            id3v2Template.setAlbumImage(cover, MP3_TAG_COVER_MIME);
        }

        return id3v2Template;
    }

    static String extractTitleFromMp3File(Mp3File mp3File) {
        String filename = mp3File.getFilename();
        filename = filename.substring(filename.lastIndexOf("/") + 1);
        filename = filename.substring(filename.indexOf("-") + 1);
        filename = filename.substring(0, filename.indexOf(".mp3"));
        return filename.trim();
    }

    static String normalizeTitle(String title) {
        String normalizedTitle = capitalize(title);

        normalizedTitle = normalizedTitle.replace("´", "'");

        // FIXME handle special substitution (ie: "(BONUS TRACK)", "III", etc...)
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(REMAKE 2006\\)", " (2006 VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(2008 VERSION\\)", " (2008 VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(2010 VERSION\\)", " (2010 VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ACOUSTIC VERSION\\)", " (ACOUSTIC VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ACOUSTIC\\)", " (ACOUSTIC VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ALTERNATIVE MIX\\)", " (ALTERNATIVE MIX)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(REMIX\\)", " (REMIX)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ORIGINAL LYRICS ROUGH MIX\\)", " (ORIGINAL LYRICS ROUGH MIX)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(EARLY ROUGH MIX\\)", " (EARLY ROUGH MIX)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(EARLY MIX\\)", " (EARLY MIX)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(APOCALYPSE VERSION\\)", " (APOCALYPSE VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(APOCALYPSE\\)", " (APOCALYPSE VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(BONUS TRACK\\)", " (BONUS TRACK)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(BONUS\\)", " (BONUS TRACK)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) COVER\\)", " COVER)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(REMASTERED\\)", " (REMASTERED)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(REMASTERED VERSION\\)", " (REMASTERED)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RADIO EDIT\\)", " (RADIO EDIT)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RADIO\\)", " (RADIO EDIT)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(DEMO VERSION\\)", " (DEMO)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(DEMO\\)", " (DEMO)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(EDIT VERSION\\)", " (EDIT VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SINGLE EDIT\\)", " (EDIT VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(EDIT\\)", " (EDIT VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(HISTORY VERSION\\)", " (HISTORY VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(HISTORY\\)", " (HISTORY VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(INSTRUMENTAL VERSION\\)", " (INSTRUMENTAL VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(INSTRUMENTAL\\)", " (INSTRUMENTAL VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(JAP BONUS TRACK\\)", " (JAP BONUS TRACK)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(JAPAN BONUS TRACK\\)", " (JAP BONUS TRACK)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(LIVE\\)", " (LIVE)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(LIVE 2012\\)", " (LIVE)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(LIVE ACOUSTIC REHEARSAL VERSION\\)", " (LIVE ACOUSTIC REHEARSAL VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(JAPANESE VERSION\\)", " (JAP VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ORCHESTRAL VERSION\\)", " (ORCHESTRAL VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ORCHESTRAL\\)", " (ORCHESTRAL VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(PIANO AND VOCAL VERSION\\)", " (PIANO AND VOCAL VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(2021 RE-RECORDED VERSION\\)", " (2021 RE-RECORDED VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(2021 RE-RECORDED\\)", " (2021 RE-RECORDED VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RE-RECORDED VERSION\\)", " (RE-RECORDED VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RE-RECORDED\\)", " (RE-RECORDED VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RERECORDED VERSION\\)", " (RE-RECORDED VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RERECORDED\\)", " (RE-RECORDED VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RE-RECORDING VERSION\\)", " (RE-RECORDED VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(RE-RECORDING\\)", " (RE-RECORDED VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SOUNDTRACK VERSION\\)", " (SOUNDTRACK VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SOUNDTRACK\\)", " (SOUNDTRACK VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(STUDIO JAM VERSION\\)", " (STUDIO JAM VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(STUDIO JAM\\)", " (STUDIO JAM VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SYMPHONIC VERSION\\)", " (SYMPHONIC VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SYMPHONIC\\)", " (SYMPHONIC VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(STUDIO VERSION\\)", " (STUDIO VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(STUDIO\\)", " (STUDIO VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(16TH CENTURY VERSION\\)", " (16TH CENTURY VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(16TH CENTURY\\)", " (16TH CENTURY VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ALTERNATIVE VOCALS VERSION\\)", " (ALTERNATIVE VOCALS VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ALTERNATIVE VOCALS\\)", " (ALTERNATIVE VOCALS VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ALTERNATIVE VOCAL VERSION\\)", " (ALTERNATIVE VOCALS VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(ALTERNATIVE VOCAL\\)", " (ALTERNATIVE VOCALS VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(FULL MARCO VOCALS VERSION\\)", " (FULL MARCO VOCALS VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(UNPLUGGED\\)", " (UNPLUGGED VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SINGLE VERSION\\)", " (SINGLE VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(PIANO VERSION\\)", " (PIANO VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(KARAOKE VERSION\\)", " (KARAOKE VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(KARAOKE\\)", " (KARAOKE VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(VOCAL DUET VERSION\\)", " (VOCAL DUET VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(VOCAL DUET\\)", " (VOCAL DUET VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(WITHOUT LYRICS VERSION\\)", " (WITHOUT LYRICS VERSION)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(WITHOUT LYRICS\\)", " (WITHOUT LYRICS VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(SPANISH VERSION\\)", " (SPANISH VERSION)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(EXTENDED PROMO MIX\\)", " (EXTENDED PROMO MIX)");
        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(PROMO MIX\\)", " (PROMO MIX)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(2006 DIRECTOR'S CUT\\)", " (2006 DIRECTOR'S CUT)");

        normalizedTitle = normalizedTitle.replaceAll("(?i) \\(DVD VERSION\\)", " (DVD VERSION)");

        return normalizedTitle.trim();
    }

}
