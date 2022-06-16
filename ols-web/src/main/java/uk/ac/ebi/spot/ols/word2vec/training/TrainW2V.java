package uk.ac.ebi.spot.ols.word2vec.training;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TrainW2V {

    private Word2Vec vec;

    /**
     *
     * @param t
     * @param iter
     */
    public void trainSerialise(TokenizerFactory t, SentenceIterator iter) throws IOException {
        vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

		vec.fit();

        String outPath = new ClassPathResource("out.txt").getFile().getAbsolutePath();
		WordVectorSerializer.writeWordVectors(vec, outPath);

    }

    public Word2Vec getVec() {
        return vec;
    }

}
