package org.miabis.converter.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;

/**
 * This class is a simple extension of the FlatFileItemReader. 
 * It is aware of the file titles and passes them to a LineTokenizer by using TitleAwareSkippedLinesCallback 
 * @author jvillaveces
 *
 * @param <T>
 */
public class TitleAwareFlatFileItemReader<T> extends FlatFileItemReader<T> {

	protected boolean hasTitles = false;
	
	public TitleAwareFlatFileItemReader(){
		super();
	}

	@Override
	public void open(ExecutionContext executionContext) {
		
		//If there are titles, then skip first line
		if(hasTitles){
			this.setLinesToSkip(1);
		}
		super.open(executionContext);
	}

	public boolean isHasTitles() {
		return hasTitles;
	}

	public void setHasTitles(boolean hasTitles) {
		this.hasTitles = hasTitles;
	}
}
