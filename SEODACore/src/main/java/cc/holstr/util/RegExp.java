package cc.holstr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp{
	public static List<String> search(String total, String exp) {
		List<String> output = new ArrayList<String>();
		try {
		Pattern p = Pattern.compile(exp);
		Matcher match = p.matcher(total);
		if(match.find()){
			for(int i=0; i<match.groupCount();i++) {
				output.add(match.group(i));
			}
		}
		} catch(java.util.regex.PatternSyntaxException e) {
			e.printStackTrace();
		}
		return output;
	}
	public static String find(String total, String exp) {
		try {
			Pattern p = Pattern.compile(exp);
			Matcher match = p.matcher(total);
			if(match.find()){
				return match.group(0);
			}
			} catch(java.util.regex.PatternSyntaxException e) {
				e.printStackTrace();
			}
			return null;
	}
}
