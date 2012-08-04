package gov.usgs.cida.retrofit;

import java.util.List;

public interface ParseState {

	// Consider removing the tag argument from here, so to conform to JSON closing brace
	void exit(String... tag);

	void enter(String... tag);

	List<String> getData();

	List<String> getHeaders();

	/**
	 * Associates the value with the current field. For all formats except XML, this is a 
	 * simple put. For XML, the inner character content may be separated by tags, 
	 * so this is an append operation which excludes tag-enclosed content, so it's 
	 * not really conforming to the XML model either.
	 * 
	 * @param fieldValue
	 * 
	 * @return
	 */
	String putFieldValue(String fieldValue);

	boolean isField(String attribName);

	boolean inField();

	boolean inChunk();
	
	boolean onChunkEnd();

	int getLevel();

	void enterAttribute(String attr, String value);

	String getCurrent();

}
