package cc.holstr.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ZMisc {
	public static int getBiggest(int[] array) {
		int biggest = array[0];
		for(int i : array) {
			if(i>biggest) {
				biggest = i; 
			}
		}
		return biggest;
	}
	
	public static int getBiggest(List<Integer> list) {
		int biggest = list.get(0);
		for(int i : list) {
			if(i>biggest) {
				biggest = i; 
			}
		}
		return biggest;
	}
	
	public static int getSmallest(int[] array) {
		int smallest = array[0];
		for(int i : array) {
			if(i<smallest) {
				smallest = i; 
			}
		}
		return smallest;
	}
	
	public static int getSmallest(List<Integer> list) {
		int smallest = list.get(0);
		for(int i : list) {
			if(i<smallest) {
				smallest = i; 
			}
		}
		return smallest;
	}
	
	public static String getAlphabetValue(int num) {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder b = new StringBuilder(); 
		while(num>26) {
			b.append(alphabet.charAt((num%26)-1));
			num = num/26;
		}
		b.append(alphabet.charAt((num)-1));
		return b.reverse().toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <K,V> String getMapString(Map<K,V> mp) {
		String r = "";
	    Iterator<Entry<K, V>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<K,V> pair = (Map.Entry<K,V>)it.next();
	        r += "K: " + (K)(pair.getKey()).toString() + " = \n" + (V)(pair.getValue()).toString() + "\n";
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    return r; 
	}
	
	public static <T> void printMatrix(T[][] data) {
		for(T[] row : data) {
			for(T col : row) {
				if(col!=null) {
					if(col.equals("")) {
						System.out.println("blank, ");
					} else {
					System.out.println(col.toString() + ", ");
					}
				} else {
					System.out.println("null, ");
				}
				
			}
		}
		
	}
	
	public static boolean isEmpty(String[][] list) {
		boolean eval = true; 
		if(list!=null) {
			for(String[] row : list) {
				for(String col : row) {
					if(col!=null) {
						if(!(col.equals(""))) {
							eval = false;
						}
					} 
				}
			}
		}
		return eval; 
	}
 	
	public static <T> int getLongestRow(T[][] arr) {
		int biggest = 0;
		for(int i =0; i<arr.length;i++) {
			if(biggest<arr[i].length) {
				biggest = arr[i].length;
			}
		}
		return biggest;
	}
	
	public static int lengthWithoutEmpty(String[][] arr, int row) {
		int length = 0; 
		for(int c = 0; c<arr[row].length;c++) {
			if(arr[row][c]!=null) {
				if(!arr[row][c].equals("")) {
					length++;
					}
				} 
			}
		return length;
	}
	
	public static String[][] mergeRow(String[][] old, String[] newRow, int offset, int row) {
		int pos = 0;
		int needed = (newRow.length+offset)-old[row].length;
		if(needed<0) {
			needed = 0;
		}
		String[][] merge = new String[old.length][old[row].length+needed];
		for(int r = 0; r<merge.length; r++) {
			for(int c = 0; c<merge[r].length;c++) {
				if(r<old.length) {
					if(c<old[r].length) {
						merge[r][c] = old[r][c];
					}
				}
			}
		}
		for(int i = offset; i<merge[row].length;i++) {
			if(pos<newRow.length)
			merge[row][i] = newRow[pos];
			pos++; 
		}
		
		return merge; 
	}
	
	public static String[][] mergeColumn(String[][] old, String[] newCol, int offset, int column) {
		int pos = 0;
		int needed = (newCol.length+offset)-old.length;
		if(needed<0) {
			needed = 0;
		}
		String[][] merge = new String[old.length+needed][old[0].length];
		for(int r = 0; r<old.length;r++) {
			for(int c = 0; c<old[r].length;c++) {
				merge[r][c] = old[r][c];
			}
		}
		for(int i= offset; i<merge.length;i++) {
			if(pos<newCol.length) {
				merge[i][column] = newCol[pos];
				pos++;
			}
		}
		
		return merge;
	}
 	
}
