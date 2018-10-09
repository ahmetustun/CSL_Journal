import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        //Designed to switch between map.unigram and map.bigram model


        if (args[4].contains("ml")) {
            ml.test.Test.doMain(args);
        } else if (args[10].equalsIgnoreCase("map/bigram")) {
            map.bigram.Gibbs_RecursiveInference.doMain(args);
        } else if (args[10].equalsIgnoreCase("map/unigram")) {
            map.unigram.Gibbs_RecursiveInference.doMain(args);
        }
    }

}
