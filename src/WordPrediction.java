import java.util.Vector;
import smile.*;

public class WordPrediction {

	public static WordNetwork net;

	public static void main(String args[]) {
		net = new WordNetwork();
		
		net.learn("Please drink your tea while it is still hot", 3);
		net.learn("Why would anybody drink tea when it is cold", 3);
		net.learn("Somewhere in the world somebody is drinking tea luke warm", 3);
		
		//net.save("test.xdsl");
		
		tryPhrase("drink your tea");
		tryPhrase("somebody is drinking tea");
		
	}
	
	public static void tryPhrase(String phrase){
		System.out.println(phrase + "... " + net.predictNextWord(phrase));
	}
	
}
