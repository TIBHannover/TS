package uk.ac.ebi.spot.ols.controller.assember;

import uk.ac.ebi.spot.ols.controller.SimilarityController;
import uk.ac.ebi.spot.ols.model.ontology.Similarity;
import uk.ac.ebi.spot.ols.model.ontology.SimilarityModel;
import org.springframework.hateoas.server.mvc.ResourceSupportAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class SimilarityModelAssembler extends ResourceSupportAssemblerSupport<Similarity, SimilarityModel> {

    public SimilarityModelAssembler() {
        super(SimilarityController.class, SimilarityModel.class);
    }

    @Override
    public SimilarityModel toModel(Similarity entity) {
        SimilarityModel model = instantiateModel(entity);
        model.setName(entity.getName());
        model.setOntologies(entity.getOntologies());

        return model;
    }

}
