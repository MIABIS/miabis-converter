package org.miabis.converter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.miabis.converter.batch.reader.TitleAwareFlatFileItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "classpath:**/TitleAwareFieldSetMapperTest.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TitleAwareFieldSetMapperTest {

	@Autowired
	@Qualifier("titleAwareFlatFileReader")
	private TitleAwareFlatFileItemReader<Map<String,String>> awareReader;
	
	@Autowired
	@Qualifier("swappedFlatFileReader")
	private TitleAwareFlatFileItemReader<Map<String,String>> swappedReader;
	
	private Map<String, String> expectedMap;
	
	private static final String DIRECTORY = "src/test/resources/data/input/";
	
	//Set up readers to begin test
	@Before
	public void setUpReaders(){
		awareReader.setResource(new FileSystemResource(DIRECTORY + "contactInfo.txt"));
		swappedReader.setResource(new FileSystemResource(DIRECTORY + "contactInfoSwapped.txt"));
	}
	
	
	@Test
	public void testTitleAwareFieldSetMapper() throws Exception{
		
		awareReader.open(new ExecutionContext());
		
		Map<String, String> line = awareReader.read();
		
		Assert.assertEquals(expectedMap, line);
		
		awareReader.close();
	}
	
	@Test
	public void testSwappedTitleAwareFieldSetMapper() throws Exception{
		
		swappedReader.open(new ExecutionContext());
		
		Map<String, String> line = swappedReader.read();
		
		Assert.assertEquals(expectedMap, line);
		
		swappedReader.close();
	}

	@Before
	public void populateMap(){
		expectedMap = new HashMap<String,String>();
		expectedMap.put("contactInformation_id", "1");
		expectedMap.put("contactInformation_firstName", "Bilbo");
		expectedMap.put("contactInformation_lastName", "Baggins");
		expectedMap.put("contactInformation_phone", "");
		expectedMap.put("contactInformation_email", "bilbo@middleearth.com");
		expectedMap.put("contactInformation_address", "Bag End 01");
		expectedMap.put("contactInformation_zip", "");
		expectedMap.put("contactInformation_city", "Hobbiton");
		expectedMap.put("contactInformation_country", "Middle Earth");
	}
}
