package org.miabis.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
	private TitleAwareFlatFileItemReader<String[]> awareReader;
	
	@Autowired
	@Qualifier("swappedFlatFileReader")
	private TitleAwareFlatFileItemReader<String[]> swappedReader;
	
	private String[] expectedLine;
	
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
		
		String[] line = awareReader.read();
		Assert.assertArrayEquals(expectedLine, line);
		
		awareReader.close();
	}
	
	@Test
	public void testSwappedTitleAwareFieldSetMapper() throws Exception{
		
		swappedReader.open(new ExecutionContext());
		
		String[] line = swappedReader.read();
		
		Assert.assertArrayEquals(expectedLine, line);
		
		swappedReader.close();
	}

	@Before
	public void populateMap(){
		expectedLine = new String[] {"1", "Bilbo", "Baggins", null, "bilbo@middleearth.com", "Bag End 01", null, "Hobbiton", "Middle Earth"};
	}
}
