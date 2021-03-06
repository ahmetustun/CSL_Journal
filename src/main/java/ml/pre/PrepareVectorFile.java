package ml.pre;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by ahmetu on 18.05.2016.
 */
public class PrepareVectorFile {

    public static void getWordsFromVectorFile(String vectorFile) throws IOException {

        PrintWriter pw = new PrintWriter(vectorFile + "_prepared");

        BufferedReader reader = new BufferedReader(new FileReader(vectorFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String space = " ";
            StringTokenizer st = new StringTokenizer(line, space);
            String word = st.nextToken();
            pw.println(word);
        }

        pw.close();
    }

    public static void constructNewVectorFile(String vectorFile, String acceptedWordsFile) throws IOException {

        Set<String> acceptedWords = new HashSet<>();

        BufferedReader reader = new BufferedReader(new FileReader(acceptedWordsFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String space = " ";
            StringTokenizer st = new StringTokenizer(line, space);
            String word = st.nextToken();
            acceptedWords.add(word);
        }

        reader.close();

        PrintWriter pw = new PrintWriter(vectorFile + "_cleared");

        BufferedReader reader2 = new BufferedReader(new FileReader(vectorFile));

        while ((line = reader2.readLine()) != null) {
            String space = " ";
            StringTokenizer st = new StringTokenizer(line, space);
            String word = st.nextToken();

            if (acceptedWords.contains(word)) {
                pw.println(line);
            }
        }

        pw.close();
    }

    public static void countLineNumber(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            count++;
        }
        System.out.println(count);
    }

    public static void prepareData() throws IOException {
        Set<String> wordInData = new HashSet<>();

        BufferedReader reader = new BufferedReader(new FileReader("outputs/eng_ready_2"));

        String line;
        while ((line = reader.readLine()) != null) {
            wordInData.add(line);
        }

        reader.close();

        PrintWriter pw = new PrintWriter("outputs/eng_ready_baseline");

        BufferedReader reader2 = new BufferedReader(new FileReader("outputs/baselineseg.final.2"));

        while ((line = reader2.readLine()) != null) {
            String space = " ";
            StringTokenizer st = new StringTokenizer(line, space);
            String freq = st.nextToken();
            String word = st.nextToken().replaceAll("\\+", "");

            if (wordInData.contains(word)) {
                pw.println(line);
            }
        }

        pw.close();

    }

    public static void main(String[] args) throws IOException {
        prepareData();
    }
}
