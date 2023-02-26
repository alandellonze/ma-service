package it.ade.ma.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static it.ade.ma.entities.Status.NONE;

@Entity
@Getter
@Setter
public class Album {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    private Integer position;
    private String type;
    private Integer typeCount;
    private String name;
    private Integer year;
    private String maType;
    private Integer maTypeCount;
    private String maName;

    @Enumerated
    private Status status = NONE;

}
