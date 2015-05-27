package org.miabis.converter.transform;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.ContactInformation;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Study;
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
		
		String cString = contact.getFirstname() + CONTACT_DELIMITER + contact.getLastname()  + CONTACT_DELIMITER + contact.getPhone()  + CONTACT_DELIMITER + contact.getEmail()
				+ CONTACT_DELIMITER + contact.getAddress() + CONTACT_DELIMITER + contact.getZip() + CONTACT_DELIMITER + contact.getCity() + CONTACT_DELIMITER + contact.getCountry();
		
		return cString;
	}
	
	/**
	 * Calls processContact and concatenates the resulting String with DELIMITER
	 * @param contactLst
	 * @return
	 */
	private String processContactList(List<ContactInformation> contactLst){
		
		List<String> cStrLst = new ArrayList<String>();
		for(ContactInformation ci : contactLst){
			cStrLst.add(processContact(ci));
		}
		
		return String.join(DELIMITER, cStrLst);
	}
	
	/**
	 * Returns a string that represents a disease. The string is delimited by CONTACT_DELIMITER
	 * @param disease
	 * @return
	 */
	private String processDisease(Disease disease){
		
		String dString = disease.getOntology() + CONTACT_DELIMITER + disease.getVersion()  + CONTACT_DELIMITER + disease.getCode()  + CONTACT_DELIMITER + disease.getDescription()
				+ CONTACT_DELIMITER + disease.getFreeText();
		
		return dString;
	}
	
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
	 * @return
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
	
	@Override
	public Object[] extract(Sample sample) {
		
		List<String> values = new ArrayList<String>();
		
		//Sample
		values.add(sample.getId());
		values.add(sample.getParentSampleId());
		
		//Material Type
		values.add(processListValues(sample.getMaterialType()));
		
		//Storage Temperature
		List<String> sTemp = sample.getStorageTemperature();
		values.add(String.join(DELIMITER, sTemp));
		
		//Sampled Time
		Calendar cal = sample.getSampledTime().toGregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(cal.getTimeZone());
		values.add(df.format(cal.getTime()));
		
		// Anatomical Site
		OntologyTerm aSite = sample.getAnatomicalSite() ;
		String aSiteStr = ""; 
		if(aSite != null) aSiteStr= aSite.getOntology() + CONTACT_DELIMITER + aSite.getVersion() + CONTACT_DELIMITER + aSite.getCode() + CONTACT_DELIMITER + aSite.getDescription();
		values.add(aSiteStr);
		
		//Biobank
		Biobank bb = sample.getBiobank();
		
		values.add(bb.getId());
		values.add(bb.getAcronym());
		values.add(bb.getName());
		values.add(bb.getUrl());
		
		values.add(processContactList(bb.getContactInformation()));
		
		values.add(bb.getDescription());
		
		//Sample Collection 
		SampleCollection sc = sample.getSamplecollection();
		
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
		values.add(String.join(DELIMITER, sc.getStorageTemperature()));
		values.add(processListValues(sc.getCollectionType()));
		values.add(processDiseaseList(sc.getDiseases()));
		
		values.add(processContactList(sc.getContactInformation()));
		
		//Study
		Study study = sample.getStudy();
		
		values.add(study.getId());
		values.add(String.join(DELIMITER, study.getPrincipalInvestigator()));
		
		values.add(processContactList(study.getContactInformation()));
		
		values.add(processListValues(study.getStudyDesign()));
		values.add(processListValues(study.getSex()));
		
		values.add(study.getAgeLow() + "");
		values.add(study.getAgeHigh() + "");
		values.add((study.getAgeUnit() != null) ? study.getAgeUnit().value() : "");
		
		values.add(processListValues(study.getDataCategory()));
		values.add(processListValues(study.getMaterialType()));
		
		values.add(study.getTotalNumberOfParticipants().toString());
		values.add(study.getTotalNumberOfDonors().toString());
		values.add(processListValues(study.getInclusionCriteria()));
		
		ListIterator<String> it = values.listIterator();
		while(it.hasNext()) {
			String nxt = it.next();
			
			if(nxt == null){
				it.set("");
			}else{
				it.set(nxt.replace("null", ""));
			}
			
		}
		
		return values.toArray();
	}

}