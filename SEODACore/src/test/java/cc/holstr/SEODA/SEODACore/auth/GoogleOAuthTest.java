package cc.holstr.SEODA.SEODACore.auth;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GoogleOAuthTest {
	private GoogleOAuth auth;
	@Before
	public void setUp() throws Exception {
		auth = new GoogleOAuth();
	}

	@Test
	public void testAuthorizeCredential() {
		try {
				auth.authorizeCredential();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(auth.getCredential().getAccessToken());
	}

}
