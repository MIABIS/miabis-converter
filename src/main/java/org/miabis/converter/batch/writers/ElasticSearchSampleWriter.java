package org.miabis.converter.batch.writers;

import java.util.ArrayList;
import java.util.List;

import org.miabis.converter.transform.IndexableSample;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import static org.springframework.util.Assert.state;

public class ElasticSearchSampleWriter implements ItemWriter<IndexableSample>, InitializingBean{

	private ElasticsearchOperations elasticsearchOperations;	
	private boolean delete;
	
	public ElasticSearchSampleWriter(ElasticsearchOperations elasticsearchOperations) {
		delete = false;
		this.elasticsearchOperations = elasticsearchOperations;
	}
	
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		state(elasticsearchOperations != null, "An ElasticsearchOperations implementation is required.");
	}
	
	@Override
	public void write(List<? extends IndexableSample> items) throws Exception {
		
		List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
		IndexQueryBuilder builder = new IndexQueryBuilder();
		
		for(IndexableSample item : items){
			if(delete) {
				elasticsearchOperations.delete(item.getClass(), item.getId());
			}else{
				indexQueries.add(builder.withId(item.getId()).withObject(item).build());
			}
		}
		
		if(indexQueries.size() > 0)
			elasticsearchOperations.bulkIndex(indexQueries);
	}
} 
