package org.miabis.converter.batch.processors;

import static org.springframework.util.Assert.state;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.miabis.converter.transform.IndexableSample;
import org.miabis.exchange.schema.Sample;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.xml.sax.helpers.DefaultHandler;



public class SampleProcessor implements ItemProcessor<Sample, IndexableSample>, InitializingBean {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private Resource schema;
	private Marshaller marshaller;
	
	@Override
	public IndexableSample process(Sample sample) throws Exception {
		
		//DefaultHandler will discard all the events, and the marshal() operation 
		//will throw a JAXBException if validation against the schema fails.
		IndexableSample is = null;
		try{
			marshaller.marshal(sample, new DefaultHandler());
			is = new IndexableSample(sample);
		}catch(JAXBException e){
			logger.error("couldn't parse sample with id "+ sample.getId(), e);
		} 
		
		return is;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		state(schema != null, "a schema is required.");
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema s = schemaFactory.newSchema(schema.getFile()); 
		
		JAXBContext jaxbContext = JAXBContext.newInstance("org.miabis.exchange.schema");
		marshaller = jaxbContext.createMarshaller();
		marshaller.setSchema(s);	
	}

	public Resource getSchema() {
		return schema;
	}

	public void setSchema(Resource schema) {
		this.schema = schema;
	}
}
