package org.miabis.converter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.transform.IndexableSample;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/spring/batch/config/config.xml", "/spring/data/config/elastic-search-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ElasticSearchTest {
	
	@Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
	
	private static Sample sample;

	@Test
	public void indexSample(){
		
		List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
		
		IndexableSample iSample = new IndexableSample(sample);
		IndexQuery indexQuery1 = new IndexQueryBuilder().withId(iSample.getId()).withObject(iSample).build();
        indexQueries.add(indexQuery1);
		
        //bulk index
        elasticsearchTemplate.bulkIndex(indexQueries);
	}
	
	@Test
	public void deleteIndex(){
		elasticsearchTemplate.deleteIndex(IndexableSample.class);
	}
	
	
	@BeforeClass 
	public static void prepare() throws DatatypeConfigurationException{
		
		sample = new Sample();
		sample.setId("mySample");
		sample.setParentSampleId("parentSample");
		sample.setSampledTime(LocalDateTime.now());
		sample.setMaterialType(MaterialType.C_DNA_M_RNA);
		sample.setStorageTemperature(Temperature.CENTIGRADES_2_TO_10);
		
		OntologyTerm as = new OntologyTerm();
		as.setId("id");
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
		
		study.setTotalNumberOfParticipants(2147483647);
		study.setTotalNumberOfDonors(2147483647);
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		iLst.add(InclusionCriteria.AGE_GROUP);
		iLst.add(InclusionCriteria.ETHNIC_ORIGIN);
		
		sample.setStudy(study);
	}
	
	

}
