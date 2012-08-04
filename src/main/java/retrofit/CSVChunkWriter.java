package gov.usgs.cida.retrofit;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVChunkWriter {
	protected Writer writer;
	protected boolean isFirstChunkOutput;
	private String delim;

	public CSVChunkWriter(Writer writer, String delimiter) {
		this.writer = writer;
		this.delim = delimiter;
	}
	
	public CSVChunkWriter(Writer writer) {
		this(writer, ",");
	}
	
	public void writeChunk(List<String> headers, List<String> data) throws IOException {
		if (!isFirstChunkOutput) {
			writeHeaders(headers);
			isFirstChunkOutput = true;
		}
		writeData(data);
	}
	
	public void writeData(List<String> data) throws IOException {
		write(data);
	}
	
	public void writeHeaders(List<String> headers) throws IOException {
		write(headers);
	}
	
	protected void write(List<String> data) throws IOException {
		// TODO worry about escaping later
		StringBuilder sb = new StringBuilder();
		String d = "";
		for (String item: data) {
			sb.append(d).append(item);
			d = delim;
		}
		sb.append("\n");
		writer.write(sb.toString());
	}


	
	public void flush() throws IOException { writer.flush(); }
}
