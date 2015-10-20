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
	private static final String DIRECTORY_OUT = "data/output/";
	
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
		pb.addString("biobank", DIRECTORY + "biobank.txt");
		pb.addString("sampleCollection", DIRECTORY + "sampleCollection.txt");
		pb.addString("study", DIRECTORY + "study.txt");
		pb.addString("sample", DIRECTORY + "sample.txt");
		
		pb.addString("map","example.mapping.properties");
		pb.addString("tab.output",DIRECTORY_OUT + "db.out.tab");
		jobLauncher.run(job, pb.toJobParameters());
	}

}
