package org.miabis.converter;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.miabis.converter.batch.util.MiabisEncoder;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.OntologyTerm;

public class MiabisEncoderTest {
	
	private MiabisEncoder encoder;
	
	@Test
	public void testDisease() throws Exception {
		
		Assert.assertNull(encoder.decodeDisease(""));
		Assert.assertEquals(encoder.encodeDisease(null), "");
		
		Disease d = new Disease();
		Assert.assertEquals("\\\\\\\\\\", encoder.encodeDisease(d));
		Assert.assertEquals(d, encoder.decodeDisease("\\\\\\\\\\"));
		
		d.setCode("code");
		d.setDescription("desc");
		d.setOntology("ontology");
		
		Assert.assertEquals("\\ontology\\\\code\\desc\\", encoder.encodeDisease(d));
		Assert.assertEquals(d, encoder.decodeDisease("\\ontology\\\\code\\desc\\"));
	}
	
	@Test
	public void testOntologyTerm() throws Exception {
		
		Assert.assertNull(encoder.decodeOntologyTerm(""));
		Assert.assertEquals(encoder.encodeOntologyTerm(null), "");
		
		OntologyTerm term = new OntologyTerm();
		Assert.assertEquals("\\\\\\\\", encoder.encodeOntologyTerm(term));
		Assert.assertEquals(term, encoder.decodeOntologyTerm("\\\\\\\\"));
		
		term.setCode("code");
		term.setDescription("desc");
		term.setOntology("ontology");
		
		Assert.assertEquals("\\ontology\\\\code\\desc", encoder.encodeOntologyTerm(term));
		Assert.assertEquals(term, encoder.decodeOntologyTerm("\\ontology\\\\code\\desc"));
	}
	
	@Test
	public void testContactInfo() throws Exception {
		
		Assert.assertNull(encoder.decodeContactInformation(""));
		Assert.assertEquals("", encoder.encodeContactInformation(null));
		
		ContactInformation ci = new ContactInformation();
		Assert.assertEquals("\\\\\\\\\\\\\\\\", encoder.encodeContactInformation(ci));
		Assert.assertEquals(ci, encoder.decodeContactInformation("\\\\\\\\\\\\\\\\"));
		
		ci.setAddress("add");
		ci.setCity("city");
		ci.setCountry("country");
		ci.setFirstname("name");
		
		Assert.assertEquals("\\name\\\\\\\\add\\\\city\\country", encoder.encodeContactInformation(ci));
		Assert.assertEquals(ci, encoder.decodeContactInformation("\\name\\\\\\\\add\\\\city\\country"));
	}
	
	@Before 
	public void prepare() throws DatatypeConfigurationException{
		encoder = new MiabisEncoder();
	}
	
}
