import java.util.ArrayList;
import java.util.HashMap;

/*
 * class for common methods used throughout the project
 * these are all static and are called by class name
 */
public class Utilities {
	
	/*
	 * for writing a tag list to the db
	 */
	static String asQuotedString(ArrayList<String> list) {
		String rv = ""; // add beginning quote
		for(int i = 0; i < list.size(); i++) {
			rv += list.get(i);
			if(i < list.size()-1) // don't add comma on last item
				rv += ",";
		}
		if(!rv.equals(""))
			rv += "\""; // add ending quote
		else {
			rv = "\"\"";
		}
		return rv;
	}

	// print a one dimensional int array
	static void printArray(int[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]+ "\t ");
		}
		System.out.println();
	}
	// print a one dimensional string array
	static void printArray(String[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]+ "\t ");
		}
		System.out.println();
	}

	static void printArray(ArrayList<Integer> al) {
		for(int i = 0; i < al.size(); i++) {
			System.out.print(al.get(i)+" ");
		}
		System.out.println();
	}
	
	static void printArrayList(ArrayList<Person> al) {
		for(int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i)+" ");
		}
		System.out.println();
	}
	
	
	static void printChars(String string) {
		for(int i = 0; i < string.length(); i++) {
			System.out.print(string.charAt(i) + " ");
		}
		System.out.println();
		
	}
	/*
	 * print a hashmap of City: [List of people]
	 */
	static void printHashMap(HashMap<String, ArrayList<Person>> hm) {
		for(String s: hm.keySet()) {
			for(Person p: hm.get(s)) {
				System.out.println(p);
			}
		}
	}

	//print 2d arrayList<Integers>
	static void printArray2D(ArrayList<ArrayList<Integer>> al) {
		for(int i = 0; i <al.size(); i++) {
			printArray(al.get(i));
		}
	}

	// prints 2d string array
	static void print2DArray(String[][] a) {
		for(int i = 0; i < a.length; i++) {
			printArray(a[i]);
		}
	}

	// print 2d int array
	static void print2DArray(int[][] a) {
		for(int i = 0; i < a.length; i++) {
			printArray(a[i]);
		}
	}

	// find the first instance where a num is in an array
	static int findLocOfmax(int num, int[] array) {
		for(int i = 0; i <array.length; i++) {
			if (array[i] == num){
				return i;
			}
		}
		return -1; // not found
	}
	/*
	 * find the max value in an array
	 */
	static int findMax(int[] array) {
		int rv = -1000000;
		for(int i = 0; i < array.length; i++) {
			if(rv < array[i]) {
				rv = array[i];
			}
		}
		return rv;
	}


	//return the max of three numbers
	public static int max(int a, int b, int c) {
		if(a > b && a > c) 
			return a;
		if(b > a && b > c) 
			return b;
		else {
			// check for possible tie
			if(c == a || c == b) {

			}
			return c;
		}
	}

	// return the max of two numbers
	public static int max(int a, int b) {
		if(a > b)
			return a;
		return b;
	}

}
