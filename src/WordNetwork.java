import java.util.Iterator;
import java.util.ArrayList;

public abstract class WordNetwork {
	
	abstract public void learn(String text, int nGramLength);
	
	abstract public String predictNextWord(ArrayList<String> nGram);
	
	public String predictNextWord(String afterPhrase, int nGramLength){
		// Return the word that is most likely to come after the phrase
		String[] words = afterPhrase.split(" ");
		ArrayList<String> nGram = new ArrayList<String>();
		
		int start = words.length-nGramLength;
		if(start < 0) start = 0;
		
		for(int i=start; i<words.length; i++){
			nGram.add(words[i]);
		}
		
		return predictNextWord(nGram);
	}
	
}