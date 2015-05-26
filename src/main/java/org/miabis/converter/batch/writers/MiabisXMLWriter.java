package org.miabis.converter.batch.writers;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.Miabis;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class MiabisXMLWriter implements ResourceAwareItemWriterItemStream<Biobank> {

	private Miabis miabis;
	private JAXBContext jc;
    private Marshaller marshaller;
	private Resource resource;
	
	public MiabisXMLWriter() throws JAXBException {
		jc = JAXBContext.newInstance("eu.bbmri_eric.miabis");
        marshaller = jc.createMarshaller();
		
		miabis = new Miabis();
	}
	
	
	@Override
	public void write(List<? extends Biobank> items) throws Exception {
		
	}


	@Override
	public void close() throws ItemStreamException {
	}


	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		
			try {
				if(!resource.exists()) resource.getFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		//Assert.state(resource.exists(), "Output resource must exist");
	}


	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
	}


	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
