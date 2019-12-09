package it.eng.ms.restservice.security;


import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.conn.ssl.;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class GetUserInfo {
	private static String userInfoEndpointUri="https://oidc-provider:3043/me";
//	public static String _OLD_get(String accessToken) {
//        try {
//			RestTemplate restTemplate = getRestTemplate();
//			HttpHeaders headers = new HttpHeaders();
//			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
//
//			HttpEntity<String> entity = new HttpEntity<String>("", headers);
//
//			ResponseEntity<String> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, String.class);
//			String userAttributes = response.getBody();
//			return userAttributes;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public static String get(String accessToken)  {
        try {
			TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager() {
			            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			                return null;
			            }
			            public void checkClientTrusted(X509Certificate[] certs, String authType) {
			            }
			            public void checkServerTrusted(X509Certificate[] certs, String authType) {
			            }
			        }
			};
 
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
 
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid;
			allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
 
			URL url = new URL(userInfoEndpointUri);
			URLConnection con = url.openConnection();
			con.setRequestProperty ("Authorization", "Bearer " + accessToken);
			ByteArrayOutputStream write = new ByteArrayOutputStream();
			InputStream read = con.getInputStream();
			while(true) {
				int b = read.read();
				if (b<0)
					break;
				write.write(b);
			}
			byte bytes [] = write.toByteArray();
			write.close();
			String ret = new String(bytes, Charset.forName("UTF-8"));
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	public static RestTemplate getRestTemplate() throws Exception {
//		// from: https://stackoverflow.com/questions/4072585/disabling-ssl-certificate-validation-in-spring-resttemplate
//		
//	    TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
//	        @Override
//	        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//	            return true;
//	        }
//	    };
//	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
//	    		.loadTrustMaterial(acceptingTrustStrategy)
//	    		.build();
//	    
//	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//
//	    CloseableHttpClient httpClient = HttpClients.custom()
//	                    .setSSLSocketFactory(csf)
//	                    .build();
//
//	    HttpComponentsClientHttpRequestFactory requestFactory =
//	                    new HttpComponentsClientHttpRequestFactory();
//
//	    requestFactory.setHttpClient(httpClient);
//	    RestTemplate restTemplate = new RestTemplate(requestFactory);
//	    return restTemplate;
//	}

}

