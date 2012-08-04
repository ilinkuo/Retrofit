package gov.usgs.cida.retrofit;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

public class XML2CSVServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Parse input params
		String url = req.getParameter("url");
		String chunkName = req.getParameter("chunk");
		String[] fields = req.getParameterValues("fields");
		if (fields.length == 1 && fields[0].contains(",")) {
			fields = fields[0].split(",");
		}
		
		// using temporary syntax for now. Looking for better syntax, possibly involving lambda expressions
		// Only one specified transform allowed currently
		String[] transforms = req.getParameterValues("transforms");
		//  TODO, add parsing into multiple transforms
		// Only one transform allowed for now
		Transform transform = null;
		if (transforms != null && transforms.length > 0) {
			transform = new Transform(transforms[0]);
		}
		
		// TODO transforms, aliases
		XMLInputFactory factory = StAXFactory.getXMLInputFactory();
		XMLStreamReader reader = null;
		
		Harvester harvester = new Harvester();
		int status = harvester.wget(url);
		
		try {
			reader = factory.createXMLStreamReader(harvester.getInputStream());
		} catch (XMLStreamException e) {
			// TODO improve error handling
			e.printStackTrace();
			return;
		}
		ParseState ps = new SimpleParseState(chunkName, fields);
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		
		XML2CSV converter = new XML2CSV();
		converter.addTransforms(transform);
		try {
			converter.convert(reader, ps, out);
		} catch (XMLStreamException e) {
			out.write(e.getMessage());
			e.printStackTrace();
		} finally {
			out.flush();
		}
		
		
	}


	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	

}
