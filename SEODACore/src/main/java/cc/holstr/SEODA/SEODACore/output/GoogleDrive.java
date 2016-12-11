package cc.holstr.SEODA.SEODACore.output;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import cc.holstr.SEODA.SEODACore.auth.GoogleOAuth;
import cc.holstr.SEODA.SEODACore.properties.Unpacker;

public class GoogleDrive {
	/* GoogleDrive.java
	 * by zudsniper @ github
	 * 
	 * */
	
	//for complex auth (wip)
	public static GoogleOAuth driveAuth; 
	private Drive drive; 
	
	public GoogleDrive() {
	init();
	}
	
	private void init() {
		   //initialize drive
			if(GoogleOAuth.isSimple()) {
				if(GoogleOAuth.isSimpleAuthorized()) {
			    drive = new Drive.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), GoogleOAuth.getSimpleAuth().getCredential()).setApplicationName(
			        GoogleOAuth.getApplicationName()).build();
				}
			} else {
				if(driveAuth.isCredentialInitialized()) {
					drive = new Drive.Builder(GoogleOAuth.getHttpTransport(), GoogleOAuth.getJsonFactory(), driveAuth.getCredential()).setApplicationName(
					        GoogleOAuth.getApplicationName()).build();
				}
			}
		  }
	
	public File createOutputSpreadsheet(String year) throws IOException {
		//make a new spreadsheet
		File body = new File();
		DateFormat df = new SimpleDateFormat("h:mm a MM/dd");
	    body.setTitle("SEODA Output " + year + " Created at "+df.format(new Date()));
	    body.setDescription(year + " SEODA Command Line output document. SEODA " + Unpacker.getVersionTxt());
	    body.setMimeType("application/vnd.google-apps.spreadsheet");

	    File file = drive.files().insert(body).execute();
	    
	    return file;
	}
	
}
