package org.miabis.converter.transform;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.elasticsearch.common.base.Joiner;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Study;
import org.miabis.exchange.util.XsdDateTimeConverter;
import org.springframework.batch.item.file.transform.FieldExtractor;

public class SampleFieldExtractor implements FieldExtractor<Sample> {
	
	private final String DELIMITER = "|";
	private final String CONTACT_DELIMITER = ",";
	
	/**
	 * Returns a string that represents a contact. The string is delimited by CONTACT_DELIMITER
	 * @param contact
	 * @return
	 */
	private String processContact(ContactInformation contact){
		
		List<String> contactLst = Arrays.asList(contact.getId(), contact.getFirstname(), contact.getLastname(), 
		                                  contact.getPhone(), contact.getEmail(), contact.getAddress(), 
		                                  contact.getZip(), contact.getCity() , contact.getCountry());
		
		return Joiner.on(CONTACT_DELIMITER).useForNull("").join(contactLst);
	}
	
	/**
	 * Calls processContact and concatenates the resulting String with DELIMITER
	 * @param contactLst
	 * @return a String representing a list of contacts
	 */
	private String processContactList(List<ContactInformation> contactLst){
		
		List<String> cStrLst = new ArrayList<String>();
		for(ContactInformation ci : contactLst){
			cStrLst.add(processContact(ci));
		}
		
		return String.join(DELIMITER, cStrLst);
	}
	
	/**
	 * Returns a String that represents a disease. The String is delimited by CONTACT_DELIMITER
	 * @param disease
	 * @return	a String representing a disease
	 */
	private String processDisease(Disease disease){
		
		List<String> diseaseLst = Arrays.asList(disease.getId(), disease.getOntology(), disease.getVersion(), 
				disease.getCode(), disease.getDescription(), disease.getFreeText());

		return Joiner.on(CONTACT_DELIMITER).useForNull("").join(diseaseLst);
	}
	
	/**
	 * Returns a String that represents a list of diseases by calling <i>processDisease</i>
	 * @param diseaseLst a list of diseases
	 * @return a String representing a list of diseases
	 */
	private String processDiseaseList(List<Disease> diseaseLst){
		List<String> dStrLst = new ArrayList<String>();
		for(Disease d : diseaseLst){
			dStrLst.add(processDisease(d));
		}
		
		return String.join(DELIMITER, dStrLst);
	}
	
	/**
	 * Extracts the value of each object in the list and returns a String of values concatenated by DELIMITER. 
	 * @param lst
	 * @return a String representing a list of objects
	 */
	private String processListValues(List<?> lst){
		
		List<String> values = new ArrayList<String>();
		
		try{
			if(lst.size()>0){
				Method  method = lst.get(0).getClass().getDeclaredMethod ("value", new Class[0]);
				for(Object obj : lst){
					String val = (String) method.invoke(obj, new Object[0]);
					values.add(val);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return String.join(DELIMITER, values);
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
		values.add(processListValues(sample.getMaterialType()));
		
		//Storage Temperature
		values.add(processListValues(sample.getStorageTemperature()));
		
		//Sampled Time
		String sTime = "";
		if(sample.getSampledTime() != null){
			sTime = XsdDateTimeConverter.marshalDate(sample.getSampledTime());
		}
		values.add(sTime);
		
		// Anatomical Site
		OntologyTerm aSite = sample.getAnatomicalSite() ;
		String aSiteStr = ""; 
		if(aSite != null) aSiteStr= aSite.getId() + CONTACT_DELIMITER + aSite.getOntology() + CONTACT_DELIMITER + aSite.getVersion() + CONTACT_DELIMITER + aSite.getCode() + CONTACT_DELIMITER + aSite.getDescription();
		values.add(aSiteStr);
		
		//Biobank
		Biobank bb = (sample.getBiobank() != null) ? sample.getBiobank() : new Biobank();
		
		values.add(bb.getId());
		values.add(bb.getAcronym());
		values.add(bb.getName());
		values.add(bb.getUrl());
			
		values.add(processContactList(bb.getContactInformation()));
			
		values.add(bb.getDescription());
		values.add(bb.getCountry());
		
		//Sample Collection 
		SampleCollection sc = (sample.getSamplecollection() != null) ? sample.getSamplecollection() : new SampleCollection();
		
		values.add(sc.getId());
		values.add(sc.getAcronym());
		values.add(sc.getName());
		values.add(sc.getDescription());
			
		values.add(processListValues(sc.getSex()));
			
		values.add(sc.getAgeLow() + "");
		values.add(sc.getAgeHigh() + "");
		values.add((sc.getAgeUnit() != null) ? sc.getAgeUnit().value() : "");
			
		values.add(processListValues(sc.getDataCategory()));
		values.add(processListValues(sc.getMaterialType()));
		values.add(processListValues(sc.getStorageTemperature()));
		values.add(processListValues(sc.getCollectionType()));
		values.add(processDiseaseList(sc.getDiseases()));
			
		values.add(processContactList(sc.getContactInformation()));

		//Study
		Study study = (sample.getStudy() != null) ? sample.getStudy() : new Study();
		
		values.add(study.getId());
		values.add(study.getName());
		values.add(study.getDescription());
		values.add(String.join(DELIMITER, study.getPrincipalInvestigator()));
		
		values.add(processContactList(study.getContactInformation()));
		
		values.add(processListValues(study.getStudyDesign()));
		values.add(processListValues(study.getSex()));
		
		values.add(study.getAgeLow() + "");
		values.add(study.getAgeHigh() + "");
		values.add((study.getAgeUnit() != null) ? study.getAgeUnit().value() : "");
		
		values.add(processListValues(study.getDataCategory()));
		values.add(processListValues(study.getMaterialType()));
		
		
		values.add(study.getTotalNumberOfParticipants() + "");
		values.add(study.getTotalNumberOfDonors() + "");
		values.add(processListValues(study.getInclusionCriteria()));
		
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
