import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Arranger2 {
	ArrayList<Person> peopleList = new ArrayList<>();
	ArrayList<Person> VIPList = new ArrayList<>();
	Table[] tables;
	Table[] vipTables;
	ArrayList<Table> tableList = new ArrayList<>();
	ArrayList<Table> vipTableList = new ArrayList<>();
	HashMap<Integer, String> chapterMap = new HashMap<>();
	// maps from chapter -> persons
	//e.g., Dallas: [John, Mary, Sue], Austin: [Jacob, Juan, Maria]
	HashMap<String, ArrayList<Person>> chapterList = new HashMap<>();
	HashMap<String, ArrayList<Person>> VIPChapterList = new HashMap<>();
	HashMap<String, ArrayList<Table>> tableAllocation = new HashMap<>();
	HashMap<String, ArrayList<Table>> VIPTableAllocation = new HashMap<>();
	
	public Arranger2(ArrayList<Person> ppl, Table[] tbls, Table[] viptables) {
		peopleList.addAll(ppl);
		fillVIPList();
		tableList.addAll(Arrays.asList(tbls));
		vipTableList.addAll(Arrays.asList(viptables));
		//System.out.println("VIP table list size "+vipTableList.size());
		//System.out.println("chapter table list size"+tableList.size());
		tables = tbls;
		vipTables = viptables;
		fillChapterList();  // fills chapterList
		
		fillVIPChapterList();
		addChapters(); // fills chapterMap
	}
	/*
	 * list that contains vip members arranged in their chapters
	 */
	private void fillVIPChapterList() {
		for(Person p: VIPList) {
			// if chapter exists, add the person to it
			if(VIPChapterList.containsKey(p.chapter)) {
				VIPChapterList.get(p.chapter).add(p);
			}
			else { // put in new chapter with that person in it
				ArrayList<Person> lst = new ArrayList<>();
				lst.add(p);
				VIPChapterList.put(p.chapter, lst);
			}
		}

	}

	void arrangeTables() {
		// first alloc to VIPS
		System.out.println("Doing VIPS...");
		allocateTableToList(VIPChapterList, VIPTableAllocation, vipTableList);
		//printArrangement();
		// then allocate to chapters
		System.out.println("Doing chapters...");
		allocateTableToList(chapterList, tableAllocation, tableList);
		// go through the open seats and give to unknowns and other chapters if still left
		System.out.println("Doing the rest...");
		allocateLeftovers(chapterList, tableAllocation, tableList);
		System.out.println("Done");
		printArrangement();
	}

	private void allocateLeftovers(HashMap<String, ArrayList<Person>> list, 
			HashMap<String, ArrayList<Table>> allocation, ArrayList<Table> table_list) {
		while(!list.isEmpty()) {
			String currentChapter = getBiggestChapter(list);
			//System.out.println("biggest chapter is: "+currentChapter);
			int numberOfPeople = list.get(currentChapter).size();
			//System.out.println("num people: "+numberOfPeople);
			int numTablesToAllocate =(numberOfPeople % 10 == 0) ? numberOfPeople/10 : numberOfPeople / 10 + 1; // useful if there are still full tables available
			// if you can allocate these people to new tables
			if(table_list.size() >= numTablesToAllocate) {
				System.out.println("Allocating "+numTablesToAllocate+" table(s) to chapter "+currentChapter);
				allocation.put(currentChapter, new ArrayList<Table>());
				//pop of the number of tables you are allocating and allocate
				for(int i = 0; i < numTablesToAllocate; i++) {
					allocation.get(currentChapter).add(table_list.remove(0));
				}
				// Allocate the seats specifically
				int leftOver = (numberOfPeople / numTablesToAllocate)%10;
				System.out.println("left over is "+leftOver);
				for(Table t: allocation.get(currentChapter)) {
					// e.g., 21 people over 3 tables. 21 % 3 = 0
					for(int i = 0; i < numberOfPeople/numTablesToAllocate; i++) {
						t.addPerson(list.get(currentChapter).remove(0));
					}
					// e.g., 29 people over 3 tables 29 % 3 = 2
					// add one leftover to this table
					if(leftOver != 0) {
						t.addPerson(list.get(currentChapter).remove(0));
						leftOver--;
					} // if leftOver != 0
				} // loop over tables
				list.remove(currentChapter);
			} // end of if
			else { // they must reuse previously allocated tables
				// get a list of open tables
				ArrayList<Table> openTables = new ArrayList<>();
				for(String chap: allocation.keySet()) {
					if(chap.equals(currentChapter)) continue; // no tables allocated to it yet
					for(Table t: allocation.get(chap)) {
						if(t.numberOfSpots > 0) {
							openTables.add(t);
							//System.out.println("table "+t.tableID+" has open spots");
						}
					} // end of for
				}
				// look in the open tables and fill them in
				for(Table t: openTables) {
					for(int i = t.numberOfSpots; i > 0; i--) {
						//System.out.println("adding to table "+t.tableID);
						if(list.get(currentChapter).size() > 0) {
							t.addPerson(list.get(currentChapter).remove(0));
						}
					}
					//System.out.println("Table "+t.tableID+" now is ");
					//System.out.println(t);
				}
				list.remove(currentChapter);
			} // end of else
		} // end of while

	} // end of method
	/*
	 * takes a list (in form of a hashmap) and allocates tables to them
	 */
	private void allocateTableToList(HashMap<String, ArrayList<Person>> list,
			HashMap<String, ArrayList<Table>> allocation, ArrayList<Table> table_list) {
		while(!list.isEmpty()) {
			String currentChapter = getBiggestChapter(list);
			//System.out.println("biggest chapter is "+currentChapter);
			int numberOfPeople = list.get(currentChapter).size();
			//System.out.println("there are "+numberOfPeople+ " people from "+currentChapter);
			int numTablesToAllocate = numberOfPeople / 10 + 1;
			// should never happen unless there are like 500 VIPS
			if(numTablesToAllocate > table_list.size()) {
				//System.out.println("this chapter can't get its own table");
				allocateLeftovers(list, allocation, table_list);
				return;
			}
			else {
				//System.out.println("eAllocating "+numTablesToAllocate+" table(s) to "+currentChapter);
				allocation.put(currentChapter, new ArrayList<Table>());
				//pop of the number of tables you are allocating and allocate
				for(int i = 0; i < numTablesToAllocate; i++) {
					allocation.get(currentChapter).add(table_list.remove(0));
					//System.out.println("used table "+allocation.get(currentChapter).get(i).tableID);
				}
				// Allocate the seats specifically
				// with 1 person this is 1. Hence the && on line 152
				int leftOver = numberOfPeople % (numberOfPeople/numTablesToAllocate); 
				//System.out.println("leftover is "+leftOver);
				for(Table t: allocation.get(currentChapter)) {
					//System.out.println("table "+t.tableID);
					// e.g., 21 people over 3 tables. 21 % 3 = 0
					for(int i = 0; i < numberOfPeople/numTablesToAllocate; i++) {
						//System.out.println("adding "+list.get(currentChapter).get(0));
						t.addPerson(list.get(currentChapter).remove(0));
					}
					// e.g., 29 people over 3 tables 29 % 3 = 2
					// add one leftover to this table
					if(leftOver != 0 && numTablesToAllocate > 1) {
						t.addPerson(list.get(currentChapter).remove(0));
						leftOver--;
					} // if leftOver != 0
				} // loop over tables
				list.remove(currentChapter);
			} // end of else
		} // end of while
	} // end of method

	void printArrangement() {
		for(Table t: vipTables) {
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
	 * removes vip from normal list
	 */
	private void fillVIPList() {
		for(Person p: peopleList) {
			if(p.isVIP) {
				VIPList.add(p);
			}
		}
		for(Person p: VIPList) {
			peopleList.remove(p);
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
		chapterMap.put(20, "National");
		chapterMap.put(21, "unknown"); // for those without a chapter
	}

	/*
	 * a slow linear search through a hashmap which I am treating
	 * as a priority queue the chapters with the most members get picked first
	 */
	String getBiggestChapter(HashMap<String, ArrayList<Person>> list) {
		String biggestChapter = "unknown";
		int max = -1;
		for(String chapterName: list.keySet()) {
			if(chapterName.equals("unknown")) continue; // these people get seated last
			if(list.get(chapterName).size() > max) {
				max = list.get(chapterName).size();
				biggestChapter = chapterName;
			}
		}
		return biggestChapter;
	}
}
