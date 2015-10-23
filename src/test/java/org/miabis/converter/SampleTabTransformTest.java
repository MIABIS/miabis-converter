package org.miabis.converter;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.CollectionType;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.DataCategory;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.InclusionCriteria;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Sex;
import org.miabis.exchange.schema.Study;
import org.miabis.exchange.schema.TimeUnit;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "classpath:**/SampleTabTransformTest.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SampleTabTransformTest {
	
	private static Sample sample;
	private static Biobank bb;
	private static SampleCollection sc;
	private static Study study;
	
	@Autowired
	private DelimitedLineAggregator<Sample> aggregator;
	
	@Autowired
	private DefaultLineMapper<Sample> mapper;
	
	@Test
	public void testMapperAndExtractor() throws Exception{
		
		String sampleStr = aggregator.aggregate(sample);
		
		Sample sample1 = mapper.mapLine(sampleStr, 0);
		
		assertEquals(sample.getBiobank(), sample1.getBiobank());
		assertEquals(sample.getStudy(), sample1.getStudy());
		
		assertEquals(sample.getSamplecollection(), sample1.getSamplecollection());
		assertEquals(sample.getAnatomicalSite(), sample.getAnatomicalSite());
		assertEquals(sample.getMaterialType(), sample.getMaterialType());
		assertEquals(sample.getStorageTemperature(), sample.getStorageTemperature());
		assertEquals(sample.getId(), sample.getId());
	}
	
	
	@Test
	public void testEmptySample() throws Exception{
		String sampleStr = aggregator.aggregate(new Sample());
		mapper.mapLine(sampleStr, 0);
	}
	
	
	@Test
	public void testIncompleteContactInfo() throws Exception{
		ContactInformation ci = new ContactInformation();
		ci.setCity("Macondo");
		
		bb.setContactInformation(ci);
		
		Sample sample = new Sample();
		sample.setBiobank(bb);
		
		String sampleStr = aggregator.aggregate(sample);
		Sample mappedSample = mapper.mapLine(sampleStr, 0);
		
		assertEquals(sample.getBiobank(), mappedSample.getBiobank());
	}
	
	@Test
	public void testIncompleteDisease() throws Exception{
		Disease d = new Disease();
		d.setCode("123");
		
		Sample sample = new Sample();
		sample.setDisease(d);
		
		String sampleStr = aggregator.aggregate(sample);
		Sample mappedSample = mapper.mapLine(sampleStr, 0);
		
		assertEquals(sample.getDisease(), mappedSample.getDisease());
	}
	
	//Create a sample
	@BeforeClass 
	public static void prepare() throws DatatypeConfigurationException{
		
		sample = new Sample();
		sample.setId("mySample");
		sample.setParentSampleId("parentSample");
		sample.setSampledTime(LocalDateTime.now());
		sample.setMaterialType(MaterialType.C_DNA_M_RNA);
		sample.setStorageTemperature(-10);
		
		OntologyTerm as = new OntologyTerm();
		as.setId("id");
		as.setOntology("ontology");
		as.setVersion("version");
		as.setCode("code");
		as.setDescription("desc");
		
		sample.setAnatomicalSite(as);
		
		sample.setAgeLow(5);
		sample.setAgeHigh(90);
		sample.setAgeUnit(TimeUnit.YEAR);
		
		Disease d = new Disease();
		d.setId("disease id");
		d.setOntology("ontology");
		d.setVersion("version");
		d.setCode("code");
		d.setDescription("desc");
		d.setFreeText("free text");
		
		sample.setDisease(d);
		
		sample.setSex(Sex.FEMALE);
		
		//Biobank
		bb = new Biobank();
		bb.setId("my bb");
		bb.setAcronym("acronym");
		bb.setName("name");
		bb.setUrl("https://github.com/MIABIS");
		bb.setDescription("A biobank description");
		bb.setCountry("ES");
		bb.setJuristicPerson("");
		
		ContactInformation ci = new ContactInformation();
		ci.setId("ci id");
		ci.setFirstname("pepin");
		ci.setLastname("peres");
		ci.setPhone("+490176551234");
		ci.setEmail("pepin@veryimpotantcompany.com");
		ci.setAddress("zeppelin strasse 12");
		ci.setZip("54123");
		ci.setCity("Hamburg");
		ci.setCountry("DE");
		
		bb.setContactInformation(ci);
		sample.setBiobank(bb);
		
		//Sample Collection
		sc = new SampleCollection();
		sc.setId("my SC");
		sc.setAcronym("sc acronym");
		sc.setName("sc name");
		sc.setDescription("just a sample col");
		
		List<DataCategory> dCat = sc.getDataCategory();
		dCat.add(DataCategory.BIOLOGICAL_SAMPLES);
		dCat.add(DataCategory.IMAGING_DATA);
		
		List<CollectionType> ctLst = sc.getCollectionType();
		ctLst.add(CollectionType.BIRTH_COHORT);
		ctLst.add(CollectionType.LONGITUDINAL);
		
		sc.setContactInformation(ci);
				
		sample.setSamplecollection(sc);
		
		//Study
		study = new Study();
		study.setId("my study");
		study.setName("study name");
		study.setDescription("just a study");
		
		study.setPrincipalInvestigator("Some guy");
		study.setContactInformation(ci);
		
		List<CollectionType> sDesign = study.getStudyDesign();
		sDesign.add(CollectionType.BIRTH_COHORT);
		sDesign.add(CollectionType.DISEASE_SPECIFIC);
		
		List<DataCategory> sDCat = study.getDataCategory();
		sDCat.add(DataCategory.GENEALOGICAL_RECORDS);
		
		study.setTotalNumberOfParticipants(2147483647);
		study.setTotalNumberOfDonors(2147483647);
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		iLst.add(InclusionCriteria.AGE_GROUP);
		iLst.add(InclusionCriteria.ETHNIC_ORIGIN);
		
		sample.setStudy(study);
	}
}
