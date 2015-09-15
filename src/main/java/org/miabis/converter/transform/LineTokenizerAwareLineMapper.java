package org.miabis.converter.transform;

import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class LineTokenizerAwareLineMapper extends DefaultLineMapper<String> {

	protected DelimitedLineTokenizer tokenizer;
	
	public void setLineTokenizer(DelimitedLineTokenizer tokenizer) {
		this.tokenizer = tokenizer;
		super.setLineTokenizer(tokenizer);
	}

	public DelimitedLineTokenizer getTokenizer() {
		return tokenizer;
	}
}
