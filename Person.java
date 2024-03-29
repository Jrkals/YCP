import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Person {
	String firstName;
	String lastName;
	String chapter;
	ArrayList<String> tagList = new ArrayList<String>();
	ArrayList<String> rest = new ArrayList<String>();
	HashMap<Integer, String> chapterList = new HashMap<>();
	boolean isVIP;
	boolean hasChapter;
	boolean attendingGala;
	boolean hasDietRestrictions;
	boolean hasSpouse;
	String dietRestrictions;
	Person spouse;
	boolean hasPotentialCity;
	Table table;
	
	/*
	 * constructor used in DatabaseReader.getTables() since its reading just from the tables table in the DB
	 * which includes only name
	 
	public Person(String fnm, String lnm) {
		firstName = fnm;
		lastName = lnm;
	}*/
	
	public Person(String fullName) {
		String parts[] = fullName.split(" ");
		if(parts.length == 2) {
			firstName = parts[0];
			lastName = parts[1];
		}
		if(parts.length == 3) {
			firstName = parts[0] + " "+parts[1];
			lastName = parts[2];
		}
		if(parts.length == 4) {
			firstName = parts[0] + " "+ parts[1] + " "+parts[2];
			lastName = parts[3];
		}
	}
	/*
	 * constructor used in DatabaseReader.parsePersonFromRow()
	 * this uses all the info stored in the database people table
	 */
	public Person(String fullName, String diet, String spouse_name, String chpt, String tags, int table_num) {
		Person temp = new Person(fullName);
		firstName = temp.firstName;
		lastName = temp.lastName;
		dietRestrictions = diet;
		
		if(dietRestrictions == null || dietRestrictions.equals("")) {hasDietRestrictions = false;}
		else {hasDietRestrictions = true;}
		
		if(spouse != null) {
			spouse = new Person(spouse_name);
			hasSpouse = true;
		}
		chapter = chpt;
		tagList.addAll(Arrays.asList(tags.split(" ")));
		table = new Table(table_num);		
	}

	public Person(String fnm, String lnm, ArrayList<String> tglist, ArrayList<String> rst) {
		firstName = fnm;
		lastName = lnm;
		tagList.addAll(tglist);
		rest.addAll(rst);
		addChapters(); // make list of chapters
		chapter = findChapter(); // needs addChapters to be called first
		isVIP(); // determine whether this person is a VIP and set the isVIP var accordingly
		attendingGala = isGoingToGala();
		hasDietRestrictions = hasDietaryRestrictions();
		if(hasDietRestrictions) {
			dietRestrictions = fetchDietRestrictions();
		}
		spouse = null;
		table = null;
	}

	private String fetchDietRestrictions() {
		String rv = "";
		rv = rest.get(0);
		/*System.out.println("Fetching diet for "+firstName+ " "+lastName);
		if(rest.get(2).equals("TRUE")) { rv+="diary,"; }
		if(rest.get(3).equals("TRUE")) { rv+="treenuts,"; }
		if(rest.get(6).equals("TRUE")) { rv+="gluten,"; }
		if(rest.get(7).equals("TRUE")) { rv+="legumes/peanuts,"; }
		if(rest.get(8).equals("TRUE")) { rv+="vegetarian"; }*/

		return rv;
	}

	/*
	 * searches through input looking for a valid chapter name
	 * if there is none it returns unknown
	 */
	private String findChapter() {
		String rv = "unknown";
		for(String s: rest) {
			if(chapterList.containsValue(s)) {
				rv = s;
			}
		}
		hasChapter = (rv.equals("unknown") ? false: true );
		return rv;
	}

	private boolean isVIP() {
		boolean rv = false;
		for(String tag: tagList) {
			if(tag.contains("VIP")) {
				rv = true;
			}
		}
		isVIP = rv;
		return rv;
	}


	private boolean isGoingToGala() {
		boolean rv = true;
		if(rest.size() > 2 && rest.get(2).equals("FALSE")) {
			rv = false;
		}
		return rv;
	}

	private boolean hasDietaryRestrictions() {
		boolean rv = false;
		if(rest.size() > 0 && rest.get(0).equals("None") || rest.get(0).equals("")) {
			rv = true;
		}
		return rv;
	}

	/*
	 * make an arrayList of the 20 chapters
	 */
	private void addChapters() {
		chapterList.put(0, "Austin");
		chapterList.put(1, "Chicago");
		chapterList.put(2, "Cleveland");
		chapterList.put(3, "Columbus");
		chapterList.put(4, "Dallas");
		chapterList.put(5, "Denver");
		chapterList.put(6, "Detroit");
		chapterList.put(7, "Fort Worth");
		chapterList.put(8, "Houston");
		chapterList.put(9, "Jacksonville");
		chapterList.put(10, "Los Angeles");
		chapterList.put(11, "New Orleans");
		chapterList.put(12, "Omaha");
		chapterList.put(13, "Orange County");
		chapterList.put(14, "Orlando");
		chapterList.put(15, "Phoenix");
		chapterList.put(16, "Portland");
		chapterList.put(17, "San Antonio");
		chapterList.put(18, "San Diego");
		chapterList.put(19, "Silicon Valley");
		chapterList.put(20, "National");
	}

	/*
	 * for printing
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String rv = "";
		rv += firstName+" "+lastName+","+chapter+",";
		rv += (isVIP) ? "VIP," : ",";
		rv += (hasDietRestrictions && dietRestrictions.length()>0) ? dietRestrictions : "none,";
		rv += (hasSpouse()) ? "\t"+spouse.firstName + " "+spouse.lastName : "";
		//rv += tagList.get(0);
		return rv;
	}
	
	public void findSpouse(ArrayList<Person> list) {
		if(this.spouse != null) {
			return;
		}
		
		for(Person p: list) {
			
			if(p != this) {
				if(p.lastName.equals(this.lastName) && p.chapter.equals(this.chapter)) {
					this.spouse = p;
					p.spouse = this;
					System.out.println(p +" and "+this+" are spouses (methinks)");
				}
			}
		}
	}
	
	boolean hasSpouse() {
		return this.spouse != null;
	}
}


