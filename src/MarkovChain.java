import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

class MarkovChain extends WordNetwork {

	private HashMap<String, LinkedList<Arc>> _nGrams;

	public MarkovChain() {
		_nGrams = new HashMap<String, LinkedList<Arc>>();
	}
	
	public void addTransition(String fromNGram, String toNGram){
		if(!_nGrams.containsKey(fromNGram)){
			// nGram not already seen
			_nGrams.put(fromNGram, new LinkedList<Arc>());
		}
		
		LinkedList<Arc> arcs = _nGrams.get(fromNGram);
		
		ListIterator arcsItr = arcs.listIterator();
		boolean found = false;
		
		while(arcsItr.hasNext()){
			Arc current = (Arc)arcsItr.next();
			if(current.getNGram() == toNGram){
				current.increment();
				found = true;
			}
		}
		
		if(!found){
			arcs.add(new Arc(toNGram));
		}
		
	}
	
	public void learn(String text, int nGramLength){
		String[] words = text.split(" ");
		ArrayList<String> nGram = new ArrayList<String>();
		
		// Loop over the words in the text and add them to the network
		for(int i=0; i<words.length-1; i++){
			addTransition(words[i], words[i+1]);
			
			// Create an nGram of  the last few words
			nGram.add(words[i]);
			if(nGram.size() > nGramLength){
				nGram.remove(0);
			}
			
			// Create a string from the nGram
			String fromNGram = "";
			Iterator<String> nGramItr = nGram.iterator();
			while(nGramItr.hasNext()){
				if(fromNGram == ""){
					fromNGram = nGramItr.next();
				}else{
					fromNGram += " " + nGramItr.next();
				}
			}
			
			// Learn the transition between the nGram and the next word
			addTransition(fromNGram, words[i+1]);
		}
	}
	
	public String predictNextWord(ArrayList<String> nGram){
		// Start at the last word and gradually increase the length of the 
		// phrase we are searching for until no match is found
		String phrase = "";
		String mostProbableWord = "";
		
		for(int i=nGram.size()-1; i >= 0; i--){
			if(phrase == ""){
				phrase = nGram.get(i);
			}else{
				phrase = nGram.get(i) + " " + phrase;
			}
			
			if(_nGrams.get(phrase) != null){
				LinkedList<Arc> arcs = _nGrams.get(phrase);
				
				String bestWord = "";
				int highestCount = 0;
				for(int j=0; j<arcs.size(); j++){
					if(arcs.get(j).getCount() > highestCount){
						bestWord = arcs.get(j).getNGram();
						highestCount = arcs.get(j).getCount();
					}
				}
				
				mostProbableWord = bestWord;
			}		
		}
	
		return mostProbableWord;
			
	}
	
	public HashMap<String, LinkedList<Arc>> getNGrams(){
		return _nGrams;
	}
	
}


class Arc {
	private final String  _nGram;
	private int _count;
	
	public Arc(String nGram){
		_nGram = nGram;
		_count = 1;
	}
	
	public String getNGram(){
		return _nGram;
	}
	
	public int getCount(){
		return _count;
	}
	
	public void increment(){
		_count++;
	}
}