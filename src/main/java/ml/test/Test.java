package ml.test;

import ml.prob.Utilities;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import ml.prob.MorphemeTransition;
import ml.prob.ReSegmenter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Created by ahmetu on 04.05.2016.
 */
public class Test {

    static Logger root = (Logger) LoggerFactory
            .getLogger(Logger.ROOT_LOGGER_NAME);

    static {
        root.setLevel(Level.ERROR);
    }

    public void multiThreadBigramTestViaMap(String inputFileName, int threadNumber, Map<String, Double> stems,
                                            Map<String, Double> affixes,
                                            ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> morphemeProb,
                                            double totalStemCount,
                                            String vectorFile, String priorType, String outputFileName) throws InterruptedException, IOException {

        Map<String, Double> stemProbabilities = new ConcurrentHashMap();
        Map<String, Double> results = new ConcurrentHashMap();
        Map<String, Double> notfounds = new ConcurrentHashMap();

        System.out.println("------------------------------------------------------------");
        System.out.println("--------------Stems & Affixes are constructing--------------");
        System.out.println("");

        for (String stem : stems.keySet()) {
            stemProbabilities.put(stem, (stems.get(stem) / totalStemCount));
        }

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("--------------ReSegmentation started with " + threadNumber + " threads --------------");
        System.out.println("");

        ReSegmenter rs = new ReSegmenter(inputFileName, stems, affixes, morphemeProb,
                stemProbabilities, results, notfounds);

        final BufferedReader reader = new BufferedReader(new FileReader(inputFileName), 1024 * 1024);

        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(new Runnable() {

                @Override
                public void run() {
                    //method testing

                    System.out.println("Thread starting: " + Thread.currentThread().getName());

                    String line = null;
                    do {
                        try {
                            synchronized (reader) {
                                line = reader.readLine();
                            }
                            if (line != null) {
                                String space = " ";
                                StringTokenizer st = new StringTokenizer(line, space);

                                double freq = Double.parseDouble(st.nextToken());
                                String word = st.nextToken();

                                if (priorType.equalsIgnoreCase("binomial_c")) {
                                    System.exit(101);
                                } else if (priorType.equalsIgnoreCase("binomial_np")) {
                                    System.exit(101);
                                } else {
                                    rs.reSegmentBigramWithMap(word, freq, true);
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } while (line != null);
                }
            });
        }

        for (int i = 0; i < threadNumber; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threadNumber; i++) {
            threads[i].join();
        }

        Map<String, Double> newResults = rs.getResults();
        Map<String, Double> newNotFound = rs.getNotFounds();

        PrintWriter writer_res_new = new PrintWriter(outputFileName, "UTF-8");

        for (Map.Entry<String, Double> entry : newResults.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_res_new.println(line);
        }

        for (Map.Entry<String, Double> entry : newNotFound.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_res_new.println(line);
        }

        writer_res_new.close();
    }

    public void multiThreadUnigramTestViaMap(String inputFileName, int threadNumber, Map<String, Double> stems,
                                             Map<String, Double> affixes, double totalAffixCount,
                                             double totalStemCount,
                                             String vectorFile, String priorType, String outputFileName) throws InterruptedException, IOException {

        Map<String, Double> stemProbabilities = new ConcurrentHashMap();
        Map<String, Double> affixProbabilities = new ConcurrentHashMap();
        Map<String, Double> results = new ConcurrentHashMap();
        Map<String, Double> notfounds = new ConcurrentHashMap();

        System.out.println("------------------------------------------------------------");
        System.out.println("--------------Stems & Affixes are constructing--------------");
        System.out.println("");

        for (String stem : stems.keySet()) {
            stemProbabilities.put(stem, (stems.get(stem) / totalStemCount));
        }

        for (String affix : affixes.keySet()) {
            affixProbabilities.put(affix, (affixes.get(affix) / totalAffixCount));
        }

        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("--------------ReSegmentation started with " + threadNumber + " threads --------------");
        System.out.println("");

        ReSegmenter rs = new ReSegmenter(inputFileName, stems, affixes, affixProbabilities,
                stemProbabilities, results, notfounds);

        final BufferedReader reader = new BufferedReader(new FileReader(inputFileName), 1024 * 1024);

        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(new Runnable() {

                @Override
                public void run() {
                    //method testing

                    System.out.println("Thread starting: " + Thread.currentThread().getName());

                    String line = null;
                    do {
                        try {
                            synchronized (reader) {
                                line = reader.readLine();
                            }
                            if (line != null) {
                                String space = " ";
                                StringTokenizer st = new StringTokenizer(line, space);

                                double freq = Double.parseDouble(st.nextToken());
                                String word = st.nextToken();

                                if (priorType.equalsIgnoreCase("binomial_c")) {
                                    System.exit(101);
                                } else if (priorType.equalsIgnoreCase("binomial_np")) {
                                    System.exit(101);
                                } else {
                                    rs.reSegmentUnigramWithMap(word, freq, true);
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } while (line != null);
                }
            });
        }

        for (int i = 0; i < threadNumber; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threadNumber; i++) {
            threads[i].join();
        }

        Map<String, Double> newResults = rs.getResults();
        Map<String, Double> newNotFound = rs.getNotFounds();

        PrintWriter writer_res_new = new PrintWriter(outputFileName, "UTF-8");

        for (Map.Entry<String, Double> entry : newResults.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_res_new.println(line);
        }

        for (Map.Entry<String, Double> entry : newNotFound.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_res_new.println(line);
        }

        writer_res_new.close();
    }

    public static void main(String[] args) throws Exception {

        /*
        NestedSegmenter ns = new NestedSegmenter(args[0], args[1]);
        ns.findSegmentsAndAffixes();

        Map<String, Double> stems = ns.getStems();
        Map<String, Double> affixes = ns.getAffixes();
        Map<String, Double> results = ns.getResults();
        Map<String, Double> notFounds = ns.getNotFounds();

        ns.setVectors(null);

        PrintWriter writer_seg = new PrintWriter("outputs/stems_nested", "UTF-8");
        PrintWriter writer_af = new PrintWriter("outputs/affixes_nested", "UTF-8");
        PrintWriter writer_res = new PrintWriter("outputs/results_nested", "UTF-8");
        PrintWriter writer_noF = new PrintWriter("outputs/absent_nested", "UTF-8");

        for (Map.Entry<String, Double> entry : stems.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_seg.println(line);
        }

        for (Map.Entry<String, Double> entry : affixes.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_af.println(line);
        }

        for (Map.Entry<String, Double> entry : results.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_res.println(line);
        }

        for (Map.Entry<String, Double> entry : notFounds.entrySet()) {
            String line = entry.getValue() + " " + entry.getKey();
            writer_noF.println(line);
        }

        writer_seg.close();
        writer_af.close();
        writer_res.close();
        writer_noF.close();
        */


        System.out.println("---------------------------------------------------------------");
        System.out.println("------------Transition probabilities are calculating-----------");
        MorphemeTransition mt = new MorphemeTransition(args[0]);
        mt.doItForFile();
        mt.calculateTotalMorphemeCount();
        mt.calculateTotalStemCount();
        mt.calculateTransitionProbabilities(MorphemeTransition.Smoothing.LAPLACE);

        mt.setMorphemeBiagramCount(null);

        //Utilities.writeFileBigramProbabilities(mt.getMorphemeBiagramProbabilities());


        ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> bigramsProb = mt.getMorphemeBiagramProbabilities();
        ConcurrentHashMap<String, Double> morphemes = mt.getMorphemeCount();
        ConcurrentHashMap<String, Double> stems = mt.getStemCount();
        double totalMorphs = mt.getTotalMorphemeCount();
        double totalStems = mt.getTotalStemCount();

        Test test = new Test();

        if (args[4].contains("unigram")) {
            test.multiThreadUnigramTestViaMap(args[1], Integer.parseInt(args[3]), stems, morphemes, totalMorphs, totalStems,
                    null, "non_prior", args[2]);
        } else if (args[4].contains("bigram")) {
            test.multiThreadBigramTestViaMap(args[1], Integer.parseInt(args[3]), stems, morphemes, bigramsProb, totalStems,
                    null, "non_prior", args[2]);
        }
    }

    public static void doMain(String[] args) throws Exception {
        main(args);
    }
}
