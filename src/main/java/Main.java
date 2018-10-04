import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //Designed to switch between unigram and bigram model
        if (args[11].equalsIgnoreCase("unigram")) {
            unigram.Gibbs_RecursiveInference.doMain(args);
        } else if (args[11].equalsIgnoreCase("bigram")) {
            bigram.Gibbs_RecursiveInference.doMain(args);
        }
    }

}
