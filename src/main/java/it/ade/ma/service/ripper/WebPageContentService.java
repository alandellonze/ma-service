package it.ade.ma.service.ripper;

import it.ade.ma.service.ripper.model.WebPageAlbum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.jsoup.Jsoup.connect;
import static org.jsoup.Jsoup.parse;

@Slf4j
@Component
public class WebPageContentService {

    @Value("${ma.metal-archives.url}")
    private String maMetalArchivesUrl;

    @SneakyThrows
    List<WebPageAlbum> parsePage(Long bandMAKey) {
        log.info("parsePage({})", bandMAKey);

        List<WebPageAlbum> webPageAlbums = new ArrayList<>();

        // retrieve the web page content
        String url = format(maMetalArchivesUrl, bandMAKey);
        log.debug("url: {}", url);
        Document doc = connect(url).get();

        // extract all the html content from tds
        Elements tds = doc.select("td");
        List<String> tdsHtmlContent = tds.stream().map(Element::html).collect(toList());

        // group tds 4 by 4
        for (int i = 0; i < tdsHtmlContent.size(); i = i + 4) {
            String type = tdsHtmlContent.get(i + 1);
            String name = normalizeHtml(parse(tdsHtmlContent.get(i)).select("a").html());
            String year = tdsHtmlContent.get(i + 2);
            webPageAlbums.add(new WebPageAlbum(type, name, year));
        }

        return webPageAlbums;
    }

    private String normalizeHtml(String html) {
        return html.replace("&amp;", "&");
    }

}
