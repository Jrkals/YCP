import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

public class seatingUnitTests {

	HashMap<String, ArrayList<Person>> list = new HashMap<>();

	
	private void makeFakeData() {
		ArrayList<Person> dallas = new ArrayList<Person>();
		dallas.addAll(Arrays.asList(createPeople(5, "dallas")));
		
		ArrayList<Person> unknown = new ArrayList<Person>();
		unknown.addAll(Arrays.asList(createPeople(10, "unknown")));
		
		ArrayList<Person> phoenix = new ArrayList<Person>();
		phoenix.addAll(Arrays.asList(createPeople(3, "phoenix")));
		
		ArrayList<Person> cleveland = new ArrayList<Person>();
		cleveland.addAll(Arrays.asList(createPeople(6, "cleveland")));
		
		list.put("Dallas", dallas);
		list.put("Unknown", unknown);
		list.put("Phoenix", phoenix);
		list.put("Cleveland", cleveland);
	}
	
	@Test
	public void testPickBiggestChapter() {
		makeFakeData();
	
		Arranger2 arr = new Arranger2(new ArrayList<Person>(), new Table[1], new Table[1], new HashMap<>());
		String rv = arr.getBiggestChapter(list);
		System.out.println(rv);
		assertFalse(rv.equals("Unknown"));
		
	}
	
	/*
	 * creates a list of fake people
	 */
	private Person[] createPeople(int size, String cityName) {
		Person[] rv = new Person[size];
		for(int i = 0; i < size; i++) {
			String name = "test " + i;
			rv[i] = new Person(name, cityName, new ArrayList<String>(), new ArrayList<String>());
		}
		return rv;
	}

}
