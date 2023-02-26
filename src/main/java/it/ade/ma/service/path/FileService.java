package it.ade.ma.service.path;

import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.walk;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

public interface FileService {

    static boolean exist(String path) {
        return exists(get(path));
    }

    @SneakyThrows
    static Stream<String> getFolderContent(String folderName) {
        Path path = get(folderName);
        if (exists(path)) {
            int folderLength = folderName.length();
            return walk(path, 1)
                    .filter(p -> p.toString().length() > folderLength)
                    .map(p -> p.toString().substring(folderLength))
                    .sorted();
        }
        return empty();
    }

    static List<String> concatStream(Stream<String> s1, Stream<String> s2) {
        return concat(s1, s2).sorted().collect(toList());
    }

}
