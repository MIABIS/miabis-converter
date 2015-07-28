package org.miabis.converter.transform;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import org.elasticsearch.common.base.Splitter;
import org.elasticsearch.common.collect.Lists;
import org.miabis.converter.batch.util.Util;
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
import org.miabis.exchange.util.XsdDateTimeConverter;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class SampleFieldSetMapper implements FieldSetMapper<Sample>{
	
	private String[] columns = Util.COLUMNS.split(",");
	
	/**
	 * Splits a String around matches of <i>,</i>
	 * @param str a String
	 * @return a list of Strings
	 */
	private List<String> getTokens(String str){
		List<String> values = Lists.newArrayList(Splitter.on(Util.DELIMITER_BACKSLASH).trimResults().split(str));
		ListIterator<String> i = values.listIterator();
		
		while(i.hasNext()){
			String nxt = i.next();
			if (nxt.equals("")) i.set(null);
		}
		return values;
	}
	
	/**
	 * Decodes a formatted string and returns a <i>ContactInformation</i> object
	 * @param str
	 * @return contact information object
	 */
	private ContactInformation decodeContactInformation(String str){
		
		List<String> values = getTokens(str);

		ContactInformation ci = new ContactInformation();
		if(values.size() == 9){
			ci.setId(values.get(0));
			ci.setFirstname(values.get(1));
			ci.setLastname(values.get(2));
			ci.setPhone(values.get(3));
			ci.setEmail(values.get(4));
			ci.setAddress(values.get(5));
			ci.setZip(values.get(6));
			ci.setCity(values.get(7));
			ci.setCountry(values.get(8));
		}
		return ci;
	}
	
	/**
	 * Decodes a formatted string and returns a <i>Disease</i> object
	 * @param str
	 * @return disease object
	 */
	private Disease decodeDisease(String str){
		List<String> values = getTokens(str);
		
		Disease d = new Disease();
		if(values.size() == 6){
			d.setId(values.get(0));
			d.setOntology(values.get(1));
			d.setVersion(values.get(2));
			d.setCode(values.get(3));
			d.setDescription(values.get(4));
			d.setFreeText(values.get(5));
		}
		return d;
	}
	
	/**
	 * Splits a String around matches of <i>|</i> and filters null values
	 * @param str a String
	 * @return a string Stream
	 */
	private Stream<String> getListStream(String str){
		return Arrays.asList(str.split(Util.DELIMITER_VERTICAL_BAR)).stream().filter(s -> s == null || s.length() > 0);
	}
	
	/**
	 * Turns a fieldSet into a sample
	 * @param fieldSet
	 * @return a sample
	 */
	@Override
	public Sample mapFieldSet(FieldSet fieldSet) throws BindException {
		
		Sample sample = new Sample();
		
		sample.setId(fieldSet.readString(columns[0]));
		sample.setParentSampleId(fieldSet.readString(columns[1]));
		
		//Material Type
		try{
			sample.setMaterialType(MaterialType.fromValue(fieldSet.readString(columns[2])));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Storage Temperature
		try{
			sample.setStorageTemperature(Temperature.fromValue(fieldSet.readString(columns[3])));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Sampled Time
		try {
			sample.setSampledTime(XsdDateTimeConverter.unmarshal(fieldSet.readString(columns[4])));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Anatomical Site
		List<String> values = getTokens(fieldSet.readString(columns[5]));
		if(values.size() == 5){
			OntologyTerm aSite = new OntologyTerm();
			aSite.setId(values.get(0));
			aSite.setOntology(values.get(1));
			aSite.setVersion(values.get(2));
			aSite.setCode(values.get(3));
			aSite.setDescription(values.get(4));
			
			sample.setAnatomicalSite(aSite);
		}
		
		//Biobank
		Biobank bb = new Biobank();
		bb.setId(fieldSet.readString(columns[6]));
		bb.setAcronym(fieldSet.readString(columns[7]));
		bb.setName(fieldSet.readString(columns[8]));
		bb.setUrl(fieldSet.readString(columns[9]));
		bb.setJuristicPerson(fieldSet.readString(columns[10]));
		
		//ContactInfo
		bb.setContactInformation(decodeContactInformation(fieldSet.readString(columns[11])));
		
		bb.setDescription(fieldSet.readString(columns[12]));
		bb.setCountry(fieldSet.readString(columns[13]));
		sample.setBiobank(bb);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(fieldSet.readString(columns[14]));
		sc.setAcronym(fieldSet.readString(columns[15]));
		sc.setName(fieldSet.readString(columns[16]));
		sc.setDescription(fieldSet.readString(columns[17]));
		
		List<Sex> sexLst = sc.getSex();
		getListStream(fieldSet.readString(columns[18])).forEach(sex -> sexLst.add(Sex.fromValue(sex)));
		
		try{
			sc.setAgeLow(fieldSet.readInt(columns[19]));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeHigh(fieldSet.readInt(columns[20]));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(columns[21])));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> dCat = sc.getDataCategory();
		getListStream(fieldSet.readString(columns[22])).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		getListStream(fieldSet.readString(columns[23])).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		List<Disease> dLst = sc.getDiseases();
		getListStream(fieldSet.readString(columns[24])).forEach(d -> dLst.add(decodeDisease(d)));
		
		sc.setContactInformation(decodeContactInformation(fieldSet.readString(columns[25])));
		
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
		
		study.setId(fieldSet.readString(columns[26]));
		study.setName(fieldSet.readString(columns[27]));
		study.setDescription(fieldSet.readString(columns[28]));
		
		study.setPrincipalInvestigator(fieldSet.readString(columns[29]));
		
		//ContactInfo
		study.setContactInformation(decodeContactInformation(fieldSet.readString(columns[30])));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		getListStream(fieldSet.readString(columns[31])).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<Sex> sSexLst = study.getSex();
		getListStream(fieldSet.readString(columns[32])).forEach(sex -> sSexLst.add(Sex.fromValue(sex)));
		
		try{
			study.setAgeLow(fieldSet.readInt(columns[33]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setAgeHigh(fieldSet.readInt(columns[34]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(columns[35])));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> sDCat = study.getDataCategory();
		getListStream(fieldSet.readString(columns[36])).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		try{
			study.setTotalNumberOfParticipants(fieldSet.readInt(columns[37]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setTotalNumberOfDonors(fieldSet.readInt(columns[38]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		getListStream(fieldSet.readString(columns[39])).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		sample.setStudy(study);
		
		return sample;
	}
}
