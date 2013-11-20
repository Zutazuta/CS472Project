import java.util.Iterator;
import java.util.Vector;
import smile.*;
import smile.learning.*;

class WordNetwork {

	private Network net;

	public WordNetwork() {
		net = new Network();
	}
	
	public void learn(String text, int nGramLength){
		String[] words = text.split(" ");
		Vector<String> nGram = new Vector<String>();
		
		// Loop over the words in the text and add them to the network
		for(int i=0; i<words.length-1; i++){
			if(!wordExists(words[i])){
				// Add the individual word to the network
				addWord(words[i]);
			}
			if(!wordExists(words[i+1])){
				// Add the next word to the network
				addWord(words[i+1]);
			}
			
			// Create an nGram of  the last few words
			nGram.add(words[i]);
			if(nGram.size() > nGramLength){
				nGram.remove(0);
			}
			
			// Learn the transition between the nGram and the next word
			transition(nGram, words[i+1]);
		}
	}
	
	public void addWord(String word){
		// Add a word to the network. The word is a single variable with states "Present" and "Not Present"
		net.addNode(Network.NodeType.Cpt, word);
		net.setOutcomeId(word, 0, "Present");
		net.setOutcomeId(word, 1, "NotPresent");
	}
	
	public boolean wordExists(String word){
		// Check to see if the word is already in the network
		try{
			net.getNode(word);
			return true;
		}catch(smile.SMILEException e){
			return false;
		}
	}
	
	public void transition(Vector<String> nGram, String toWord){
		// Learn the transition between an nGram and a particular word
		Iterator<String> nGramItr = nGram.iterator();
		
		DataSet ds = new DataSet(); // A table containing a single row. This is used
									// to build the conditional probabilities between words
									// in the network. The table looks like this:
									// nGram[0]		nGram[1]	toWord
									//	present	    present     present
		
		String gram = "";
		
		while(nGramItr.hasNext()){
			// For each word in the nGram add an arc to the final word
			gram = nGramItr.next();
			try{
				net.addArc(gram, toWord);
			}catch(Exception e){
				// Arc already exists
			}
			
			// Create a column for each word in the nGram
			try{
				ds.addIntVariable(gram, -1);
			}catch(Exception e){
				nGram.removeElement(gram);
				// Probably created a cycle
			}
			
		}
		
		// Add the final word as a column in the dataset
		boolean toWordCreatesCycle = false;
		try{
			ds.addIntVariable(toWord, -1);
		}catch(Exception e){
			toWordCreatesCycle = true;
			// Probably created a cycle
		}
		
		ds.addEmptyRecord(); // The row that will contain the variable values
		
		// Add the value "present" to each variable in the table
		int counter = 0;
		nGramItr = nGram.iterator();
		while(nGramItr.hasNext()){
			nGramItr.next();
			ds.setInt(counter, 0, 0); // The third argument is 0 because that is the index of "Present"
			counter++;
		}
		
		if(!toWordCreatesCycle){
			ds.setInt(counter, 0, 0); // The third argument is 0 because that is the index of "Present"
		}
		
		//printDataSet(ds);

		DataMatch[] m = ds.matchNetwork(net); // Match the dataset columns to variables in the network
		
		// Use the expectation–maximization algorithm to learn the network parameters
		EM em = new EM();
		em.learn(ds, net, m);
	}
	
	public String predictNextWord(String afterPhrase){
		// Return the word that is most likely to come after the phrase
		String[] words = afterPhrase.split(" ");
		Vector<String> nGram = new Vector<String>();
		
		for(int i=0; i<words.length; i++){
			nGram.add(words[i]);
		}
		
		return predictNextWord(nGram);
	}
	
	public String predictNextWord(Vector<String> nGram){
		// Return the word that is most likely to come after the phrase
		Iterator<String> nGramItr = nGram.iterator();
		
		String gram = "";
		String lastGram = "";
		
		// Set each word in the nGram as evidence
		while(nGramItr.hasNext()){
			gram = nGramItr.next();
			
			try{
				net.clearEvidence(gram);
			}catch(Exception e){
				// No evidence to clear
			}
			
			try{
				net.setEvidence(gram, "Present");
				lastGram = gram; // This is the last word that is in the network. 
								 // We will look at its children when determining 
								 // what the next word should be.
			}catch(Exception e){
				// Could not find node
			}
		}
		
		net.updateBeliefs(); // Update the network based on the evidence
		
		if(lastGram != ""){
			// Look at each child node of the last word in the nGram and determine
			// which has the highest probability of coming next
			int[] children = net.getChildren(lastGram);
			
			int mostLikelyChildId = -1;
			double mostLikelyChildValue = 0;
			
			for(int i=0; i<children.length; i++){
				double value = net.getNodeValue(children[i])[0]; // 0=Present
				//System.out.println(net.getNodeId(children[i]) + "=" + value);
				if(value >= mostLikelyChildValue){
					mostLikelyChildValue = value;
					mostLikelyChildId = children[i];
				}
			}
			if(mostLikelyChildId != -1){
				return net.getNodeId(mostLikelyChildId);
			}else{
				// No children found. The next word is unkown.
				return "";
			}
		}else{
			// None of the words were in the network, so there is no way to know what would come next.
			return "";
		}
		
	}
	
	public void save(String filename){
		// Save the network to a file
		net.writeFile(filename);
	}
	
	private void printDataSet(DataSet ds){
		// Utility method to print the dataset object while debugging
		String heading = "";
		for(int i=0; i<ds.getVariableCount(); i++){
			heading += ds.getVariableId(i) + "\t";
		}
		System.out.println(heading);
		
		for(int i=0; i<ds.getRecordCount(); i++){
			String row = "";
			for(int j=0; j<ds.getVariableCount(); j++){
				row += ds.getInt(j, i) + "\t";
			}
			System.out.println(row);
		}
		
		System.out.println("\n");
	}
	
}