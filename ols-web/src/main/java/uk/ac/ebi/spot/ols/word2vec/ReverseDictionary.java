package uk.ac.ebi.spot.ols.word2vec;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 *
 */
@Component
public class ReverseDictionary {
    public Collection<String> dict(Word2Vec vec, String query, int count) {
        Collection<String> lst = vec.wordsNearest(query, count);
        System.out.println(lst.toString());
        return lst;
    }
}
