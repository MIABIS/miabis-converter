package org.miabis.converter.batch.reader;

import org.miabis.converter.transform.LineTokenizerAwareLineMapper;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

/**
 * This class is a simple extension of the FlatFileItemReader. 
 * It is aware of the file titles and passes them to a LineTokenizer by using TitleAwareSkippedLinesCallback 
 * @author jvillaveces
 *
 * @param <T>
 */
public class TitleAwareFlatFileItemReader<T> extends FlatFileItemReader<T> implements LineCallbackHandler {

	protected boolean hasTitles = false;
	protected String delimiter;
	protected LineTokenizerAwareLineMapper lineMapper;
	
	public TitleAwareFlatFileItemReader(){
		super();
	}

	@Override
	public void open(ExecutionContext executionContext) {
		
		//If there are titles, then skip first line
		if(hasTitles){
			this.setLinesToSkip(1);
			this.setSkippedLinesCallback(this);
		}
		super.open(executionContext);
	}

	@Override
	public void handleLine(String line) {
		lineMapper.getTokenizer().setNames(line.split(delimiter));
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public boolean isHasTitles() {
		return hasTitles;
	}

	public void setHasTitles(boolean hasTitles) {
		this.hasTitles = hasTitles;
	}

	public LineTokenizerAwareLineMapper getLineMapper() {
		return lineMapper;
	}

	public void setLineMapper(LineTokenizerAwareLineMapper lineMapper) {
		this.lineMapper = lineMapper;
		super.setLineMapper((LineMapper<T>) lineMapper);
	}
}
