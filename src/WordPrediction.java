import java.util.Vector;
import smile.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import java.awt.Color;

public class WordPrediction {

	public static WordNetwork net1;
	public static WordNetwork net2;
	
	public static void main(String args[]) throws FileNotFoundException
	{
		net1 = new BayesianNetwork();
		net2 = new MarkovChain();
		
		int nGramLength = 2;
		
		learnNewFile("ToTC.txt", nGramLength);
		
		//net1.save("test.xdsl");
		
		tryPhrase("It was the best of");
		tryPhrase("When the world was");
		
		tryPhrase("life guards had");
		tryPhrase("There was a king");
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new WordPredictionPanel(net1, net2);
			}
		});
	}
	
	public static void tryPhrase(String phrase){
		System.out.println(phrase + "... [Bayesian: " + net1.predictNextWord(phrase, 4) + "] [Markov: " + net2.predictNextWord(phrase, 4) + "]" );
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
				net1.learn(build, nGramLength);
				net2.learn(build, nGramLength);
				
				build = "";	
			}
		}	
	}	
}
