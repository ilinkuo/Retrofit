package gov.usgs.cida.retrofit;

import javax.xml.stream.XMLInputFactory;

import org.codehaus.stax2.XMLInputFactory2;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * copied from StaxFactory in ngwmn, package gov.usgs.ngwmn.dm.io.parse.StAXFactory;
 * @author ilinkuo
 *
 */
public abstract class StAXFactory {

	private static XMLInputFactory xmlInputFactory;
	
	public static synchronized XMLInputFactory getXMLInputFactory() {
		if (xmlInputFactory == null) {
			XMLInputFactory2 xmlInputFactory2 = new WstxInputFactory();
			xmlInputFactory2.configureForSpeed();
			xmlInputFactory = xmlInputFactory2;
		}
		return xmlInputFactory;
	}

}