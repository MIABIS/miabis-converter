package org.miabis.converter;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import eu.bbmri_eric.miabis.Biobank;
import eu.bbmri_eric.miabis.Miabis;

public class ExampleItemWriter implements ResourceAwareItemWriterItemStream<Biobank> {

	private Miabis miabis;
	private JAXBContext jc;
    private Marshaller marshaller;
	private Resource resource;
	
	public ExampleItemWriter() throws JAXBException {
		jc = JAXBContext.newInstance("eu.bbmri_eric.miabis");
        marshaller = jc.createMarshaller();
		
		miabis = new Miabis();
	}
	
	
	@Override
	public void write(List<? extends Biobank> items) throws Exception {
		for(Biobank e : items)
			miabis.getBiobank().add(e);
		
		
		marshaller.marshal(miabis, resource.getFile());
	}


	@Override
	public void close() throws ItemStreamException {
	}


	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		Assert.state(resource.exists(), "Output resource must exist");
	}


	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
	}


	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
