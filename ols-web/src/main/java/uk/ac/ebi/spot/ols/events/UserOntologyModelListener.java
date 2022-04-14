package uk.ac.ebi.spot.ols.events;

import uk.ac.ebi.spot.ols.entities.UserOntology;
import uk.ac.ebi.spot.ols.service.SequenceGeneratorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class UserOntologyModelListener extends AbstractMongoEventListener<UserOntology> {
    private SequenceGeneratorService sequenceGenerator;

    @Autowired
    public UserOntologyModelListener(SequenceGeneratorService sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<UserOntology> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGenerator.generateSequence(UserOntology.SEQUENCE_NAME));
        }
    }
}
