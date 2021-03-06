package map.unigram;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by ahmetu on 28.09.2016.
 */
public class Constant {

    private static double laplaceCoefficient = 0.0000001;
    private static double simUnfound = 0.1;
    private static double simUnsegmented;
    private static int heuristic = 2;
    private static double smoothingCoefficient = 0.01;
    private static int frequencyThreshold;
    private static HashMap<String, Double> cosineTable;
    private static boolean includeFrequencies = true;

    private Map<String, Integer> morphemeFreq = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<Sample> sampleList = new CopyOnWriteArrayList<>();

    public static double getSimUnfound() {
        return simUnfound;
    }

    public static boolean getIncludeFrequency() {
        return includeFrequencies;
    }

    public static double getSmoothingCoefficient() {
        return smoothingCoefficient;
    }

    public static int getHeuristic() {
        return heuristic;
    }

    public static int getFrequencyThreshold() {
        return frequencyThreshold;
    }

    public static double getLaplaceCoefficient() {
        return laplaceCoefficient;
    }

    public static double getSimUnsegmented() {
        return Math.log10(simUnsegmented);
    }

    public CopyOnWriteArrayList<Sample> getSampleList() {
        return sampleList;
    }

    public Map<String, Integer> getMorphemeFreq() {
        return morphemeFreq;
    }

    public static HashMap<String, Double> getCosineTable() {
        return cosineTable;
    }

    public Constant(String mapDir, String wordListDir, int heuristic, double simUnsegmentedArg, double simUnfound, int frequencyThreshold, boolean includeFrequencies) throws IOException, ClassNotFoundException {

        this.heuristic = heuristic;
        this.simUnsegmented = simUnsegmentedArg;
        this.simUnfound = simUnfound;
        this.frequencyThreshold = frequencyThreshold;
        this.includeFrequencies = includeFrequencies;
        List<String> freqWords = null;
        try {
            freqWords = Files.readAllLines(new File(wordListDir).toPath(), Charset.forName("UTF-8"));
        } catch (MalformedInputException e) {
            System.out.println(e.getMessage());
            freqWords = Files.readAllLines(new File(wordListDir).toPath(), Charset.forName("ISO-8859-9"));
        }
        generateCosineTable(mapDir + "//similarityScoresToSerialize");

        int numberOfProcessedWord = 0;
        for (String str : freqWords) {
            StringTokenizer tokens = new StringTokenizer(str, " ");
            String f = tokens.nextToken();
            String w = tokens.nextToken();

            if ((Integer.parseInt(f) >= frequencyThreshold) && (w.length() > 1)) {
                constructLists(w, Integer.parseInt(f));
                numberOfProcessedWord++;
            }
        }
        System.out.println(">>>>>>>>> Number of Processed Word: " + numberOfProcessedWord);
    }

    private void constructLists(String w, int f) {
        String randomSegmentation = Operations.randomSplitB(w);
        sampleList.add(new Sample(w, randomSegmentation));
        String uSymbol = "$";

        int frequency = 1;
        if (includeFrequencies) {
            frequency = f;
        }

        StringTokenizer tokenizer = new StringTokenizer(randomSegmentation, "+");

        if (tokenizer.countTokens() == 1) {
            String suffix = uSymbol;
            if (morphemeFreq.containsKey(suffix)) {
                morphemeFreq.put(suffix, morphemeFreq.get(suffix) + frequency);
            } else {
                morphemeFreq.put(suffix, frequency);
            }
        }

        String stem = tokenizer.nextToken();
        if (morphemeFreq.containsKey(stem)) {
            morphemeFreq.put(stem, morphemeFreq.get(stem) + frequency);
        } else {
            morphemeFreq.put(stem, frequency);
        }

        while (tokenizer.hasMoreTokens()) {
            String suffix = tokenizer.nextToken();
            if (morphemeFreq.containsKey(suffix)) {
                morphemeFreq.put(suffix, morphemeFreq.get(suffix) + frequency);
            } else {
                morphemeFreq.put(suffix, frequency);
            }
        }
    }

    public void generateCosineTable(String nameOfsimilarityScoresFile) throws IOException, ClassNotFoundException {

        File filesOfsimilarityScoresFile = new File(nameOfsimilarityScoresFile);

        FileInputStream fis = new FileInputStream(filesOfsimilarityScoresFile);
        ObjectInput in = null;
        Object o = null;
        in = new ObjectInputStream(fis);
        o = in.readObject();
        fis.close();
        in.close();

        cosineTable = (HashMap<String, Double>) o;
    }

    /*
    private void createSmoothCorpusWithAddition(Map<String, Double> corpus) {

        trieList.parallelStream().forEach((n) -> {
            for (String str : n.getWordList().keySet()) {
                if (!str.endsWith("$"))
                    newCorpus.put(str, laplaceCoefficient);
            }
        });

        for (String str : corpus.keySet()) {
            double value = corpus.get(str);
            newCorpus.put(str, (value + laplaceCoefficient));
        }

        for (String str : newCorpus.keySet()) {
            newCorpusSize = newCorpusSize + newCorpus.get(str);
        }

        corpus.clear();
    }
     */

 /*    public void generateBoundaryListforBaseline(int childLimit) {

        for (String trie : branchTable.keySet()) {
            Set<String> boundaryList = new TreeSet<>();
            // for baseline
            for (String s : branchTable.get(trie).keySet()) {
                if (branchTable.get(trie).get(s) >= childLimit) {
                    boundaryList.add(s);
                }
            }
            baselineBoundaries.put(trie, boundaryList);
        }
    }*/

 /*    private void calculateFrequencyForMorp(String trie) {

        Set<String> boundaries = baselineBoundaries.get(trie);

        ArrayList<String> tokens = new ArrayList<String>(); // unique elements?? set??
        for (String node : trieTable.get(trie)) {

            Stack<String> morphemeStack = new Stack<>();

            String current = "";
            boolean found = false;
            for (String boundary : boundaries) {
                if (node.startsWith(boundary) && !node.equals(boundary + "$")) {
                    current = boundary;
                    found = true;
                }
            }

            String morpheme = node.substring(current.length(), node.length() - 1);
            morphemeStack.add(morpheme);

            String word = node.substring(0, current.length());
            doSegmentation(word, boundaries, morphemeStack);

            String segmentation = morphemeStack.pop();
            int a = morphemeStack.size();
            for (int i = 0; i < a; i++) {
                String popped = morphemeStack.pop();
                segmentation = segmentation + "+" + popped;
            }
            tokens.addAll(tokenSegmentation(segmentation));
            sampleList.add(new Sample(node.substring(0, node.length() - 1), segmentation, trie));
        }

        for (String morpheme : tokens) {
            if (morphemeFreq.containsKey(morpheme)) {
                morphemeFreq.put(morpheme, morphemeFreq.get(morpheme) + 1);
            } else {
                morphemeFreq.put(morpheme, 1);
            }
        }
    }*/

 /*    private void doSegmentation(String node, Set<String> boundaries, Stack<String> morphmeStack) {

        if (!node.equals("")) {
            String current = "";
            boolean found = false;
            for (String boundary : boundaries) {
                if (node.startsWith(boundary) && !node.equals(boundary)) {
                    current = boundary;
                    found = true;
                }
            }
            String morpheme = node.substring(current.length(), node.length());
            morphmeStack.add(morpheme);

            String word = node.substring(0, current.length());

            doSegmentation(word, boundaries, morphmeStack);
        }
    }*/

 /*    public ArrayList<String> tokenSegmentation(String segmentation) {
        ArrayList<String> segments = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(segmentation, "+");
        while (tokens.hasMoreTokens()) {
            segments.add(tokens.nextToken());
        }
        return segments;
    }*/
}
