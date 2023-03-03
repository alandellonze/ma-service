package it.ade.ma.service.path;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.walk;
import static java.nio.file.Paths.get;
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
                    .filter(p -> p.toString().length() > folderLength)
                    .map(p -> onlyName ? p.toString().substring(folderLength) : p.toString())
                    .sorted();
        }
        return empty();
    }

    static Stream<String> concatAndSort(Stream<String> s1, Stream<String> s2) {
        return concat(s1, s2).sorted();
    }

}
