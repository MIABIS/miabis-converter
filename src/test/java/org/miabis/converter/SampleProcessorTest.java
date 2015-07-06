package org.miabis.converter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.batch.processors.SampleProcessor;
import org.miabis.converter.transform.IndexableSample;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.Temperature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "/spring/batch/jobs/job-csv-index.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SampleProcessorTest {

	@Autowired
	private SampleProcessor processor;
	
	@Test
	public void processorShouldReturnNull() throws Exception{
		IndexableSample is = processor.process(new Sample());
		assertNull(is);
	}
	
	@Test
	public void processorShouldReturnIndexableSample() throws Exception{
		Sample s = new Sample();
		
		s.setId("mySample");
		s.setParentSampleId("parentSample");
		s.setSampledTime(LocalDateTime.now());
		s.getMaterialType().add(MaterialType.C_DNA_M_RNA);
		s.getStorageTemperature().add(Temperature.CENTIGRADES_2_TO_10);
		
		OntologyTerm as = new OntologyTerm();
		as.setId("id");
		as.setOntology("ontology");
		as.setVersion("version");
		as.setCode("code");
		as.setDescription("desc");
		
		s.setAnatomicalSite(as);
		
		Biobank bb = new Biobank();
		bb.setId("bb");
		bb.setAcronym("acronym");
		bb.setName("name");
		bb.setUrl("https://github.com/MIABIS/");
		bb.setDescription("A biobank description");
		bb.setCountry("ES");
		
		ContactInformation ci = new ContactInformation();
		ci.setId("ciid");
		ci.setFirstname("pepin");
		ci.setLastname("peres");
		ci.setPhone("+490176551234");
		ci.setEmail("pepin@veryimpotantcompany.com");
		ci.setAddress("zeppelin strasse 12");
		ci.setZip("54123");
		ci.setCity("Hamburg");
		ci.setCountry("DE");
		
		bb.getContactInformation().add(ci);
		s.setBiobank(bb);
		
		IndexableSample is = processor.process(s);
		assertNotNull(is);
	}

}
