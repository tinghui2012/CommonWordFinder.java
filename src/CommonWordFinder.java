import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Common Word Finder program for COMS 3134 Fall 2022 Final Project.
 * This program finds the n most common words in a text document using BSTMap, AVLTreeMap, and MyHashMap.
 * Takes in command line arguments with input file, data structure type, and count limit (optional)
 * java CommonWordFinder <filename> <bst|avl|hash> [limit]
 * @author Christine Li cl4315
 * @version 1.0 December 19, 2022
 */
public class CommonWordFinder {
    /**
     * The main method that parses command line argument with the input file, data structure type, and count limit (optional).
     * Step 1 checks the validity of command line arguments for errors.
     * Step 2 identifies unique words from reading in text input and inserts word with counts into BST, AVL or HashMap
     * Step 3 traverse through BST, AVL, or HashMap using Iterator and add entries into an array
     * Step 4 sort the array in descending order of word count. When two words have the same count, order alphabetically
     * Step 5 print the top n words and counts of the array in required formatting
     * @param args  the String array of two or three inputs, separated by a space
     *              the first argument is the name of the input text file residing in the same directory as this program
     *              the second argument is the data structure type: "bst", "avl", or "hash"
     *              the third (optional argument) is the limit for finding the most common words
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        BufferedReader reader = null;
        String type; // data structure type
        int limit = 10; // default limit is 10

        // STEP 1: Parse command line arguments + ERROR CHECKING

        // incorrect number of arguments (must be exactly 2 or 3 arguments)
        if (args.length != 2 && args.length != 3) {
            System.err.println("Usage: java CommonWordFinder <filename> <bst|avl|hash> [limit]");
            System.exit(1); // exit in failure
        }

        // verify input file exists in first argument, args[0]
        try {
            reader = new BufferedReader(new FileReader(args[0]));
        } catch (IOException e){
            System.err.println("Error: Cannot open file '" + args[0] + "' for input.");
            System.exit(1); // exit in failure
        }

        // check which data structure is specified ("bst", "avl", or "hash") in second argument, args[1]
        if (!args[1].equals("bst") && !args[1].equals("avl") && !args[1].equals("hash")) {
            System.err.println("Error: Invalid data structure '" + args[1] + "' received.");
            System.exit(1); // exit in failure
        }

        type = args[1];

        // process limit command line argument if applicable, args[2]
        if (args.length == 3) {
            if (Integer.parseInt(args[2]) < 1) {
                System.err.println("Error: Invalid limit '" + args[2] + "' received.");
                System.exit(1); // exit in failure
            } else {
                limit = Integer.parseInt(args[2]);
            }
        }

        // STEP 2: Parse input file

        // instantiate map with reference to MyMap interface, using polymorphism to create BST, AVL, or HashMap
        MyMap<String, Integer> map;
        if (type.equals("bst")) {
            map = new BSTMap<>();
        } else if (type.equals("avl")) {
            map = new AVLTreeMap<>();
        } else {
            map = new MyHashMap<>();
        }

        // read input text file character-by-character using BufferedReader
        // use StringBuilder to append characters to form words
        int ch = 0;
        StringBuilder wordBuilder = new StringBuilder();

        while (ch != -1) {
            try {
                ch = reader.read();
            } catch (IOException e) {
                System.err.println("Error: An I/O error occurred reading '" + args[0] + "'.");
                System.exit(1); // exit in failure
            }
            if (Character.isLowerCase((char)ch) || (char)ch == '\'') {
                wordBuilder.append((char)ch);
            } else if ((char)ch == '-' && wordBuilder.length() != 0) {
                wordBuilder.append((char)ch);
            } else if (Character.isUpperCase((char)ch)) {
                wordBuilder.append(Character.toLowerCase((char)ch));
            } else if (((char)ch == ' ' || (char)ch == '\n' || (char)ch == '\r') && !wordBuilder.isEmpty()) {
                if (map.get(wordBuilder.toString()) == null) {
                    // new unique word
                    map.put(wordBuilder.toString(), 1);
                } else {
                    // word already exists in map, increment count
                    map.put(wordBuilder.toString(), map.get(wordBuilder.toString()) + 1);
                }
                wordBuilder.setLength(0); // restart word
            }
        }

        // add the leftover word in wordBuilder
        if (!wordBuilder.isEmpty()) {
            if (map.get(wordBuilder.toString()) == null) {
                // new word
                map.put(wordBuilder.toString(), 1);
            } else {
                map.put(wordBuilder.toString(), map.get(wordBuilder.toString()) + 1);
            }
        }

        System.out.println("Total unique words: " +map.size());
        if (limit > map.size()) {
            limit = map.size();
        }

        // STEP 3: Traverse through map using Iterator and dump everything into an array
        Entry<String, Integer>[] ranked = new Entry[map.size()];
        Iterator<Entry<String, Integer>> iterator = map.iterator();
        for (int i = 0; i < ranked.length; i++) {
            if (iterator.hasNext()) {
                ranked[i] = iterator.next();
            }
        }

        // STEP 4: Sort the array using Arrays.sort()
        // use a lambda function to sort the array in descending order by Entry value
        // if two Entries have the same value, order by key alphabetically
        // https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html#sort(T[],%20java.util.Comparator)
        Arrays.sort(ranked, (ent1, ent2) -> {
            if (ent2.value.compareTo(ent1.value) != 0) {
                return ent2.value.compareTo(ent1.value);
            } else {
                return ent1.key.compareTo(ent2.key);
            }
        });

        // STEP 5: Display output

        // FORMATTING output: LEFT ALIGN to count of the longest word
        int numWidth = String.valueOf(limit).length();
        int leadingSpaces; // tracks spacing before row numbers
        int middleSpaces; // tracks spacing between word and count
        int wordWidth = 0; // width of the longest word
        // find width of the longest word
        for (int i = 0; i < limit; i++) {
            if (ranked[i].key.length() > wordWidth) {
                wordWidth = ranked[i].key.length();
            }
        }
        // format and print out each word and count in sorted array up to the limit
        for (int i = 1; i <= limit; i++) {
            leadingSpaces = numWidth - String.valueOf(i).length();
            middleSpaces = wordWidth + 1 - ranked[i-1].key.length();
            String line = "";
            if (leadingSpaces > 0) {
                line = String.format("%1$"+leadingSpaces+ "s", " ");
            }
            line += i + ". " + ranked[i-1].key + String.format("%1$"+middleSpaces+"s", " ") + ranked[i-1].value + System.lineSeparator();
            System.out.print(line);
        }
    }
}

// SCRATCH WORK CODE
//        System.out.println(map);
//        Entry<String, Integer>[] ranked = new Entry[limit];
//        Iterator<Entry<String, Integer>> iterator;
//        int prevMax;
//        for (int i = 0; i < ranked.length; i++) {
//            iterator = map.iterator();
//            Entry<String, Integer> max = new Entry<>("", 0);
//            prevMax = 0;
//            while (iterator.hasNext()) {
//                Entry<String, Integer> current = iterator.next();
//                if (current.value > max.value) {
//                    max = current;
//                }
//            }
//            map.remove(max.key);
//            ranked[i] = max;
//        }


//probably a dumb slow method: dump everything into an array
// but no! since bst is already in alphabetical order, it would always find the next most common word alphabetically first

//        for (Entry<String, Integer> word : ranked) {
//            if (word.key.length() > wordWidth) {
//                wordWidth = word.key.length();
////                countWidth = String.valueOf(word.value).length();
//            }
//        }
//        int totalWidth = wordWidth + 1 + countWidth;
//        System.out.println("WORD WIDTH " +wordWidth);
//        System.out.println("COUNT WIDTH " +countWidth);
//        System.out.println("TOTAL WIDTH " +totalWidth);
//        Entry<String, Integer>[] ranked = new Entry[limit];
//        Iterator<Entry<String, Integer>> iterator;
//        int prevMax = 0;
//        for (int i = 0; i < ranked.length; i++) {
//            iterator = map.iterator();
//            Entry<String, Integer> max = new Entry<>("", 0);
//            while (iterator.hasNext()) {
//                Entry<String, Integer> current = iterator.next();
////                System.out.println("CURRENT: "+current.key+" "+current.value);
//                if (current.value > max.value
//                        && (i==0 || current.value < ranked[prevMax].value)) {
//                    max = current;
//                    System.out.println("CASE 1 NEW MAX: " + max.key+ " "+max.value);
//                } else if (current.value > max.value
//                        && (current.value == ranked[prevMax].value && current.key.compareTo(ranked[prevMax].key) > 0)){
//                    max = current;
//                    System.out.println("CASE 3 NEW MAX: " + max.key+ " "+max.value);
//                } else if (i > 0 && current.value == max.value && max.value == ranked[prevMax].value && current.key.compareTo(ranked[prevMax].key) > 0){
//                    max = current;
//                    System.out.println("CASE 2 NEW MAX: " + max.key+ " "+max.value);
//                }
//            }
//            ranked[i] = max;
//            prevMax = i;
//            System.out.println("prevMax INDEX: " + prevMax);
//            System.out.println("prevMax: "+ranked[prevMax].key + " "+ranked[prevMax].value);
//            System.out.println("pass count: "+map.get("pass"));
//            if (i > 0 && max.value.equals(ranked[prevMax].value)) {
//                System.out.println("new MAX "+ max.value+" EQUALS old MAX"+ranked[prevMax].value);
//                int j;
//                for (j = prevMax; j < i; j++) {
//                    if (max.key.compareTo(ranked[j].key) < 0) {
//                        break;
//                    }
//                }
//                for (int k = i; k > j; k--) {
//                    ranked[k] = ranked[k-1];
//                }
//                ranked[j] = max;
//            } else {
//                ranked[i] = max;
//                prevMax = i;
//            }
//
//        Entry<String, Integer>[] ranked = everything;

//        System.out.println(map);
// loop to find nth most common words
//        Entry<String, Integer>[] ranked = new Entry[limit];
//        for (int i = 0; i < limit; i++) {
//            iterator = map.iterator();
//            Entry<String, Integer> max = new Entry<>("", 0);
//            int prevMax = 0;
//            while (iterator.hasNext()) {
//                Entry<String, Integer> current = iterator.next();
//                if (current.value > max.value && (i == 0 || (i > 0 && current.value <= ranked[prevMax].value && !current.key.equals(ranked[prevMax].key)))) {
//                    max = current;
//                }
//            }
//            System.out.println("ADDING at " + i+": " + max.key+" "+max.value);
//            if (i > 0 && max.value == ranked[prevMax].value) {
//                System.out.println("SAME COUNT");
//                int j;
//                for (j = prevMax; j < i; j++) {
//                     if (ranked[i].key.compareTo(ranked[j].key) < 0) {
//                         break;
//                     }
//                }
//                for (int k = i; k > j; k--) {
//                    ranked[k] = ranked[k-1];
//                }
//                ranked[j] = max;
//            } else {
//                ranked[i] = max;
//                prevMax = i;
//            }
//            //System.out.println(ranked[i]);
//        }
//        while (ch != -1) {
//            try {
//                ch = input.read();
//            } catch (IOException e) {
//                System.err.println("Error: An I/O error occurred reading '" + args[0] + "'.");
//                System.exit(1); // exit in failure
//            }
//
//            if (Character.isLowerCase((char)ch) || (char)ch == '\'') {
//                wordBuilder.append((char)ch);
//            } else if ((char)ch == '-' && wordBuilder.length() != 0) {
//                wordBuilder.append((char)ch);
//            } else if (Character.isUpperCase((char)ch)) {
//                wordBuilder.append(Character.toLowerCase((char)ch));
//            } else if (((char)ch == ' ' || (char)ch == '\n' || (char)ch == '\r') && !wordBuilder.isEmpty()) {
//                if (map.get(wordBuilder.toString()) == null) {
//                    // new word
//                    map.put(wordBuilder.toString(), 1);
//                } else {
//                    map.put(wordBuilder.toString(), map.get(wordBuilder.toString()) + 1);
//                }
////                System.out.println(wordBuilder.toString());
//                wordBuilder.setLength(0); // restart word
//            }
//        }