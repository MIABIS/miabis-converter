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

@ContextConfiguration(locations={"/spring/batch/config/database.xml", "/spring/batch/jobs/job-csv-db.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class JobFilesDBTabTest {
	
	private static final String DIRECTORY = "data/input/";
	
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
		pb.addString("contactInfo", DIRECTORY + "contactInfo.txt");
		jobLauncher.run(job, pb.toJobParameters());
	}

}
