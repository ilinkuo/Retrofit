package gov.usgs.cida.retrofit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SimpleParseState implements ParseState{
	protected String chunkName;
	protected Set<String> fields;
	protected boolean inChunk;
	protected boolean inField;
	protected Map<String, String> values = new HashMap<String, String>();
	protected List<String> headers;
	protected Stack<String> stack = new Stack<String>();
	protected String currFieldName;
	protected String previousEndTag;

	public SimpleParseState(String chunkName, String... fields) {
		this.chunkName = chunkName;
		this.headers = new ArrayList<String>();
		this.headers.addAll(Arrays.asList(fields));
		this.fields = new HashSet<String>();
		this.fields.addAll(headers);
	}
	// ==================
	// enter() and exit() 
	//		should be called before state querying methods
	// ==================
	@Override
	public void enter(String... tags) {
		if (tags == null || tags.length == 0) throw new IllegalArgumentException("Must enter tags");
		// handle a single tag
		if (tags.length == 1) {
			String tag = tags[0];
			if (chunkName.equals(tag)) {
				inChunk = true;
				// reset the values
				values.clear();
			} else if (inChunk && fields.contains(tag)) {
				inField = true;
				currFieldName = tag;
			}
			
			stack.push(tag);
			return;
		}
		
		// more than one tag
		for (String tag: tags) {
			enter(tag);
		}
	}
	
	@Override
	public void exit(String... tag) {
		String closeTag =  stack.peek();
		if (tag != null && tag.length > 0) {
			// if end tag provided, validate
			if ( !closeTag.equals(tag[0])) throw new IllegalStateException("end tag mismatch: expected " + closeTag + " but was " + tag[0]);
		}
		if (chunkName.equals(closeTag)) {
			inChunk = false;
		} else if (inChunk && fields.contains(closeTag)) {
			inField = false;
			currFieldName = null;
		}
		previousEndTag = closeTag;
		stack.pop();
	}
	
	// ===============================
	@Override
	public boolean inChunk() {return inChunk;}
	
	@Override
	public boolean inField() {return inField;}
	
	@Override
	public boolean onChunkEnd() {
		return !inChunk && chunkName.equals(previousEndTag);
	}
	
	@Override
	public boolean isField(String attribName) {
		return fields.contains(attribName);
	}
	
	@Override
	public String putFieldValue(String fieldValue) {
		if (currFieldName == null) throw new IllegalStateException("error: must be inField in order to put a field value");
		String currentValue = values.get(currFieldName);
		if (fieldValue == null) return fieldValue;
		if (currentValue == null) return values.put(currFieldName, fieldValue);
		return values.put(currFieldName, currentValue + fieldValue); 
	}
	

	@Override
	public List<String> getHeaders(){
		List<String> safeCopy = new ArrayList<String>();
		safeCopy.addAll(headers);
		return safeCopy;
	}
	
	@Override
	public List<String> getData() {
		List<String> result = new ArrayList<String>();
		for (String header: headers) {
			result.add(values.get(header));
		}
		return result;
	}
	

	@Override
	public void enterAttribute(String attr, String value) {
		String attrField = "@" + attr;
		if (inChunk && fields.contains(attrField)) {
			values.put(attrField, value);
		}
	}
	
	@Override
	public int getLevel() {return stack.size();}
	
	@Override
	public String getCurrent() {
		return getLevel() + ": " + ((stack.isEmpty())? "empty": stack.peek());
	}
	

	

}
