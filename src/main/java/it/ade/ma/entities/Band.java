package it.ade.ma.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Band {

    @Id
    private Long id;

    private String name;
    private Long maKey;

}
