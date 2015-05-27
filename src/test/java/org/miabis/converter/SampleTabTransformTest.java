package org.miabis.converter;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.transform.SampleFieldExtractor;
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
	
	@Autowired
	private DelimitedLineAggregator<Sample> aggregator;
	
	@Autowired
	private DefaultLineMapper<Sample> mapper;
	
	@Test
	public void testMapperAndExtractor() throws Exception{
		
		String sampleStr = aggregator.aggregate(sample);
		
		Sample sample1 = mapper.mapLine(sampleStr, 0);
		
		
		System.out.println(sample.toString());
		System.out.println(sample1.toString());
		
		//System.out.println(sampleStr);
		//String e = extractor.extract(sample);
		
		
	}
	
	//Create a sample
	@BeforeClass 
	public static void prepare() throws DatatypeConfigurationException{
		
		sample = new Sample();
		sample.setId("mySample");
		sample.setParentSampleId("parentSample");
		sample.setSampledTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		sample.getMaterialType().add(MaterialType.C_DNA_M_RNA);
		sample.getStorageTemperature().add("Storage tmp");
		
		OntologyTerm as = new OntologyTerm();
		as.setOntology("ontology");
		as.setVersion("version");
		as.setCode("code");
		as.setDescription("desc");
		
		sample.setAnatomicalSite(as);
		
		Biobank bb = new Biobank();
		bb.setId("my bb");
		bb.setAcronym("acronym");
		bb.setName("name");
		bb.setUrl("https://github.com/MIABIS");
		bb.setDescription("A biobank description");
		
		ContactInformation ci = new ContactInformation();
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
		SampleCollection sc = new SampleCollection();
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
		List<String> stLst = sc.getStorageTemperature();
		stLst.add("temp1");
		stLst.add("temp2");
		
		List<CollectionType> ctLst = sc.getCollectionType();
		ctLst.add(CollectionType.BIRTH_COHORT);
		ctLst.add(CollectionType.LONGITUDINAL);
		
		Disease d = new Disease();
		d.setOntology("ontology");
		d.setVersion("version");
		d.setCode("code");
		d.setDescription("desc");
		d.setFreeText("free text");
		
		sc.getDiseases().add(d);
		sc.getContactInformation().add(ci);
				
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
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
		
		study.setTotalNumberOfParticipants(new BigInteger("3213213454654654231321"));
		study.setTotalNumberOfDonors(new BigInteger("3213213454654654231321"));
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		iLst.add(InclusionCriteria.AGE_GROUP);
		iLst.add(InclusionCriteria.ETHNIC_ORIGIN);
		
		sample.setStudy(study);
	}
	

}