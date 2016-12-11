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

public class Unpacker {
	
	private static Path storePath;
	private static File storeFile;

	private static Path loggingPath;
	private static File loggingFile;
	
	private static File templateFile;
	private static File configTemplate;
	private static String versionTxt;
	private static File layoutsDir;
	
	public static boolean propsReset;
	
	public static void unpack() {
		int changes = 0;
		boolean success = true; 
		loadVersionString();
		storePath  = Paths.get(System.getProperty("user.home"),".store","seoda","data");
		loggingPath = Paths.get(System.getProperty("user.home"),".store","seoda","logging");
		storeFile = storePath.toFile();
		if(!storeFile.exists()) {
			System.out.println("UNPACKER : Creating store directory...");
			try {
				Files.createDirectories(storePath);
				System.out.println("Success.");
				changes++;
			} catch (IOException e) {
				System.out.println("ERROR : Failed to create store directory.");
				success = false;
			}
		}
		if(!loggingPath.toFile().exists()) {
			System.out.println("UNPACKER : Creating logging directory...");
			try {
				Files.createDirectories(loggingPath);
				System.out.println("Success.");
				changes++;
			} catch (IOException e) {
				System.out.println("ERROR : Failed to create logging directory.");
				success = false;
			}
		}
		loggingFile = Paths.get(loggingPath.toString(),"log"+System.currentTimeMillis()+".txt").toFile();
		if(!loggingFile.exists()) {
			try {
				loggingFile.createNewFile();
			} catch (IOException e) {
				System.out.println("UNPACKER : Couldn't create logging file.");
			}
		}
		File config = Paths.get(storePath.toString(),"config.properties").toFile();
		if(!config.exists()) {
			try {
				copyToFile(Unpacker.class.getClassLoader().getResourceAsStream("config.properties"),config);
				System.out.println("UNPACKER : Created default config document.");
				changes++;
			} catch (IOException e) {
				System.out.println("UNPACKER : Couldn't make configuration directory in store dir.");
				e.printStackTrace();
				success = false;
			}
		}
		File layout = Paths.get(storePath.toString(),"layouts").toFile();
		if(!layout.exists()) {
			try {
				loadLayouts(layout);
				System.out.println("UNPACKER : created layout templates.");
				changes++; 
			} catch (IOException e) {
				System.out.println("UNPACKER : Couldn't make layouts directory in store dir.");
				e.printStackTrace();
				success = false;
			}
		}
		if(success) {
			System.out.println(Ansi.GREEN+"UNPACKER : Unpacked, " + changes + " changes made."+Ansi.SANE);
		}
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
			System.out.println("UNPACKER : Error during config reset.");
		}
	}
	
	public static void resetLayoutTemplates() {
		File layout = Paths.get(storePath.toString(),"layouts").toFile();
		if(layout.delete()) {
			System.out.println("UNPACKER : Old layout templates deleted.");
		}
		try {
			loadLayouts(layout);
			System.out.println("UNPACKER : layout templates reset.");
		} catch (IOException e) {
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
