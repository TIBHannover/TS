package uk.ac.ebi.spot.ols.controller.assember;

import uk.ac.ebi.spot.ols.controller.MostCommonlyUsedController;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.model.ontology.KeyValueModel;
import org.springframework.hateoas.server.mvc.ResourceSupportAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class KeyValueModelAssembler extends ResourceSupportAssemblerSupport<KeyValueResultDto, KeyValueModel> {

    public KeyValueModelAssembler() {
        super(MostCommonlyUsedController.class, KeyValueModel.class);
    }

    @Override
    public KeyValueModel toModel(KeyValueResultDto entity) {
        KeyValueModel model = instantiateModel(entity);
        model.setKey(entity.getKey());
        model.setValue(entity.getValue());

        return model;
    }

}
