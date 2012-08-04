package gov.usgs.cida.retrofit;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XML2CSV {
	protected List<Transform> transforms;

	public void convert(XMLStreamReader in, ParseState ps, Writer out) throws XMLStreamException, IOException {

		CSVChunkWriter cw = new CSVChunkWriter(out);

		while (in.hasNext()) {
			int type = in.next();
			switch(type) {
				case XMLStreamConstants.START_ELEMENT:
					ps.enter(in.getLocalName());
					for (int i=0; i<in.getAttributeCount(); i++) {
						ps.enterAttribute(in.getAttributeLocalName(i), in.getAttributeValue(i));
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					ps.exit(); 
					if (ps.onChunkEnd()) {
						// HACK
						List<String> data = applyTransforms(ps.getHeaders(), ps.getData());
						
						cw.writeChunk(ps.getHeaders(), data);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if (ps.inField()) {
						ps.putFieldValue(in.getText());
					}
					break;
			}
			// HACK -- Some streams (BuggyStringXMLReaderWrapper.java) have inaccurate hasNext() information on end element
			if (ps.getLevel() == 0) break;

		}
		cw.flush();
		return;
	}

	private static List<String> applyTransforms(List<String> headers,
			List<String> data) {
		// XML data needs to trim off whitespace (whitespace normalization)
		List<String> result = new ArrayList<String>(data.size());
		for (String item: data) {
			item = (item == null)? null : item.trim();
			result.add(item);
		}
		return result;
	}

	public void addTransforms(Transform... transform) {
		// don't add null or invalid transforms
		List<Transform> transforms = new ArrayList<Transform>();
		for (Transform tr: transform) {
			if (tr != null && tr.isValid) transforms.add(tr);
		}
		this.transforms = transforms;				
	}

}
