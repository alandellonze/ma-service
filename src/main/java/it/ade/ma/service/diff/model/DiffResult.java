package it.ade.ma.service.diff.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiffResult<T> {

    private Integer changes = 0;
    private List<DiffRow<T>> diffs = new ArrayList<>();

}
