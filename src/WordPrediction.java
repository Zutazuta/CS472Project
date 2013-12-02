import java.util.Vector;
import smile.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import java.awt.Color;

public class WordPrediction {

	public static WordNetwork net;
	
	public String structure = "markov-chain";

	public static void main(String args[]) throws FileNotFoundException
	{
		//net = new BayesianNetwork();
		net = new MarkovChain();
		
		int nGramLength = 2;
		
		learnNewFile("ToTC.txt", nGramLength);
		
		//net.learn("The dogs name is Jack", nGramLength);
		//net.learn("My first name was Jim", nGramLength);
		//net.learn("If my name is Henry then what is your name", nGramLength);
		//net.learn("If my dogs name is John then what is his breed", nGramLength);
		//net.learn("What color is the dog with name Alex", nGramLength);
		
		//net.save("test.xdsl");
		
		//tryPhrase("My first name was");
		//tryPhrase("The dogs name is");
		//tryPhrase("Our last dog was");
		//tryPhrase("If my name is");
		
		tryPhrase("It was the");
		tryPhrase("When the world was");
		
		tryPhrase("life guards had");
		tryPhrase("There was a king");
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new WordPredictionPanel(net);
			}
		});
	}
	
	public static void tryPhrase(String phrase){
		System.out.println(phrase + "... " + net.predictNextWord(phrase, 4));
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
