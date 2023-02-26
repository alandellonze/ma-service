package it.ade.ma.service.diff;

import it.ade.ma.service.diff.model.DiffResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringDiffService extends AbstractDiffService<String> {

    @Override
    protected void emptyAction(DiffResult<String> diffResult, List<String> original) {
        minusAction(diffResult, original);
    }

}
