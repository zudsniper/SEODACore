package cc.holstr.SEODA.SEODACore.log;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class SEODALogger {

private static Logger log;

	public static void configure(String filepath) {
		System.setProperty("logfile", filepath);

		log = Logger.getLogger("fileLogger");
		BasicConfigurator.configure();
	}

	public static Logger getLogger(){
	        return SEODALogger.log;
	}
}


