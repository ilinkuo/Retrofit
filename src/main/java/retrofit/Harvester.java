package gov.usgs.cida.retrofit;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copied from ngwmn project package gov.usgs.ngwmn.dm.harvest.Harvester
 * @author ilinkuo
 *
 */
public class Harvester {
	protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	
	private InputStream    is;
	private int    statusCode;
	private HttpClient client;

	
	public InputStream getInputStream() {
		return is;
	}
	public int getStatusCode() {
		return statusCode;
	}
	
	public int wget(String url) throws IOException {
		logger.info("wget from {}",url);
		
		try {
			
			client = new HttpClient();
			
			HttpMethod method = new GetMethod(url);
			
			statusCode = client.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {
				return statusCode;
			}
			is = method.getResponseBodyAsStream();

			return statusCode;
			
		} catch (IOException e) {
			String msg = "error wget from " + url; // need to concatenate for new exception msg
			logger.error(msg, e); // so I might as well use it here
			throw new IOException(msg, e);
		}
	}

}

