package org.miabis.converter;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

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
import org.miabis.exchange.schema.Temperature;
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
		
		bb.getContactInformation().clear();
		bb.getContactInformation().add(ci);
		
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
		
		sc.getDiseases().add(d);
		
		Sample sample = new Sample();
		sample.setSamplecollection(sc);
		
		String sampleStr = aggregator.aggregate(sample);
		Sample mappedSample = mapper.mapLine(sampleStr, 0);
		
		assertEquals(sample.getSamplecollection(), mappedSample.getSamplecollection());
	}
	
	//Create a sample
	@BeforeClass 
	public static void prepare() throws DatatypeConfigurationException{
		
		sample = new Sample();
		sample.setId("mySample");
		sample.setParentSampleId("parentSample");
		sample.setSampledTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		sample.getMaterialType().add(MaterialType.C_DNA_M_RNA);
		sample.getStorageTemperature().add(Temperature.CENTIGRADES_2_TO_10);
		
		OntologyTerm as = new OntologyTerm();
		as.setId("id");
		as.setOntology("ontology");
		as.setVersion("version");
		as.setCode("code");
		as.setDescription("desc");
		
		sample.setAnatomicalSite(as);
		
		bb = new Biobank();
		bb.setId("my bb");
		bb.setAcronym("acronym");
		bb.setName("name");
		bb.setUrl("https://github.com/MIABIS");
		bb.setDescription("A biobank description");
		bb.setCountry("ES");
		
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
		
		bb.getContactInformation().add(ci);
		sample.setBiobank(bb);
		
		//Sample Collection
		sc = new SampleCollection();
		sc.setId("my SC");
		sc.setAcronym("sc acronym");
		sc.setName("sc name");
		sc.setDescription("just a sample col");
		
		List<Sex> sexLst = sc.getSex();
		sexLst.add(Sex.FEMALE);
		sexLst.add(Sex.MALE);
		sexLst.add(Sex.UNDIFFERENTIATED);
		
		sc.setAgeLow(5);
		sc.setAgeHigh(90);
		sc.setAgeUnit(TimeUnit.YEAR);
		
		List<DataCategory> dCat = sc.getDataCategory();
		dCat.add(DataCategory.BIOLOGICAL_SAMPLES);
		dCat.add(DataCategory.IMAGING_DATA);
		
		List<MaterialType> scMtLst = sc.getMaterialType();
		scMtLst.add(MaterialType.C_DNA_M_RNA);
		scMtLst.add(MaterialType.MICRO_RNA);
		
		//Storage Temperature
		List<Temperature> stLst = sc.getStorageTemperature();
		stLst.add(Temperature.CENTIGRADES_MIN_18_TO_MIN_35);
		stLst.add(Temperature.LN);
		
		List<CollectionType> ctLst = sc.getCollectionType();
		ctLst.add(CollectionType.BIRTH_COHORT);
		ctLst.add(CollectionType.LONGITUDINAL);
		
		Disease d = new Disease();
		d.setId("disease id");
		d.setOntology("ontology");
		d.setVersion("version");
		d.setCode("code");
		d.setDescription("desc");
		d.setFreeText("free text");
		
		sc.getDiseases().add(d);
		sc.getContactInformation().add(ci);
				
		sample.setSamplecollection(sc);
		
		//Study
		study = new Study();
		study.setId("my study");
		study.setName("study name");
		study.setDescription("just a study");
		
		study.getPrincipalInvestigator().add("very important PI");
		study.getContactInformation().add(ci);
		
		List<CollectionType> sDesign = study.getStudyDesign();
		sDesign.add(CollectionType.BIRTH_COHORT);
		sDesign.add(CollectionType.DISEASE_SPECIFIC);
		
		sexLst = study.getSex();
		sexLst.add(Sex.FEMALE);
		sexLst.add(Sex.MALE);
		sexLst.add(Sex.UNDIFFERENTIATED);
		
		study.setAgeLow(10);
		study.setAgeHigh(90);
		study.setAgeUnit(TimeUnit.YEAR);
		
		List<DataCategory> sDCat = study.getDataCategory();
		sDCat.add(DataCategory.GENEALOGICAL_RECORDS);
		
		List<MaterialType> sMtLst = study.getMaterialType();
		sMtLst.add(MaterialType.CELL_LINES);
		
		study.setTotalNumberOfParticipants(2147483647);
		study.setTotalNumberOfDonors(2147483647);
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		iLst.add(InclusionCriteria.AGE_GROUP);
		iLst.add(InclusionCriteria.ETHNIC_ORIGIN);
		
		sample.setStudy(study);
	}
	

}
