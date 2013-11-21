import java.util.Vector;
import smile.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class WordPrediction {

	public static WordNetwork net;

	public static void main(String args[]) throws FileNotFoundException
	{
		net = new WordNetwork();
		
		int nGramLength = 2;
		
		learnNewFile("ToTC.txt", nGramLength);
		
		//net.learn("The dogs name is Jack", nGramLength);
		//net.learn("My first name was Jim", nGramLength);
		//net.learn("If my name is Henry then what is your name", nGramLength);
		//net.learn("If my dogs name is John then what is his breed", nGramLength);
		//net.learn("What color is the dog with name Alex", nGramLength);
		
		net.save("test.xdsl");
		
		//tryPhrase("My first name was");
		//tryPhrase("The dogs name is");
		//tryPhrase("Our last dog was");
		//tryPhrase("If my name is");
		
		tryPhrase("It was the");
		tryPhrase("When the world was");
		tryPhrase("There was a king");
		
	}
	
	public static void tryPhrase(String phrase){
		System.out.println(phrase + "... " + net.predictNextWord(phrase));
	}
	
	public static void learnNewFile(String fileName, int nGramLength) throws FileNotFoundException
	{
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);
		
		String build = "";
		String add = "";
		//ArrayList<String> sentenceList = new ArrayList<String>();
		
		while(scanner.hasNext())
		{
			add = scanner.next();
			build = build + " " + add;
			if(add.endsWith(".") || add.endsWith("!") || add.endsWith("?") || add.endsWith(";"))
			{
				build = build.replace(".", "");
				build = build.replace(",", "");
				build = build.replace("!", "");
				build = build.replace("-", "");
				build = build.replace("?", "");
				build = build.replace("(", "");
				build = build.replace(")", "");
				build = build.replace(";", "");
				build = build.replace(":", "");
				build = build.replace("\"", "");
				build = build.replace("\'", "");
				build = build.replace("﻿", "");
				build = build.replaceFirst(" ", "");
				
				System.out.println(build);
				//sentenceList.add(build);
				net.learn(build, nGramLength);
				
				build = "";	
			}
		}	
	}	
}
