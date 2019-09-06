import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CsvReader {

	Scanner scan;
	File file;
	ArrayList<String> words = new ArrayList<>();
	ArrayList<Person> people = new ArrayList<>();

	public CsvReader(String filename) {
		file = new File(filename);
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} 

	/*
	 * read file and make list of people
	 */
	public String[] getItems() {
		skipFirstLine();
		while(scan.hasNext()) {	
			String line = scan.nextLine();
			String[] wordsInLine = line.split(","); // make array of individual words
			ArrayList<String> tagList = new ArrayList<>();

			// add each word to the word arrayList
			int i = 0;
			boolean endOfTagList = false;
			for(String word: wordsInLine) {
				//System.out.print(i+ " ");
				words.add(word);
				//System.out.println(word);
				// add all of the tags
				int temp = i;
				while(temp >= 8 && !endOfTagList) {
					// a '"' at the end means the tag list is over
					if(wordsInLine[temp].charAt(wordsInLine[temp].length()-1) == '"') {
						//System.out.println("true");
						endOfTagList = true;
					}
					tagList.add(wordsInLine[temp]);
					temp++;
					//System.out.println("temp " +temp);
				}
				i++;

				/* with MY CSVs firstName is spot 2, last name 4, tag list begins at 8. Its
				 * length determines the rest so we find it, then use it to pick the diet and chapter
				 * */
			}
			ArrayList<String> rest = new ArrayList<String>();
			rest = filterOutEmpty(wordsInLine);
			Person p = new Person(wordsInLine[2], wordsInLine[4], tagList, rest);
			people.add(p);
		}
		int sizeOfWords = words.size();

		//Put words into fixed size array
		String[] wordsArray = new String[sizeOfWords];
		for(int i = 0; i < sizeOfWords; i++) {
			wordsArray[i] = words.get(i);
		}
		scan.close();
		words.addAll(Arrays.asList(wordsArray));
		return wordsArray;
	}

	public void getItems2() {
		skipFirstLine();
		while(scan.hasNext()) {
			String line = scan.nextLine();
			ArrayList<String> tags = new ArrayList<String>();
			ArrayList<String> rest = new ArrayList<String>();
			// split line into parts
			// NB ID, name, tags, potential_city, diet, dairy, tree_nuts,...
			// chapter, going_to_gala, gluten, legumes, vegetarian
			String[] parts = line.split(",");
			//System.out.println(line);
			String firstName = "No First Name"; // these should be replaced with actual names
			String lastName = "No Last Name"; // ''
			String[] name = parts[1].split(" "); 
			// check to make sure they have a name
			if(name.length > 1) {
				if(name.length == 2) {
					firstName=name[0]; 
					lastName=name[1];
				} // handle middle names
				else if(name.length == 3) {
					firstName = name[0]+ " ";
					firstName += name[1];
					lastName = name[2];
				}
				else if(name.length == 4) {
					firstName = name[0] + " ";
					firstName += name[1] + " ";
					firstName += name[2];
					lastName = name[3];
				}
			} else {System.out.println("ERROR NO NAME ENTERED: "+line);}
			System.out.println(firstName+" "+lastName);
			int[] tagListIndices = new int[2];
			tagListIndices = findTagListIndices(parts);
			// put tags in a list
			for(int i = tagListIndices[0]; i < tagListIndices[1]; i++) {
				tags.add(parts[i]);
			}

			// put rest of information in a list
			for(int i = tagListIndices[1]+1; i <parts.length; i++) {
				rest.add(parts[i]);
			}
			//System.out.println(firstName+" "+lastName+" "+tags.size()+" "+rest.size());
			Person p = new Person(firstName, lastName, tags, rest);
			people.add(p);
		}
		System.out.println("The number of people is "+people.size());

		scan.close();
	}
	/*
	 * finds where the tag list starts and begins
	 */
	private int[] findTagListIndices(String[] parts) {
		int[] rv = new int[2];
		// start at 2 to skip the NB id and name
		for(int i = 2; i < parts.length; i++) {
			if(parts[i] != null) {
				//System.out.println(parts[i]);
				if(parts[i].equals("")) { rv[1] = i; break;}
				if(parts[i].charAt(0) == '"') {
					rv[0] = i; // beginning of tag list
				}
				if(parts[i].charAt(parts[i].length()-1) == '"') {
					rv[1] = i; // end of tag list
					break;
				}
			}
		}

		return rv;
	}

	/*
	 * takes an array and returns a list from that spot in the array to
	 * the end of all non empty strings (and ones that are not T/F/Yes/No
	 */
	private ArrayList<String> filterOutEmpty(String[] wordsInLine) {
		ArrayList<String> rv = new ArrayList<>();
		for(int j = 0; j < wordsInLine.length; j++) {
			if(!wordsInLine[j].equals("") &&
					!wordsInLine[j].equals("TRUE") &&
					!wordsInLine[j].equals("FALSE") &&
					!wordsInLine[j].equals("Yes") &&
					!wordsInLine[j].equals("No")) {
				//System.out.println("added "+wordsInLine[j]);
				rv.add(wordsInLine[j]);
			}
		}
		return rv;
	}

	private void skipFirstLine() {
		scan.nextLine();
	}


	void printFile() {
		for (String s: getItems()) {
			System.out.print(s);
			System.out.println();
		}
	}

	public ArrayList<Person> getPeople(){
		return people;
	}

	public ArrayList<Person> getPeopleGoingToGala(){
		ArrayList<Person> rv = new ArrayList<>();
		for(Person p: people) {
			if(p.attendingGala) {
				rv.add(p);
			}
		}
		System.out.println("The number of people attending the Gala is "+rv.size());
		return rv;
	}

}
