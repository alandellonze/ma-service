package it.ade.ma.service.path;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

@Slf4j
public abstract class FileService {

    private FileService() {
        throw new IllegalStateException("FileService utility class cannot be instantiated");
    }

    public static boolean exist(String path) {
        return exists(get(path));
    }

    static Stream<String> getFolderContent(String folderName) {
        return getFolderContent(folderName, true);
    }

    @SneakyThrows
    static Stream<String> getFolderContent(String folderName, boolean onlyName) {
        Path path = get(folderName);
        boolean exists = exists(path);
        log.debug("folder content: '{}' exists: {}", folderName, exists);
        if (exists) {
            int folderLength = folderName.length();
            return walk(path, 1)
                    .map(Path::toString)
                    .filter(p -> p.length() > folderLength)
                    .map(p -> onlyName ? p.substring(folderLength + 1) : p)
                    .sorted();
        }
        return empty();
    }

    @SneakyThrows
    static Map<String, List<String>> getFoldersContent(String folderName, List<String> excludeList, String fileExtension) {
        Path path = get(folderName);
        boolean exists = exists(path);
        log.debug("folder content: '{}' exists: {}", folderName, exists);
        if (exists) {
            Map<String, List<String>> map = new LinkedHashMap<>();
            int folderNamePartsSize = folderName.split("/").length;
            walk(path)
                    .map(Path::toString)
                    .filter(p -> excludeList.stream().noneMatch(p::contains) && p.endsWith(fileExtension))
                    .sorted()
                    .forEachOrdered(p -> {
                        String cd = "";

                        // get the sub folder (if exists)
                        List<String> dirParts = asList(p.split("/"));
                        if (folderNamePartsSize < dirParts.size() - 1) {
                            cd = join(" - ", dirParts.subList(folderNamePartsSize, dirParts.size() - 1));
                        }

                        // insert the mp3 name grouped by sub folders
                        List<String> files = map.computeIfAbsent(cd, f -> new LinkedList<>());
                        files.add(p);
                    });
            return map;
        }
        return emptyMap();
    }

    static Stream<String> concatAndSort(Stream<String> s1, Stream<String> s2) {
        return concat(s1, s2).sorted();
    }

    @SneakyThrows
    public static byte[] loadFile(String path) {
        return exist(path) ? readAllBytes(get(path)) : null;
    }

}
