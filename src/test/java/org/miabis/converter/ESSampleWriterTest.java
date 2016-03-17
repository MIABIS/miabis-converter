package org.miabis.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.batch.writers.ElasticSearchSampleWriter;
import org.miabis.converter.util.EmbeddedElasticsearchServer;
import org.miabis.exchange.schema.Sample;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

//@RunWith(SpringJUnit4ClassRunner.class)
public class ESSampleWriterTest {
	
	private ElasticSearchSampleWriter writer;
	private EmbeddedElasticsearchServer embeddedElasticsearchServer;
	private PodamFactory factory = new PodamFactoryImpl();

	@Before
    public void startEmbeddedElasticsearchServer() throws Exception {
        embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
        
        writer = new ElasticSearchSampleWriter();
        writer.setClusterName("elasticsearch");
        writer.setClusterNodes("localhost:9300");
        writer.setIndex("sample");
        writer.afterPropertiesSet();
    }

    @After
    public void shutdownEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer.shutdown();
    }
    
    protected Client getClient() {
        return embeddedElasticsearchServer.getClient();
    }
    
    
    protected long getHits(){
    	SearchResponse response = getClient().prepareSearch().setTypes("sample").setSearchType(SearchType.QUERY_AND_FETCH).setFetchSource(true).setSize(0).get();
    	return response.getHits().getTotalHits();
    }
    
    @Test
    public void testWriter() throws Exception{
    	//There should be no samples at this time
    	Assert.assertEquals(0, getHits());
    	
    	//index a few samples
    	List<Sample> samples = new ArrayList<Sample>();
    	for(int i=0; i<1000; i++){
    		Sample sample = factory.manufacturePojo(Sample.class);
    		sample.setId("id_"+i);
    		samples.add(sample);
    	}
    	writer.write(samples);
    	
    	//Wait a while to check if documents are indexed
    	Thread.sleep(5000);
    	
    	//There should be 1000 hits
    	Assert.assertEquals(1000, getHits());
    	
    	//set writer to delete
    	writer.setDelete(true);
    	
    	//delete the samples
    	writer.write(samples);
    	
    	//Wait a while to check if documents are deleted
    	Thread.sleep(5000);
    	
    	//There should be 0 hits
    	Assert.assertEquals(0, getHits());
    }
    

}
