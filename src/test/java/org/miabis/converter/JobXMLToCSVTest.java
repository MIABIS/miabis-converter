package org.miabis.converter;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "/spring/batch/jobs/job-xml-csv.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JobXMLToCSVTest {
	
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;
	
	@Test
	public void testSimpleProperties() throws Exception {
		assertNotNull(jobLauncher);
	}
	
	@Test
	public void testLaunchJob() throws Exception {
		JobParametersBuilder pb = new JobParametersBuilder();
		pb.addString("xml.input", "classpath:sample-exchange-schema.xml");
		pb.addString("tab.output", "file:out/sample.tab");
		
		jobLauncher.run(job, pb.toJobParameters());
	}
}
