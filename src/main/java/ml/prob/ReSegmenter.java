package ml.prob;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ahmetu on 04.05.2016.
 */
public class ReSegmenter {

    private Map<String, Double> stems;
    private Map<String, Double> affixes;

    private Map<String, Double> results;
    private Map<String, Double> notFounds;

    private Map<String, Double> stemProbabilities;
    private Map<String, Double> affixProbabilities;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> morphemeBiagramProbabilities;

    private String fileSegmentationInput;

    WordVectors vectors;

    String startMorpheme = "STR";
    String endMorphmeme = "END";

    public Map<String, Double> getResults() {
        return results;
    }

    public Map<String, Double> getNotFounds() {
        return notFounds;
    }

    public void setStems(Map<String, Double> stems) {
        this.stems = stems;
    }

    public void setAffixes(Map<String, Double> affixes) {
        this.affixes = affixes;
    }

    public void setResults(Map<String, Double> results) {
        this.results = results;
    }

    public void setNotFounds(Map<String, Double> notFounds) {
        this.notFounds = notFounds;
    }

    public void setMorphemeBiagramProbabilities(ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> morphemeBiagramProbabilities) {
        this.morphemeBiagramProbabilities = morphemeBiagramProbabilities;
    }

    public ReSegmenter(String fileSegmentationInput, Map<String, Double> stems, Map<String, Double> affixes, ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> morphemeBiagramProbabilities,
                       Map<String, Double> stemProbabilities, Map<String, Double> results, Map<String, Double> notFounds) {
        this.fileSegmentationInput = fileSegmentationInput;
        this.stems = stems;
        this.affixes = affixes;
        this.stemProbabilities = stemProbabilities;
        this.morphemeBiagramProbabilities = morphemeBiagramProbabilities;

        this.results = results;
        this.notFounds = notFounds;

        this.vectors = vectors;
    }

    public ReSegmenter(String fileSegmentationInput, Map<String, Double> stems, Map<String, Double> affixes, Map<String, Double> affixProbabilities,
                       Map<String, Double> stemProbabilities, Map<String, Double> results, Map<String, Double> notFounds) {
        this.fileSegmentationInput = fileSegmentationInput;
        this.stems = stems;
        this.affixes = affixes;
        this.stemProbabilities = stemProbabilities;
        this.affixProbabilities = affixProbabilities;

        this.results = results;
        this.notFounds = notFounds;

        this.vectors = vectors;
    }


    public void reSegmentBigramWithMap(String word, double frequency, boolean withStemProbability) throws FileNotFoundException {

        /*
         ** Prior information must be added to the production due to prevent undersegmentation.
         ** Affix lenght can be used for prior with coefficient of n in the equation (1/29)^n
         */

        List<String> segmentations = Utilities.getPossibleSegmentations(word, stems.keySet(), affixes.keySet()/*, vectors*/);
        if (segmentations.isEmpty()) {
            if (notFounds.containsKey(word)) {
                notFounds.put(word, notFounds.get(word) + frequency);
            } else {
                notFounds.put(word, frequency);
            }
        } else {

            double max = -1 * Double.MAX_VALUE;
            String argmax = word;

            for (String segmentation : segmentations) {
                String seperator = "+";
                StringTokenizer st = new StringTokenizer(segmentation, seperator);

                String stem = st.nextToken();
                String curr = startMorpheme;
                String next = null;

                double probability = 0d;
                if (withStemProbability) {
                    probability = probability + Math.log(stemProbabilities.get(stem));
                }

                while (st.hasMoreTokens()) {
                    next = st.nextToken();
                    probability = probability + Math.log(morphemeBiagramProbabilities.get(curr).get(next));
                    curr = next;
                }

                next = endMorphmeme;
                probability = probability + Math.log(morphemeBiagramProbabilities.get(curr).get(next));

                if (probability > max) {
                    max = probability;
                    argmax = segmentation;
                }
            }

            if (results.containsKey(argmax)) {
                results.put(argmax, results.get(argmax) + frequency);
            } else {
                results.put(argmax, frequency);
            }
        }
    }

    public void reSegmentUnigramWithMap(String word, double frequency, boolean withStemProbability) throws FileNotFoundException {

        /*
         ** Prior information must be added to the production due to prevent undersegmentation.
         ** Affix lenght can be used for prior with coefficient of n in the equation (1/29)^n
         */

        List<String> segmentations = Utilities.getPossibleSegmentations(word, stems.keySet(), affixes.keySet()/*, vectors*/);
        if (segmentations.isEmpty()) {
            if (notFounds.containsKey(word)) {
                notFounds.put(word, notFounds.get(word) + frequency);
            } else {
                notFounds.put(word, frequency);
            }
        } else {

            double max = -1 * Double.MAX_VALUE;
            String argmax = word;

            for (String segmentation : segmentations) {
                String seperator = "+";
                StringTokenizer st = new StringTokenizer(segmentation, seperator);

                String stem = st.nextToken();
                String next = null;

                double probability = 0d;
                if (withStemProbability) {
                    probability = probability + Math.log(stemProbabilities.get(stem));
                }

                while (st.hasMoreTokens()) {
                    next = st.nextToken();
                    probability = probability + Math.log(affixProbabilities.get(next));
                }

                if (probability > max) {
                    max = probability;
                    argmax = segmentation;
                }
            }

            if (results.containsKey(argmax)) {
                results.put(argmax, results.get(argmax) + frequency);
            } else {
                results.put(argmax, frequency);
            }
        }
    }
}
