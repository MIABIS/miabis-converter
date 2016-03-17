package org.miabis.converter;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.util.EmbeddedElasticsearchServer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/database.xml", "/spring/batch/jobs/job-csv-db-index.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DBIndexJobTest {

	private static final String DIRECTORY = "data/input/";
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	private EmbeddedElasticsearchServer embeddedElasticsearchServer;
	
	@Before
    public void startEmbeddedElasticsearchServer() throws Exception {
        embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
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
    public void testJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException{
    	
    	// should be zero
    	Assert.assertEquals(0, getHits());
    	
    	JobParametersBuilder pb = new JobParametersBuilder();
		pb.addString("contactInfo", DIRECTORY + "contactInfo.txt");
		pb.addString("biobank", DIRECTORY + "biobank.txt");
		pb.addString("sampleCollection", DIRECTORY + "sampleCollection.txt");
		pb.addString("study", DIRECTORY + "study.txt");
		pb.addString("sample", DIRECTORY + "sample.txt");
		
		pb.addString("map","example.mapping.properties");
		
		//Elasticsearch config
		pb.addString("cluster.nodes", "localhost:9300");
		pb.addString("cluster.name", "elasticsearch");
		pb.addString("index.name", "sample");
		
		JobExecution launch = jobLauncher.run(job, pb.toJobParameters());
		
		while(launch.isRunning()){
			Thread.sleep(5000);
		}
		Thread.sleep(5000);
		
		Assert.assertEquals(3, getHits());
    }
    
}
