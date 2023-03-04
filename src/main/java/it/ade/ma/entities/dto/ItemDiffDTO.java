package it.ade.ma.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDiffDTO implements Comparable<ItemDiffDTO> {

    private Long albumId;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDiffDTO itemDiffDTO = (ItemDiffDTO) o;
        return name.equals(itemDiffDTO.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public int compareTo(ItemDiffDTO o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }

}
