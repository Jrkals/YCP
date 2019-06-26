import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class fakeDataCreator {
	
	File output;
	int numPeople = 0;
	HashMap<Integer, String> chapterMap = new HashMap<>();
	Person[] people;
	
	public fakeDataCreator(String filename, int numppl) {
		output = new File(filename);
		numPeople = numppl;
		people = new Person[numPeople];
		addChapters();
	}
	
	String createRandomName() {
		Random ran = new Random();
		String rv = "";
		for(int i = 0; i < 6; i++) {
			int randInt = ran.nextInt(25)+65;
			rv += (char)randInt;
		}
		return rv;
	}
	
	private void createRandomPeople() {
		for(int i = 0; i < numPeople; i++) {
			ArrayList<String> tags = new ArrayList<String>();
			ArrayList<String> other = new ArrayList<String>();
			Person p = new Person(createRandomName(), createRandomName(), tags, other);
			if(isVIP()) { p.tagList.add("VIP"); p.isVIP = true;}
			p.tagList.add("testTag1, testTag2");
			if(isGala()) p.attendingGala = true;
			p.chapter = pickRandomChapter();
			// potential chapter
			if(hasPotentialYCPCity()) {p.hasPotentialCity=true; p.rest.add(pickRandomChapter());}
			else {p.rest.add("");} // even if there is no potential chapter add a "" to leave it blank
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			//current chapter
			if(!p.chapter.equals("")) p.rest.add(p.chapter);
			else {p.rest.add("");}
			//gala rsvp
			if(p.attendingGala) p.rest.add("TRUE");
			else {p.rest.add("");}
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			//diet
			if(hasDietaryRestriction()) { p.rest.add("TRUE"); } else p.rest.add("FALSE");
			people[i] = p;
			//System.out.println(people[i]);
		}
	}
	
	public void writeToFile() throws IOException {
		createRandomPeople();
		FileWriter fw = new FileWriter(output);
		Random ran = new Random();
		fw.write("nationbuilder_id, full_name, tag_list, potential_ycp_chapter,");
		fw.write("2019_mc_diet_none, 2019_mc_diet_dairy, 2019_mc_diet_tree_nuts,");
		fw.write("current_chapter, 2019_rsvp_gala, 2019_mc_diet_gluten_wheat,");
		fw.write("2019_mc_diet_legumes_peanuts, 2019_mc_diet_vegetarian\n");
		for(Person p: people) {
			fw.write(ran.nextInt(100000) + ",");
			fw.write(p.firstName+" "+p.lastName+",");
			fw.write("\""); // beginning of tags
			for(int i = 0; i < p.tagList.size(); i++) {
				if(i+1 == p.tagList.size()) {
					fw.write(p.tagList.get(i)); // ommit the comma on the last one
				}
				else {
					fw.write(p.tagList.get(i)+",");
				}
			}
			fw.write("\","); // end of tags
			for(String r: p.rest) {
				fw.write(r+",");
			}
			fw.write("\n");
		}
		fw.close();
	}
	
	private boolean hasPotentialYCPCity() {
		Random ran = new Random();
		int x = ran.nextInt(10);
		if(x == 6) {
			return true;
		}
		else {
			return false;
		}
	}

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
		chapterMap.put(21, "");
	}
	
	private String pickRandomChapter() {
		Random ran = new Random();
		String rv = "";
		rv = chapterMap.get(ran.nextInt(22));
		return rv;
	}
	
	/*
	 * 1/10 chance of yes
	 */
	private boolean isVIP() {
		Random ran = new Random();
		int x = ran.nextInt(10);
		if(x == 6) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isGala() {
		Random ran = new Random();
		int x = ran.nextInt(10);
		if(x < 11) { // 100% chance of going
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean hasDietaryRestriction() {
		Random ran = new Random();
		int x = ran.nextInt(20);
		if(x == 7) { 
			return true;
		}
		else {
			return false;
		}
	}
	

}
