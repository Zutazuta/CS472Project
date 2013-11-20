import java.util.Vector;
import smile.*;

public class WordPrediction {

	public static WordNetwork net;

	public static void main(String args[]) {
		net = new WordNetwork();
		
		int nGramLength = 2;
		
		net.learn("The dogs name is Jack", nGramLength);
		net.learn("My first name was Jim", nGramLength);
		net.learn("If my name is Henry then what is your name", nGramLength);
		net.learn("If my dogs name is John then what is his breed", nGramLength);
		net.learn("What color is the dog with name Alex", nGramLength);
		
		net.save("test.xdsl");
		
		tryPhrase("My first name was");
		tryPhrase("The dogs name is");
		tryPhrase("Our last dog was");
		tryPhrase("If my name is");
		
	}
	
	public static void tryPhrase(String phrase){
		System.out.println(phrase + "... " + net.predictNextWord(phrase));
	}
	
}
