package uk.ac.ebi.spot.ols.controller.assember;

import uk.ac.ebi.spot.ols.controller.SimilarityController;
import uk.ac.ebi.spot.ols.controller.dto.PairwiseSimilarityModel;
import uk.ac.ebi.spot.ols.model.ontology.PairwiseSimilarity;
import org.springframework.hateoas.server.mvc.ResourceSupportAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class PairwiseSimilarityModelAssembler
    extends ResourceSupportAssemblerSupport<PairwiseSimilarity, PairwiseSimilarityModel> {

    public PairwiseSimilarityModelAssembler() {
        super(SimilarityController.class, PairwiseSimilarityModel.class);
    }

    @Override
    public PairwiseSimilarityModel toModel(PairwiseSimilarity entity) {
        PairwiseSimilarityModel model = instantiateModel(entity);
        model.setPair(entity.getPair());
        model.setSum(entity.getSum());
        model.setTotalSum(entity.getTotalSum());
        model.setPercentage(entity.getPercent());
        model.setTitles(entity.getTitles());
        model.setCharacteristics(entity.getCharacteristics());

        return model;
    }

}
