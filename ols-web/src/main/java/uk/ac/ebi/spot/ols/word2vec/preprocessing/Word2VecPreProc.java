package uk.ac.ebi.spot.ols.word2vec.preprocessing;

import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *
 */
@Component
public class Word2VecPreProc {

    private TokenizerFactory t;
    private SentenceIterator iter;

    public void processing(String path){

        iter = new LineSentenceIterator(new File(path));
            iter.setPreProcessor(new SentencePreProcessor() {
            @Override
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });
        // Split on white spaces in the line to get words
        t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
    }


    public TokenizerFactory getT() {
        return t;
    }

    public SentenceIterator getIter() {
        return iter;
    }
}
