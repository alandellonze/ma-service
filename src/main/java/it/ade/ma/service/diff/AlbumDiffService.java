package it.ade.ma.service.diff;

import it.ade.ma.entities.dto.AlbumDTO;
import it.ade.ma.service.diff.model.DiffResult;
import it.ade.ma.service.diff.model.DiffRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static it.ade.ma.service.diff.model.DiffRow.DiffType.EQUAL;
import static it.ade.ma.service.diff.model.DiffRow.DiffType.MINUS;
import static java.util.Collections.singletonList;

@Slf4j
@Component
public class AlbumDiffService extends AbstractDiffService<AlbumDTO> {

    @Override
    protected void minusAction(DiffResult<AlbumDTO> diffResult, List<AlbumDTO> original) {
        List<DiffRow<AlbumDTO>> diffs = diffResult.getDiffs();
        for (AlbumDTO o : original) {
            if (o.isFullyCustom()) {
                log.debug("  {}", o);
                diffs.add(new DiffRow<>(EQUAL, singletonList(o), null));
            } else {
                log.debug("- {}", o);
                diffs.add(new DiffRow<>(MINUS, singletonList(o), null));
                incrementCount(diffResult, 1);
            }
        }
    }

}
