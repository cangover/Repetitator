import java.util.*;
import java.io.*;

public class RepetitatorEfficient
{
	Scanner reader;

	public RepetitatorEfficient ()
	{
		reader = new Scanner(System.in);
	}
	
	public static void main(String[] args) throws IOException
	{
		RepetitatorEfficient part = new RepetitatorEfficient();
		boolean done = false;
		while(!done){
			boolean good = false;
			while(!good){
				int option = part.getUserInput();
				switch(option){
				case 1: part.run1();
					good = true;
					break;
				case 2: part.run2();
					good = true;
					break;
				case 3: part.run3();
					good = true;
					break;
				case 4: done = true;
					good = true;
				}
			}
		}
	}
	
	public void run1 ()
	{
		System.out.print("What is the name of the file to scan?");
		String file = reader.next();
		System.out.print("What piece of text would you like to look for?");
		String text = reader.next();
		BufferedReader buff = openReader(file);
		long reps = 0;
		try{
			String str = buff.readLine();
			reps = findInstances(str, text, reps, buff);
			
		}catch (IOException e) {
			System.out.println("Error ­­ " + e.toString());
		}
		System.out.println("Your text was found " + reps + " times in the text file.");
	}

	public void run2 ()
	{
		System.out.print("What is the name of the file to scan?");
		String file = reader.next();
		System.out.print("How many of the top sentences in the file would you like found (between 1 and 10)?");
		long num = reader.nextLong();
		System.out.print("What would you like the minimum length of a sentence to be (in words)?");
		long minLength = reader.nextLong();
		BufferedReader buff = openReader(file);
		String[][] common = countInstances(buff, true, num, minLength);
		for (int i = 0; i < common.length; i++) {
			if(common[i][1].equals("0"))
				System.out.println("There are not this many sentences of the specified length in the document.");
			else
				System.out.println((i+1) + ". \"" + common[i][0] + "\" was found " + common[i][1] + " times.");
		}
	}

	public void run3 ()
	{
		System.out.print("What is the name of the file to scan?");
		String file = reader.next();
		System.out.print("How many of the top words in the file would you like found (between 1 and 10)?");
		long num = reader.nextLong();
		System.out.print("What would you like the minimum length of a word to be?");
		long minLength = reader.nextLong() - 2;
		BufferedReader buff = openReader(file);
		String[][] common = countInstances(buff, false, num, minLength);
		for (int i = 0; i < common.length; i++) {
			if(common[i][1].equals("0"))
				System.out.println("There are not this many words of the specified length in the document.");
			else
				System.out.println((i+1) + ". \"" + common[i][0] + "\" was found " + common[i][1] + " times.");
		}
	}

	private int findNextWord(String str, int ind){
		boolean found = false;
		while(!found){
			int c = (int)str.charAt(ind);
			if((c>64 && c<91) || (c>96 && c<123))
				return ind;
			ind++;
		}
		return ind;
	}
	
	private int countWhiteSpaces(String str){
		int num = 0;
		for(int i = 0; i < str.length(); i++){
			if(str.charAt(i) == ' ')
				num++;
		}
		return num;
	}
	
	private int findNextNonCar(String str, int ind){
		boolean found = false;
		while(!found){
			int c = (int)str.charAt(ind);
			if(!((c>64 && c<91) || (c>96 && c<123)))
				return ind;
			ind++;
		}
		return ind;
	}

	public BufferedReader openReader(String filename){
		try {
			FileReader file = new FileReader(filename);
			BufferedReader buff = new BufferedReader(file);
			return buff;
		} catch (IOException e) {
			System.out.println("Error ­­ " + e.toString());
		}
		return null;
	}

	private long findInstances(String str, String text, long reps, BufferedReader buff) throws IOException{
		while(str != null){
			while(str.length() >= text.length()){
				String s = str.substring(0, text.length());
				if(s.equalsIgnoreCase(text))
					reps++;
				if(str.indexOf(' ') > 0)
					str = str.substring(str.indexOf(' ') + 1);
				else
					str = "";
			}
			str = buff.readLine();
		}
		return reps;
	}

	private String[][] countInstances(BufferedReader buff, boolean isSentence, long num, long minLgth){
		Hashtable<String, Long> sentences = new Hashtable<String, Long>();
		String common[][] = new String [(int)num][2];
		for(int i = 0; i < common.length; i++){
			common[i][0] = "placeholder";
			common[i][1] = "0";
		}
		long end = 0;
		boolean done = false;
		try{
			String line = buff.readLine();
			while(line != null){
				String temp = buff.readLine();
				boolean endsWP = false;
				if(line.length() > 1 && line.lastIndexOf('.') >= line.length() - 2)
					endsWP = true;
				while(!endsWP){
					while(temp == null)
						temp = buff.readLine();
					line += temp;
					if(line.length() > 1 && line.lastIndexOf('.') >= line.length() - 2)
						endsWP = true;
					temp = null;
				}
				String text = "";
				done = false;
				do{
					text = "";
					if(isSentence){
						long period = line.indexOf('.');
						long exclam = line.indexOf('!');
						long quest = line.indexOf('?');
						if(period == -1)
							period = Long.MAX_VALUE;
						if(exclam == -1)
							exclam = Long.MAX_VALUE;
						if(quest == -1)
							quest = Long.MAX_VALUE;
						end = Math.min(Math.min(period, exclam), quest);
						text = line.substring(0, (int)end);
						if(line.length() < (int)end + 3 || (int)line.charAt((int)end + 2) < 65)
							done = true;
						else
							line = line.substring(findNextWord(line, (int)end));
					}
					else{
						end = (long)findNextNonCar(line, 0);
						boolean found = false;
						long nextWInd = end;
						while(!found){
							long c = (long)line.charAt((int)nextWInd);
							if(!(c<65 || (c>90 && c<97) || c>122))
								found = true;
							nextWInd++;
							if(nextWInd == line.length()){
								found = true;
								done = true;
							}
						}
						text = line.substring(0, (int)end);
						line = line.substring((int)nextWInd - 1);
					}
					if((!isSentence && text.length() > minLgth) || (isSentence && (int)countWhiteSpaces(text) >= minLgth)){
						boolean in = false;
						long reps = 1;
						if(sentences.containsKey(text))
							reps = sentences.get(text) + 1;
						sentences.remove(text);
						sentences.put(text, reps);
						for(int i = 0; i < common.length; i++){
							if(Long.parseLong(common[i][1]) < reps && !in){
								common[i][0] = text;
								common[i][1] = "" + reps;
								in = true;
							}
						}
						if(!isSentence){
							if(!(line.indexOf('.') + 1 < line.length()))
								done = true;
						}
					}
				} while(!done);
				line = buff.readLine();
			}
		}catch (IOException e) {
			System.out.println("Error ­­ " + e.toString());
		}
		return common;
	}

	public int getUserInput(){
		System.out.println("Which option would you like to do today?");
		System.out.println("1. Find a specific piece of text in a file.");
		System.out.println("2. Find the most common sentences in a file.");
		System.out.println("3. Find the most common words in a file");
		System.out.println("4. Exit the program");
		System.out.print("Option number (between 1 and 4) -> ");
		int input = reader.nextInt();
		return input;
	}
	
}
