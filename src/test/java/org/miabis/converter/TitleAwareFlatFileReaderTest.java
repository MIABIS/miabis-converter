package org.miabis.converter;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.batch.reader.TitleAwareFlatFileItemReader;
import org.miabis.exchange.schema.Sample;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "classpath:**/TitleAwareFlatFileReaderTest.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TitleAwareFlatFileReaderTest {

	@Autowired
	@Qualifier("titleUnawareFlatFileReader")
	private TitleAwareFlatFileItemReader<Sample> unawareReader;
	
	@Autowired
	@Qualifier("titleAwareFlatFileReader")
	private TitleAwareFlatFileItemReader<Sample> awareReader;
	
	private static final String DIRECTORY = "src/test/resources/data/input/";
	
	//Set up readers to begin test
	@Before
	public void setUpReaders(){
		
		awareReader.setResource(new FileSystemResource(DIRECTORY + "input1.txt"));
		awareReader.open(new ExecutionContext());
		
		unawareReader.setResource(new FileSystemResource(DIRECTORY + "input2.txt"));
		unawareReader.open(new ExecutionContext());
	}
	
	//Read first line an compare the result samples
	@Test
	public void testMapperAndExtractor() throws Exception{
		Sample unawareSample = unawareReader.read();
		Sample awareSample = awareReader.read();
		
		Assert.assertEquals(unawareSample, awareSample);
	}
}
