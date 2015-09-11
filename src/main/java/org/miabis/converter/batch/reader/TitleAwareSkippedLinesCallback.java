package org.miabis.converter.batch.reader;

import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

/**
 * This class is supposed to work with TitleAwareFlatFileReader. 
 * It listens to the file reader lines skipped event and sets the LineTokenizer names accordingly
 * @author jvillaveces
 *
 */
public class TitleAwareSkippedLinesCallback implements LineCallbackHandler {

	protected DelimitedLineTokenizer lineTokenizer;
	protected String delimiter;
	
	public TitleAwareSkippedLinesCallback() {}

	//Sets LineTokenizer names
	@Override
	public void handleLine(String line) {
		lineTokenizer.setNames(line.split(delimiter));
	}

	public DelimitedLineTokenizer getLineTokenizer() {
		return lineTokenizer;
	}

	public void setLineTokenizer(DelimitedLineTokenizer lineTokenizer) {
		this.lineTokenizer = lineTokenizer;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
