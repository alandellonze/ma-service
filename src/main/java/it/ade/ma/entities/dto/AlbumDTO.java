package it.ade.ma.entities.dto;

import it.ade.ma.entities.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static it.ade.ma.entities.Status.NONE;
import static java.lang.String.format;

@Data
@NoArgsConstructor
public class AlbumDTO {

    private Long id;
    private Long bandId;
    private String bandName;
    private Integer position;
    private String type;
    private Integer typeCount;
    private String name;
    private Integer year;
    private String maType;
    private Integer maTypeCount;
    private String maName;
    private Status status;
    private Status statusMP3;
    private Status statusCover;
    private Status statusScans;

    public AlbumDTO(Long bandId, Integer position, String type, Integer typeCount, String name, Integer year) {
        this.bandId = bandId;
        this.position = position;
        this.type = type;
        this.typeCount = typeCount;
        this.name = name;
        this.year = year;
        this.status = NONE;
    }

    public boolean isFullyCustom() {
        return type == null && typeCount == null && name == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlbumDTO albumDTO = (AlbumDTO) o;
        if (!Objects.equals(type, albumDTO.type)) return false;
        if (!Objects.equals(typeCount, albumDTO.typeCount)) return false;
        if (!Objects.equals(name, albumDTO.name)) return false;
        return year.equals(albumDTO.year);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (typeCount != null ? typeCount.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + year.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(format("%03d", position)).append(" ");

        if (maType != null) {
            sb.append(maType).append("*");
        } else if (type != null) {
            sb.append(type);
        }

        if (maTypeCount != null) {
            sb.append(format("%02d", maTypeCount)).append("*");
        } else if (typeCount != null) {
            sb.append(format("%02d", typeCount));
        }

        sb.append(" - ");

        if (maName != null) {
            sb.append(maName).append("*");
        } else if (name != null) {
            sb.append(name);
        }

        sb.append(" (").append(year).append(")");

        return sb.toString();
    }

}
