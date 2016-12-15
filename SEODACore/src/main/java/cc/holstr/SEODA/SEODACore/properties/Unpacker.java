package cc.holstr.SEODA.SEODACore.properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import cc.holstr.SEODA.SEODACore.commandLine.Ansi;
import cc.holstr.SEODA.SEODACore.log.SEODALogger;

public class Unpacker {
	
	private static final long logLifetimeMS = 864000000;
	private static final boolean logClear = true;
	
	private static Path storePath;
	private static File storeFile;

	private static Path loggingPath;
	private static File loggingFile;
	
	private static File templateFile;
	private static File configTemplate;
	private static String versionTxt;
	private static File layoutsDir;
	
	public static boolean propsReset;
	
	private static int changes;
	
	public static void unpack() {
		changes = 0;
		unpackLogging();
		loadVersionString();
		boolean success = unpackStoreDirectory() 
		&& unpackConfig()
		&& unpackLayouts();
		if(success) {
			System.out.println(Ansi.GREEN+"UNPACKER : Unpacked, " + changes + " changes made."+Ansi.SANE);
		}
	}
	
	private static boolean unpackLogging() {
		boolean success = true;
		loggingPath = Paths.get(System.getProperty("user.home"),".store","seoda","logging");
		if(!loggingPath.toFile().exists()) {
			System.out.println("UNPACKER : Creating logging directory...");
			try {
				Files.createDirectories(loggingPath);
				System.out.println("Success.");
				changes++;
			} catch (IOException e) {
				SEODALogger.getLogger().error("Failed to create logging directory.");
				System.out.println("ERROR : Failed to create logging directory.");
				success = false;
			}
		} else {
			if(logClear) {
				for(File file : loggingPath.toFile().listFiles()) {
					if(file.getName().contains("log")) {
						long nowMS = System.currentTimeMillis();
						long ms = Long.parseLong(file.getName().substring(0,file.getName().lastIndexOf(".")));
						if(ms+logLifetimeMS<=nowMS) {
							file.delete();
						}
					}
				}
			}
		}
		loggingFile = Paths.get(loggingPath.toString(),System.currentTimeMillis()+".log").toFile();
		if(!loggingFile.exists()) {
			try {
				loggingFile.createNewFile();
			} catch (IOException e) {
				SEODALogger.getLogger().error("Couldn't create logging file.");
				System.out.println("UNPACKER : Couldn't create logging file.");
			}
		}
		SEODALogger.configure(loggingFile.getAbsolutePath());
		return success;
	}
	
	private static boolean unpackStoreDirectory() {
		boolean success = true;
		storePath  = Paths.get(System.getProperty("user.home"),".store","seoda","data");
		storeFile = storePath.toFile();
		if(!storeFile.exists()) {
			System.out.println("UNPACKER : Creating store directory...");
			try {
				Files.createDirectories(storePath);
				System.out.println("Success.");
				changes++;
			} catch (IOException e) {
				SEODALogger.getLogger().error("Failed to create store directory.");
				System.out.println("ERROR : Failed to create store directory.");
				success = false;
			}
		}
		return success;
	}
	
	private static boolean unpackConfig() {
		boolean success = true;
		File config = Paths.get(storePath.toString(),"config.properties").toFile();
		if(!config.exists()) {
			try {
				copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("config.properties"),config);
				System.out.println("UNPACKER : Created default config document.");
				changes++;
			} catch (IOException e) {
				SEODALogger.getLogger().error("Couldn't make configuration directory in store dir.");
				System.out.println("UNPACKER : Couldn't make configuration directory in store dir.");
				e.printStackTrace();
				success = false;
			}
		}
		return success;
	}
	
	private static boolean unpackLayouts() {
		boolean success = true;
		File layout = Paths.get(storePath.toString(),"layouts").toFile();
		if(!layout.exists()) {
			try {
				loadLayouts(layout);
				System.out.println("UNPACKER : created layout templates.");
				changes++; 
			} catch (IOException e) {
				
				System.out.println("UNPACKER : Couldn't make layouts directory in store dir.");
				SEODALogger.getLogger().error("Couldn't make layouts directory in store dir.");
				e.printStackTrace();
				success = false;
			}
		}
		return success;
	}
	
	private static void loadLayouts(File target) throws IOException{
		copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("layouts/GSC.csv"),Paths.get(target.getAbsolutePath(),"GSC.csv").toFile());
		copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("layouts/Adwords.csv"),Paths.get(target.getAbsolutePath(),"Adwords.csv").toFile());
		copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("layouts/GMB.csv"),Paths.get(target.getAbsolutePath(),"GMB.csv").toFile());
		copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("layouts/Analytics.csv"),Paths.get(target.getAbsolutePath(),"Analytics.csv").toFile());
		copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("layouts/Sheet1.csv"),Paths.get(target.getAbsolutePath(),"Sheet1.csv").toFile());
	}
	
	private static void loadVersionString() {
			try {
				InputStream str = Unpacker.class.getClassLoader().getResourceAsStream("version.txt");
				StringWriter sw = new StringWriter();
				IOUtils.copy(str, sw, "UTF-8");
				versionTxt = sw.toString();
				SEODALogger.getLogger().debug("Loaded Version txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void resetProperties() {
		Properties.save();
		File config = Paths.get(storePath.toString(),"config.properties").toFile();
		if(config.delete()) {
			System.out.println("UNPACKER : Old config.properties deleted.");
		}
		try {
			copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("config.properties"),config);
			System.out.println("UNPACKER : Properties file reset.");
			Properties.config = null;
			Properties.builder = null;
			Properties.load();
		} catch (IOException e) {
			SEODALogger.getLogger().error("Error during config reset.");
			System.out.println("UNPACKER : Error during config reset.");
		}
	}
	
	public static void resetLayoutTemplates() {
		File layout = Paths.get(storePath.toString(),"layouts").toFile();
		if(layout.delete()) {
			System.out.println("UNPACKER : Old layout templates deleted.");
			SEODALogger.getLogger().error("Old layout templates deleted.");
		}
		try {
			loadLayouts(layout);
			System.out.println("UNPACKER : layout templates reset.");
			SEODALogger.getLogger().debug("UNPACKER : Layout templates reset.");
		} catch (IOException e) {
			SEODALogger.getLogger().error("Couldn't make layouts directory in store dir.");
			System.out.println("UNPACKER : Couldn't make layouts directory in store dir.");
			e.printStackTrace();
		}
	}
	
	private static File copyToFile(InputStream s, File f) throws IOException {
		if(!f.exists()) {
			Path p = Paths.get(f.getAbsolutePath());
			Files.createDirectories(p.getParent());
			f.createNewFile();
		}
		FileWriter fw = new FileWriter(f);
		IOUtils.copy(s, fw, "UTF-8");
		fw.close();
		s.close();
		return f;
	}
	
	public static void removeLoggingFile() {
		loggingFile.delete();
	}
	
	public static Path getStorePath() {
		return storePath;
	}

	public static File getStoreFile() {
		return storeFile;
	}

	public static File getTemplateFile() {
		return templateFile;
	}

	public static File getConfigTemplate() {
		return configTemplate;
	}

	public static File getLayoutsDir() {
		return layoutsDir;
	}
	
	public static String getVersionTxt() {
		return versionTxt;
	}

	public static File getLoggingFile() {
		return loggingFile;
	}
}
