import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class Pipo {
    String f1;                                              // the learning file
    Scanner in1;                                            // the scanner associated to the file
    HashMap<String, HashMap<String,Float>> LangModel;       // dictionnaire des occurences des bigrammes
    HashMap<String, Integer> Occurences;                    // dictionnaire des occurences des mots
    Random generator;
    
    Pipo(String f1) {
        /* language model constructor */
        this.f1 = f1;
        try {in1 = new Scanner(new FileInputStream(f1)); }
        catch (Exception e) {System.out.println(e);}
        LangModel = new HashMap<String, HashMap<String,Float>>();
        Occurences = new HashMap<String, Integer>();
    }
    
    public void newWorsSeq(String w1, String w2) {
        // if w1 exists we update the number of occurences in Occurences
        if(LangModel.containsKey(w1)) {
            // if bigram w1, w2 exists we update the number of occurences of the bigram in Pij
            if(LangModel.get(w1).containsKey(w2)) {
                Float nbij = LangModel.get(w1).get(w2) + 1;
                LangModel.get(w1).put(w2, nbij);
            } else {
            // if bigram w1, w2 dosn't exists we add it to Pij
                LangModel.get(w1).put(w2, Float.valueOf(1));
            }
            Occurences.put(w1, Occurences.get(w1) + 1);

        // if w1 doesn't exist we add it to Occurences
        } else {
            LangModel.put(w1, new HashMap<String, Float>());
            LangModel.get(w1).put(w2, Float.valueOf(1));
            Occurences.put(w1, Integer.valueOf(1));
        }
    }
    
    public void Learn() {
        String word1;
        // A ghost word beeing before the first word of the text
        word1=".";
        try {
            while (in1.hasNext()) {
                String word2 = in1.next();
                if (word2.matches("(.*)[.,!?<>=+-/]")) {
                    // word2 is glued with a punctuation mark
                    String[] splitedWord= word2.split("(?=[.,!?<>=+-/])|(?<=])");
                    for (String s : splitedWord) {
                        newWorsSeq(word1,s);                // update de language model
                        word1=s;
                    }
                } else {
                    // word2 is a single word
                    newWorsSeq(word1,word2);                // update de language model
                    word1=word2;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        // generate bigram probabilities
        occurencesToProba();
    }

    public void occurencesToProba() {
        /* generates bigram probabilities */
        Iterator<String> keys = Occurences.keySet().iterator();

        // for every word
        while(keys.hasNext()) {
            String key = keys.next();
            Iterator<String> subkeys = LangModel.get(key).keySet().iterator();
            // for every word that is a successor of the current word
            while(subkeys.hasNext()) {
                String skey = subkeys.next();
                // we calculate the probability of the bigram (sequence of two words)
                LangModel.get(key).put(skey, LangModel.get(key).get(skey) / Occurences.get(key));
            }
        }
    }

    public String nextWord(String word) {
        // random float between 0 and 1 
        Random r = new Random();
        Float alea = r.nextFloat();

        // list of successors words of the current word
        HashMap<String, Float> words = LangModel.get(word);

        Iterator<String> keys = words.keySet().iterator();
        Double curval = 0.0;
        String out;

        // picking a word based on probability intervals
        do {
            out = keys.next();
            curval += Double.valueOf(words.get(out));
        } while(alea > curval);

        return out;
    }

    public void Talk(int nbWord) {
        Random r = new Random();
        // we create the word set
		List<String> keysAsArray = new ArrayList<String>(Occurences.keySet());

        // we initialise the text to be generated
        String text = "";
        // the first word is chosen randmoly from the word set
        String word = keysAsArray.get(r.nextInt(keysAsArray.size()));
        // we add the first word to the text
        text += word;
        for(int i=0; i < nbWord; i++) {
            // we generate the next word
            word = nextWord(word);
            text += " " + word;
        }
        // we print the generated text
        System.out.println("Compte rendu de l'apnée 3 algo 6 :");
        System.out.println();
        System.out.println(text);
        System.out.println();
        System.out.println("Fin du compte rendu");
    }
}
