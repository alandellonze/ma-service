package it.ade.ma.service.ripper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

abstract class AlbumTypeService {

    private static final Map<String, String> TYPES = new HashMap<>();

    static {
        TYPES.put("Full-length", "FULLLENGTH");

        TYPES.put("Boxed set", "BOXSET");

        String ep = "EP";
        TYPES.put("EP", ep);
        TYPES.put("Single", ep);

        TYPES.put("Demo", "DEMO");

        TYPES.put("Live album", "LIVE");

        String video = "VIDEO";
        TYPES.put("Video", video);
        TYPES.put("Video/VHS (legacy)", video);

        TYPES.put("Compilation", "COLLECTION");

        String split = "SPLIT";
        TYPES.put("Split", split);
        TYPES.put("Split video", split);
        TYPES.put("Split album", split);
        TYPES.put("Split album (legacy)", split);
        TYPES.put("Collaboration", split);

        TYPES.put("Covers", "COVERS");

        TYPES.put("Remastered", "REMASTERED");

        TYPES.put("Bootlegs", "BOOTLEGS");

        TYPES.put("Miscellaneous", "MISCELLANEOUS");
    }

    private AlbumTypeService() {
        throw new IllegalStateException("AlbumTypeService utility class cannot be instantiated");
    }

    static Optional<String> normalize(String type) {
        return ofNullable(TYPES.get(type));
    }

    static Integer calculateCount(Map<String, Integer> typeCounts, String type) {
        Integer typeCount = typeCounts.get(type);
        if (typeCount == null) {
            typeCount = 0;
        }
        typeCounts.put(type, ++typeCount);
        return typeCount;
    }

}
