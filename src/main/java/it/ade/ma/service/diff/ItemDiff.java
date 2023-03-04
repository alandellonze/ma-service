package it.ade.ma.service.diff;

import it.ade.ma.entities.dto.ItemDiffDTO;
import it.ade.ma.service.diff.model.DiffResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemDiff extends Diff<ItemDiffDTO> {

    @Override
    protected void emptyAction(DiffResult<ItemDiffDTO> diffResult, List<ItemDiffDTO> original) {
        minusAction(diffResult, original);
    }

}
