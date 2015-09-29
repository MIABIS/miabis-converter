package org.miabis.converter.batch.writers;

import static org.springframework.util.Assert.state;

import java.util.ArrayList;
import java.util.List;

import org.miabis.converter.transform.IndexableSample;
import org.miabis.exchange.schema.Sample;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

/**
 * Writes <i>IndexableSamples</i> in a elastic search instance.
 * @author jvillaveces
 */
public class ElasticSearchSampleWriter implements ItemWriter<Sample>, InitializingBean{

	/** String data object to handle elasticsearch queries */
	private ElasticsearchOperations elasticsearchOperations;
	/** Whether or not to delete a sample */
	private boolean delete = false;
	
	public ElasticSearchSampleWriter(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}
	
	/**
	 * Whether or not to delete a sample
	 * @param delete a boolean indicating whether or not to delete a sample
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		state(elasticsearchOperations != null, "An ElasticsearchOperations implementation is required.");
	}
	
	@Override
	public void write(List<? extends Sample> items) throws Exception {
		
		List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
		IndexQueryBuilder builder = new IndexQueryBuilder();
		
		for(Sample sample : items){
			IndexableSample item = new IndexableSample(sample);
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
