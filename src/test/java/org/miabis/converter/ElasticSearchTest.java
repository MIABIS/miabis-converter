package org.miabis.converter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miabis.converter.transform.IndexableSample;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
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
	
	private static IndexableSample sample;

	@Test
	public void indexSample(){
		
		List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
		
		IndexQuery indexQuery1 = new IndexQueryBuilder().withId(sample.getId()).withObject(sample).build();
        indexQueries.add(indexQuery1);
		
        //bulk index
        elasticsearchTemplate.bulkIndex(indexQueries);
	}
	
	
	//Create a sample
	@BeforeClass 
	public static void prepare() throws DatatypeConfigurationException{
			
		sample = new IndexableSample();
		sample.setId("mySample");
		sample.setParentSampleId("parentSample");
		sample.setSampledTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
	}
	
	

}
