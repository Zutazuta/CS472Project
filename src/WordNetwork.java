import java.util.Iterator;
import java.util.Vector;
import smile.*;
import smile.learning.*;

class WordNetwork {

	private Network net;

	public WordNetwork() {
		net = new Network();
	}
	
	public void learn(String text, int n){
		String[] words = text.split(" ");
		Vector<String> nGram = new Vector<String>();
		
		for(int i=0; i<words.length-1; i++){
			if(!wordExists(words[i])){
				addWord(words[i]);
			}
			if(!wordExists(words[i+1])){
				addWord(words[i+1]);
			}
			
			nGram.add(words[i]);
			if(nGram.size() > n){
				nGram.remove(0);
			}
			
			transition(nGram, words[i+1]);
		}
	}
	
	public void addWord(String word){
		net.addNode(Network.NodeType.Cpt, word);
		net.setOutcomeId(word, 0, "Present");
		net.setOutcomeId(word, 1, "NotPresent");
	}
	
	public boolean wordExists(String word){
		try{
			net.getNode(word);
			return true;
		}catch(smile.SMILEException e){
			return false;
		}
	}
	
	public void transition(Vector<String> nGram, String toWord){
		Iterator<String> nGramItr = nGram.iterator();
		
		DataSet ds = new DataSet();
		String gram = "";
		
		while(nGramItr.hasNext()){
			gram = nGramItr.next();
			try{
				net.addArc(gram, toWord);
			}catch(Exception e){
				// Arc already exists
			}
			
			try{
				ds.addIntVariable(gram, -1);
			}catch(Exception e){
				nGram.removeElement(gram);
				// Probably a cycle
			}
			
		}
		
		boolean toWordCreatesCycle = false;
		try{
			ds.addIntVariable(toWord, -1);
		}catch(Exception e){
			toWordCreatesCycle = true;
			// Probably a cycle
		}
		
		ds.addEmptyRecord();
		
		nGramItr = nGram.iterator();
		int counter = 0;
		while(nGramItr.hasNext()){
			nGramItr.next();
			ds.setInt(counter, 0, 1);
			counter++;
		}
		
		if(!toWordCreatesCycle){
			ds.setInt(counter, 0, 1);
		}
		
		// Corresponds to table
		// Word1	Word2	toWord
		//	t		t		t
		
		DataMatch[] m = ds.matchNetwork(net);
		
		EM em = new EM();
		
		em.learn(ds, net, m);		
	}
	
	public String predictNextWord(String afterPhrase){
		String[] words = afterPhrase.split(" ");
		Vector<String> nGram = new Vector<String>();
		
		for(int i=0; i<words.length; i++){
			nGram.add(words[i]);
		}
		
		return predictNextWord(nGram);
	}
	
	public String predictNextWord(Vector<String> nGram){
		Iterator<String> nGramItr = nGram.iterator();
		
		String gram = "";
		String lastGram = "";
		
		while(nGramItr.hasNext()){
			gram = nGramItr.next();
			
			try{
				net.clearEvidence(gram);
			}catch(Exception e){
				// No evidence to clear
			}
			
			try{
				net.setEvidence(gram, "Present");
				lastGram = gram;
			}catch(Exception e){
				// Could not find node
			}
		}
		
		net.updateBeliefs();
		
		if(lastGram != ""){
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
				//System.out.println(net.getNodeId(mostLikelyChildId));
				return net.getNodeId(mostLikelyChildId);
			}else{
				return "";
				//System.out.println("unknown");
			}
		}else{
			// None of the words were in the network, so there is no way to know what would come next.
			//System.out.println("who knows");
			return "";
		}
		
	}
	
	public void save(String filename){
		net.writeFile(filename);
	}
	
}