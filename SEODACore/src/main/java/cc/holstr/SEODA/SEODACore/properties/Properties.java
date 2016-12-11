package cc.holstr.SEODA.SEODACore.properties;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import cc.holstr.SEODA.SEODACore.commandLine.Ansi;

public class Properties {
	
	/* Properties.java
	 * by zudsniper @ github
	 * 
	 * */
	
	public static Configuration config;
	public static FileBasedConfigurationBuilder<FileBasedConfiguration> builder; 
	private static Path propPath;
	private static File propFile;
	private static boolean isLoaded = false; 
	
	public static boolean load() {
		boolean success = false; 
		propPath  = Paths.get(System.getProperty("user.home"),".store","seoda","data","config.properties");
		propFile = propPath.toFile();
		if(config==null) {
			Parameters params = new Parameters();
			builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
			    .configure(params.properties()
			        .setFile(propFile)
			    	);
			try
			{
			   config = builder.getConfiguration();
			   System.out.println("CONFIGURATION : Properties file loaded.");
			   success = true; 
			}
			catch(ConfigurationException cex)
			{
				System.out.println(Ansi.RED+"CONFIGURATION : Properties file could not be loaded."+Ansi.SANE);
			}
		}
		isLoaded = success; 
		return success; 
	}
	
	public static void printProperty(String property) {
		System.out.println(getConfig().getProperty(property));
	}
	
	public static boolean save() {
		boolean failed = false; 
		if(builder!=null) {
			try {
				builder.save();
				System.out.println("CONFIGURATION : Properties saved.");
			} catch (ConfigurationException e) {
				failed = true; 
				System.out.println(Ansi.RED+"CONFIGURATION : Properties file failed to save."+Ansi.SANE);
			}
		}
		return failed; 
	}
	
	public static boolean reload() {
		boolean failed = false; 
		if(builder!=null) {
			try {
				builder.save();
				load();
			} catch (ConfigurationException e) {
				failed = true; 
				System.out.println(Ansi.RED+"CONFIGURATION : Properties file failed to save."+Ansi.SANE);
			}
		}
		return failed; 
	}
	
	public static Configuration getConfig() {
		return config;
	}
		
	public static boolean isLoaded() {
		return isLoaded; 
	}
	
}
