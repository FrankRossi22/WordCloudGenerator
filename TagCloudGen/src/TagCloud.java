import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


/**
 * Class to output a WordCounter.
 *
 * @author Rahul Rajaram & Frank Rossi
 */
public final class TagCloud {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloud() {
        // no code needed here
    }

    /**
     * Splits all the words in a file into a queue.
     *
     * @param in
     *            input file.
     *
     * @requires in is not null
     * @ensures Queue with all words in a file without separators
     *
     * @return Queue with all words without separators
     *
     */
    public static Queue<String> splitWords(BufferedReader in) {
        //separator string
        String separators = "'[-.;!?\",_0123456789*:]";

        //create queue
        Queue<String> words = new LinkedList<String>();
        String next = "";
        int currLine = 1;
        
        try {
            next = in.readLine();
        } catch (IOException e){
            System.err.println("File Could not be Read at Line: " + currLine + "; Queue is Unfinished");
            return words;
        }           
        

        //loop going to end of file
        while (next != null) {
             //remove all separators
            for (int j = 0; j < separators.length(); j++) {
                char ch = ' ';

                //replace any of the separator characters with a space
                next = next.replace(separators.charAt(j), ch);
            }

            //and split the whole string by one space
            String[] oneLine = next.split(" ");

            //add all the words to the queue
            for (int i = 0; i < oneLine.length; i++) {
                String current = oneLine[i];
                //if statement making sure current is not a space or empty
                //accounts for split statement above creating words that are spaces
                if (!(current.equals(" ") || current.equals(""))) {
                    //if it isn't a space, add lowercase version to words
                    words.add(current.toLowerCase());
                }

            }
            try {
                next = in.readLine();
            } catch (IOException e){
                System.err.println("File Could not be Read at Line: " + currLine + "; Queue is Unfinished");
                return words;
            }
            currLine++;
        }

        return words;
    }

    /**
     * Creates Map with Words and Word Counts.
     *
     * @param words
     *            Queue of all words to be counted
     * @requires array not null
     * @ensures countWords = Map with Words with respective count
     *
     *
     * @return Map with String and count.
     */

    public static Map<String, Integer> countWords(Queue<String> words) {
        //initialize map
        Map<String, Integer> collection = new HashMap<String, Integer>();

        //for loop going through each of the words in array, and counts them
        for (int i = 0; i < words.size(); i++) {
            String current = words.remove();

            //if word already in map
            if (collection.containsKey(current)) {
                //take value and add 1
                Integer currentValue = collection.get(current);
                currentValue++;
                collection.replace(current, currentValue);
            } else {
                //word is not in map
                //create Pair with key = word, and value(count) = 1
                collection.put(current, 1);
            }
            words.add(current);
        }

        return collection;
    }

    /**
     * Provides the display font size of a word in a tag cloud.
     *
     * @param collectionOfWords
     *            group of words with counts
     * @param word
     *            word to find font size of
     * @param minCount
     *            smallest count in collection
     * @param maxCount
     *            largest count in collection
     * @return the displayFontSize as calculated
     */
    public static int fontSize(Map<String, Integer> collectionOfWords,
            String word, int minCount, int maxCount) {
        final int maxFontSize = 48;
        final int minFontSize = 11;
        
        int display = 16;
        if((maxCount - minCount) != 0) {
            display = minFontSize + (maxFontSize - minFontSize) * (collectionOfWords.get(word) - minCount)
                    / (maxCount - minCount);
        }

        return display;
    }

    

    /**
     * Creates file body in html, with String and Counts from given Map placed
     * in a table, sorted alphabetically.
     *
     * @param collectionOfWords
     *            Map containing word and count
     * @param outputFile
     *            File to output word counts
     * @param maxWords
     *            numberOfWords to display
     * @updates outputFile
     * @requires collectionOfWords not null
     * @ensures outputFile = #outputFile + [table of strings and integers in
     *          collectionOfWords ordered alphabetically by String]
     *
     *
     */
    public static void fileBody(Map<String, Integer> collectionOfWords,
            PrintWriter outputFile, int maxWords) {

        //uses this to find font sizes
        Map<String, Integer> temp = new HashMap<String, Integer>();

        Set<Map.Entry<String, Integer>> mapSet = collectionOfWords.entrySet();
        int length = collectionOfWords.size();
        Iterator<Map.Entry<String, Integer>> it = mapSet.iterator();
        List<Map.Entry<String, Integer>> countSort = new LinkedList<Map.Entry<String, Integer>>();

        for(int i = 0; i < length; i++) {
            Map.Entry<String, Integer> p = it.next();
            it.remove();
            countSort.add(p);
            temp.put(p.getKey(), p.getValue());

        }
        while(!temp.isEmpty()) {
            java.util.Set<java.util.Map.Entry<String, Integer>> cTemp = temp.entrySet();
            Iterator<java.util.Map.Entry<String, Integer>> itTemp = cTemp.iterator();
            
            java.util.Map.Entry<String, Integer> pTemp = itTemp.next();
            itTemp.remove();
            collectionOfWords.put(pTemp.getKey(), pTemp.getValue());

        }
        
        countSort.sort(new IntCompareCount());
        
        int maxCount = countSort.get(0).getValue();
        int minCount = countSort.get(countSort.size() - 1).getValue();

        List<Map.Entry<String, Integer>> topCounts = new LinkedList<Map.Entry<String, Integer>>();
        for(int i = 0; i < maxWords && i < countSort.size(); i ++) {
            topCounts.add(countSort.get(i));
        }

        topCounts.sort(new StringCompareWord());




        String currentLine = "";

        //process lines
        int i = 0;

        while (i < topCounts.size()) {
            String currKey = topCounts.get(i).getKey();
            int currVal = topCounts.get(i).getValue();
                currentLine += "<span style=\"cursor:default\" class=\"f";
                //this is when fontSize is called
                currentLine += fontSize(collectionOfWords, currKey, minCount,
                        maxCount);
                currentLine += "\" title =\"count: ";
                currentLine += currVal;
                currentLine += "\">";
                currentLine += currKey;
                currentLine += "</span>\n";
                outputFile.print(currentLine);
            
            currentLine = "";
            i++;


        }

    }

    /**
     * File Header for HTML File.
     *
     * @param outputFile
     *            file being written to
     * @param fileName
     *            name of file
     * @param countTitleWords
     *            number of words in title
     *
     * @updates outputFile
     *
     * @ensures outputFile = #outputFile + [HTML header of the file]
     */

    public static void fileHeader(PrintWriter outputFile, String fileName,
            int countTitleWords) {
        outputFile.println("<html>");

        //accounts for file name and title words
        outputFile.println("<head>");
        outputFile.println("<title>Top " + countTitleWords + " words in "
                + fileName + "</title>");
        outputFile.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        outputFile.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        outputFile.println("</head>");
        outputFile.println("<body>");

        //accounts for file name and title words
        outputFile.println("<h2>Top " + countTitleWords + " words in "
                + fileName + "</h2>");

        outputFile.println("<hr>");
        outputFile.println("<div class=\"cdiv\">");
        outputFile.println("<p class=\"cbox\">");

    }

    /**
     * File Footer for HTML File.
     *
     * @param outputFile
     *            file being written to
     *
     * @updates outputFile
     *
     * @ensures outputFile = #outputFile + [HTML footer of the file]
     */

    public static void fileFooter(PrintWriter outputFile) {
        //ends file
        outputFile.println("</p>");
        outputFile.println("</div>");
        outputFile.println("</body>");
        outputFile.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader fileRead = null;
        PrintWriter fileWrite = null;
        String inputFileName = "";
        String outputFileName = "";
        int maxTerms = 0;


        try {
            System.out.println("Name of input file: ");
            inputFileName = in.readLine();
            System.out.println("Max Word Count: ");
            maxTerms = Integer.parseInt(in.readLine());
            while(maxTerms < 0) {
                System.out.println("Max Word Count Must Be A Positive Integer, Enter Another: ");
                maxTerms = Integer.parseInt(in.readLine());
            }

            System.out.println("Name of output file: ");
            outputFileName = in.readLine();

        } catch (IOException e){
            System.err.println("User Input Could not be Read");
            return;
        }
        try {
            fileWrite = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));

        } catch (IOException e){
            System.err.println("Output File Could not be Read");
            return;
        }
        try {
            fileRead = new BufferedReader(new FileReader(inputFileName));
        } catch (IOException e){
            System.err.println("Input File Could not be Read");
            fileWrite.close();
            
            return;
        }

       
        
        Queue<String> words = splitWords(fileRead);
        Map<String, Integer> counts = countWords(words);
        int titleWords = Math.min(counts.size(), maxTerms);

        fileHeader(fileWrite, outputFileName, titleWords);

        fileBody(counts, fileWrite, maxTerms);
        fileFooter(fileWrite);
        
        
        fileWrite.close();
        try {
            fileRead.close();
        } catch (IOException e){
            System.err.println("Input File Could not be Closed");
            return;
        }
        try {
            in.close();
        } catch (IOException e){
            System.err.println("System Input Could not be Closed");
            return;
        }
    }

}
