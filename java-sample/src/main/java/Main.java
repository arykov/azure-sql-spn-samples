import com.microsoft.aad.msal4j.*;

import java.sql.DriverManager;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class Main{
	public static void  main(String ... args)throws Exception{
		TENANT_ID = 'provide your AAD tenant id'
		String SERVER_NAME = "Azure SQL server", DATABASE_NAME = "Azure SQL server";
		String TENANT_ID='provide your AAD tenant id';
		String CLIENT_ID="client id of your SPN", SECRET = "secret of your SPN";

		Set<String> SCOPE = Collections.singleton("https://database.windows.net/.default");
		String JDBC_URL = String.format("jdbc:sqlserver://%s;database=%s", SERVER_NAME, DATABASE_NAME);

		try {
			Properties props = new Properties();
			props.put("authentication", "ActiveDirectoryServicePrincipal");
			props.put("aadSecurePrincipalId", CLIENT_ID);
			props.put("aadSecurePrincipalSecret", SECRET);
			DriverManager.getConnection(JDBC_URL, props);
			System.out.println("Succeeded ActiveDirectoryServicePrincipal");
		}catch(Exception ex){
			System.err.println("Failed ActiveDirectoryServicePrincipal");
			ex.printStackTrace();
		}

		IClientCredential credential = ClientCredentialFactory.createFromSecret(SECRET);
		ConfidentialClientApplication cca =
				ConfidentialClientApplication
						.builder(CLIENT_ID, credential)
						.authority(String.format("https://login.microsoftonline.com/%s", TENANT_ID))
						.build();

		IAuthenticationResult result;
		try {
			SilentParameters silentParameters =
					SilentParameters
							.builder(SCOPE)
							.build();

			// try to acquire token silently. This call will fail since the token cache does not
			// have a token for the application you are requesting an access token for
			result = cca.acquireTokenSilently(silentParameters).join();
		} catch (Exception ex) {
			if (ex.getCause() instanceof MsalException) {

				ClientCredentialParameters parameters =
						ClientCredentialParameters
								.builder(SCOPE)
								.build();

				// Try to acquire a token. If successful, you should see				
				result = cca.acquireToken(parameters).join();
			} else {
				throw ex;
			}
		}

		Properties props = new Properties();
		props.put("accessToken", result.accessToken());
		try {			
			DriverManager.getConnection(JDBC_URL, props);
			System.out.println("Succeeded with token");
		}catch(Exception ex){
			System.err.println("Failed with token");
			ex.printStackTrace();

		}
	}
}