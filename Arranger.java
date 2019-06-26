import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Arranger {
	ArrayList<Person> peopleList = new ArrayList<>();
	ArrayList<Person> VIPList = new ArrayList<>();
	Table[] tables;
	ArrayList<Table> tableList = new ArrayList<>();
	HashMap<Integer, String> chapterMap = new HashMap<>();
	// maps from chapter -> persons
	//e.g., Dallas: [John, Mary, Sue], Austin: [Jacob, Juan, Maria]
	HashMap<String, ArrayList<Person>> chapterList = new HashMap<>();

	public Arranger(ArrayList<Person> ppl, Table[] tbls) {
		peopleList.addAll(ppl);
		tableList.addAll(Arrays.asList(tbls));
		tables = tbls;
		fillChapterList();  // fills chapterList
		fillVIPList();
		addChapters(); // fills chapterMap
	}

	void arrangeTables() {
		boolean done = false;
		//String currentChapter = chapterMap.get(0); //"Austin"
		String currentChapter = getNewChapter2();
		for(Table t: tables) {
			//System.out.println("current chapter is "+currentChapter);
			//System.out.println("table +"+t.tableID+" # spots "+t.numberOfSpots);
			while(t.numberOfSpots > 0 && !done) { // while table is available
				while(VIPList.size() > 0 && t.numberOfSpots > 0) { // while VIPs need seats
					Person p = VIPList.remove(0); // pop VIP off the list
					chapterList.get(p.chapter).remove(p); // remove from this list to avoid duplicates
					t.addPerson(p); 
				}
				while(!chapterList.isEmpty() && t.numberOfSpots > 0) {
					// if finished with this chapter, get a new one
					if(chapterList.get(currentChapter).size() == 0) {
						chapterList.remove(currentChapter); // done with this
						currentChapter = getNewChapter2();
						//Utilities.printHashMap(chapterList);
						//System.out.println("new chapter is "+currentChapter);
					}
					// since we delete stuff above we need to re-check
					if(!chapterList.isEmpty()) {
						Person p = chapterList.get(currentChapter).remove(0); // pop off new person
						t.addPerson(p);
						
					}
					else {
						done = true;
						//System.out.println("done");
					}
				} // end of chapter loop
			} // end of numberOfSpots loop
		} // end of table loop
	}
	/*
	 * goes through list of chapters and returns the first one it finds
	 * over time it will remove all of them
	 */
	private String getNewChapter() {
		for(int i = 0; i < 21; i++) {
			// check 1) not yet picked 2) has members at the conference 3) not all seated yet
			if(chapterMap.containsKey(i) && chapterList.containsKey(chapterMap.get(i))
					&& !chapterList.get(chapterMap.get(i)).isEmpty()) {
				return chapterMap.remove(i);
			}
		}
		return null;
	}
	
	/*
	 * a slow linear search through a hashmap which I am treating
	 * as a priority queue the chapters with the lowest members get picked first
	 */
	private String getNewChapter2() {
		String biggestChapter = "";
		int min = 500;
		for(String chapterName: chapterList.keySet()) {
			if(chapterList.get(chapterName).size() < min) {
				min = chapterList.get(chapterName).size();
				biggestChapter = chapterName;
			}
		}
		return biggestChapter;
	}

	void printArrangement() {
		for(Table t: tables) {
			if(t.numberOfSpots < 10) {
				System.out.println("Table "+t.tableID);
				for(int i = 0; i < 10; i++) {
					System.out.print("seat:"+(i+1)+" ");
					if(t.seats[i] != null) {
						System.out.println(t.seats[i]);
					}
					else {
						System.out.println("empty");
					}
				}
			}
		}
	}

	/*
	 * fills the VIP list by searching peopleList
	 */
	private void fillVIPList() {
		for(Person p: peopleList) {
			if(p.isVIP) {
				VIPList.add(p);
			}
		}

	}

	/*
	 * fills chapterList hashmap
	 */
	private void fillChapterList() {
		for(Person p: peopleList) {
			// if chapter exists, add the person to it
			if(chapterList.containsKey(p.chapter)) {
				chapterList.get(p.chapter).add(p);
			}
			else { // put in new chapter with that person in it
				ArrayList<Person> lst = new ArrayList<>();
				lst.add(p);
				chapterList.put(p.chapter, lst);
			}
		}
	}

	/*
	 * make an arrayList of the 20 chapters
	 */
	private void addChapters() {
		chapterMap.put(0, "Austin");
		chapterMap.put(1, "Chicago");
		chapterMap.put(2, "Cleveland");
		chapterMap.put(3, "Columbus");
		chapterMap.put(4, "Dallas");
		chapterMap.put(5, "Denver");
		chapterMap.put(6, "Detroit");
		chapterMap.put(7, "Fort Worth");
		chapterMap.put(8, "Houston");
		chapterMap.put(9, "Jacksonville");
		chapterMap.put(10, "Los Angeles");
		chapterMap.put(11, "New Orleans");
		chapterMap.put(12, "Omaha");
		chapterMap.put(13, "Orange County");
		chapterMap.put(14, "Orlando");
		chapterMap.put(15, "Phoenix");
		chapterMap.put(16, "Portland");
		chapterMap.put(17, "San Antonio");
		chapterMap.put(18, "San Diego");
		chapterMap.put(19, "Silicon Valley");
		chapterMap.put(20, "unknown"); // for those without a chapter
	}

}
