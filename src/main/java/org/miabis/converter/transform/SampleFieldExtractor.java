package org.miabis.converter.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.miabis.converter.batch.util.MiabisEncoder;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Study;
import org.miabis.exchange.util.XsdDateTimeConverter;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class SampleFieldExtractor implements FieldExtractor<Sample> {
	
	private MiabisEncoder encoder;
	
	public SampleFieldExtractor(){
		encoder = new MiabisEncoder();
	}
	
	/**
	 * Turns a sample into an array of strings
	 * @param sample
	 * @return an array of strings representing a Sample
	 */
	@Override
	public Object[] extract(Sample sample) {
		
		List<String> values = new ArrayList<String>();
		
		//Sample
		values.add(sample.getId());
		values.add(sample.getParentSampleId());
		
		//Material Type
		String mt = (sample.getMaterialType() != null) ? sample.getMaterialType().value() : "";
		values.add(mt);
		
		//Storage Temperature
		String t = (sample.getStorageTemperature() != null) ? sample.getStorageTemperature().value() : "";
		values.add(t);
		
		//Sampled Time
		String sTime = "";
		if(sample.getSampledTime() != null){
			sTime = XsdDateTimeConverter.marshalDateTime(sample.getSampledTime());
		}
		values.add(sTime);
		
		// Anatomical Site
		OntologyTerm aSite = sample.getAnatomicalSite() ;
		values.add(encoder.encodeOntologyTerm(aSite));
		
		//Biobank
		Biobank bb = (sample.getBiobank() != null) ? sample.getBiobank() : new Biobank();
		
		values.add(bb.getId());
		values.add(bb.getAcronym());
		values.add(bb.getName());
		values.add(bb.getUrl());
		
		String jp = (bb.getJuristicPerson() != null) ? bb.getJuristicPerson() : "";
		values.add(jp);
		
		values.add(encoder.encodeContact(bb.getContactInformation()));
			
		values.add(bb.getDescription());
		values.add(bb.getCountry());
		
		//Sample Collection 
		SampleCollection sc = (sample.getSamplecollection() != null) ? sample.getSamplecollection() : new SampleCollection();
		
		values.add(sc.getId());
		values.add(sc.getAcronym());
		values.add(sc.getName());
		values.add(sc.getDescription());
			
		values.add(encoder.encodeValues(sc.getSex()));
			
		values.add(sc.getAgeLow() + "");
		values.add(sc.getAgeHigh() + "");
		values.add((sc.getAgeUnit() != null) ? sc.getAgeUnit().value() : "");
			
		values.add(encoder.encodeValues(sc.getDataCategory()));
		values.add(encoder.encodeValues(sc.getCollectionType()));
		values.add(encoder.encodeDisease(sc.getDiseases()));
			
		values.add(encoder.encodeContact(sc.getContactInformation()));

		//Study
		Study study = (sample.getStudy() != null) ? sample.getStudy() : new Study();
		
		values.add(study.getId());
		values.add(study.getName());
		values.add(study.getDescription());
		values.add(encoder.encodeContact(study.getPrincipalInvestigator()));
		
		values.add(encoder.encodeContact(study.getContactInformation()));
		
		values.add(encoder.encodeValues(study.getStudyDesign()));
		values.add(encoder.encodeValues(study.getSex()));
		
		values.add(study.getAgeLow() + "");
		values.add(study.getAgeHigh() + "");
		values.add((study.getAgeUnit() != null) ? study.getAgeUnit().value() : "");
		
		values.add(encoder.encodeValues(study.getDataCategory()));
		
		
		values.add(study.getTotalNumberOfParticipants() + "");
		values.add(study.getTotalNumberOfDonors() + "");
		values.add(encoder.encodeValues(study.getInclusionCriteria()));
		
		ListIterator<String> it = values.listIterator();
		while(it.hasNext()) {
			String nxt = it.next();
			
			if(nxt == null){
				it.set("");
			}
		}
		return values.toArray();
	}
}
