package cc.holstr.util;

import java.util.List;

public class ZUtilities {
	
	//Requires ZCheckable interface
	public static <T extends ZCheckable<T>> T findByExample(List<T> list, ZCheckable<T> example) {
		for(T t : list) {
			if(example.test(t)) {
				return t; 
			}
		}
		return null;
		}
}
