package org.miabis.converter.transform;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.elasticsearch.common.base.Splitter;
import org.elasticsearch.common.collect.Lists;
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

	private final String DELIMITER = "\\|";
	private final String CONTACT_DELIMITER = ",";
	
	private List<String> getValueList(String str){
		List<String> values = Lists.newArrayList(Splitter.on(CONTACT_DELIMITER).trimResults().split(str));
		ListIterator<String> i = values.listIterator();
		
		while(i.hasNext()){
			String nxt = i.next();
			if (nxt.equals("")) i.set(null);
		}
		return values;
	}
	
	private ContactInformation decodeContactInformation(String str){
		
		List<String> values = getValueList(str);

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
	
	private Disease decodeDisease(String str){
		List<String> values = getValueList(str);
		
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
	
	private Stream<String> getListStream(String str){
		return Arrays.asList(str.split(DELIMITER)).stream().filter(s -> s == null || s.length() > 0);
	}
	
	@Override
	public Sample mapFieldSet(FieldSet fieldSet) throws BindException {
		
		Sample sample = new Sample();
		sample.setId(fieldSet.readString(0));
		sample.setParentSampleId(fieldSet.readString(1));
		
		//Material Type
		List<MaterialType> mtLst = sample.getMaterialType();
		getListStream(fieldSet.readString(2)).forEach(mt -> mtLst.add(MaterialType.fromValue(mt)));
		
		//Storage Temperature
		List<Temperature> stLst = sample.getStorageTemperature();
		getListStream(fieldSet.readString(3)).forEach(t -> stLst.add(Temperature.fromValue(t)));
		
		
		//Sampled Time
		try {
			sample.setSampledTime(XsdDateTimeConverter.unmarshal(fieldSet.readString(4)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Anatomical Site
		List<String> values = getValueList(fieldSet.readString(5));
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
		bb.setId(fieldSet.readString(6));
		bb.setAcronym(fieldSet.readString(7));
		bb.setName(fieldSet.readString(8));
		bb.setUrl(fieldSet.readString(9));
		
		//ContactInfo
		List<ContactInformation> bCLst = bb.getContactInformation();
		getListStream(fieldSet.readString(10)).forEach(ci -> bCLst.add(decodeContactInformation(ci)));
		
		bb.setDescription(fieldSet.readString(11));
		bb.setCountry(fieldSet.readString(12));
		sample.setBiobank(bb);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(fieldSet.readString(13));
		sc.setAcronym(fieldSet.readString(14));
		sc.setName(fieldSet.readString(15));
		sc.setDescription(fieldSet.readString(16));
		
		List<Sex> sexLst = sc.getSex();
		getListStream(fieldSet.readString(17)).forEach(sex -> sexLst.add(Sex.fromValue(sex)));
		
		
		try{
			sc.setAgeLow(fieldSet.readInt(18));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeHigh(fieldSet.readInt(19));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(20)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> dCat = sc.getDataCategory();
		getListStream(fieldSet.readString(21)).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<MaterialType> scMtLst = sc.getMaterialType();
		getListStream(fieldSet.readString(22)).forEach(mt -> scMtLst.add(MaterialType.fromValue(mt)));
		
		//Storage Temperature
		List<Temperature> stLst1 = sc.getStorageTemperature();
		getListStream(fieldSet.readString(23)).forEach(t -> stLst1.add(Temperature.fromValue(t)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		getListStream(fieldSet.readString(24)).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		List<Disease> dLst = sc.getDiseases();
		getListStream(fieldSet.readString(25)).forEach(d -> dLst.add(decodeDisease(d)));
		
		List<ContactInformation> scCLst = sc.getContactInformation();
		getListStream(fieldSet.readString(26)).forEach(ci -> scCLst.add(decodeContactInformation(ci)));
		
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
		
		study.setId(fieldSet.readString(27));
		study.setName(fieldSet.readString(28));
		study.setDescription(fieldSet.readString(29));
		study.getPrincipalInvestigator().addAll(Arrays.asList(fieldSet.readString(30).split(DELIMITER)));
		
		//ContactInfo
		List<ContactInformation> cLst = study.getContactInformation();
		getListStream(fieldSet.readString(31)).forEach(ci -> cLst.add(decodeContactInformation(ci)));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		getListStream(fieldSet.readString(32)).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<Sex> sSexLst = study.getSex();
		getListStream(fieldSet.readString(33)).forEach(sex -> sSexLst.add(Sex.fromValue(sex)));
		
		study.setAgeLow(fieldSet.readInt(34));
		study.setAgeHigh(fieldSet.readInt(35));
		
		try{
			study.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(36)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> sDCat = study.getDataCategory();
		getListStream(fieldSet.readString(37)).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		List<MaterialType> sMtLst = study.getMaterialType();
		getListStream(fieldSet.readString(38)).forEach(mt -> sMtLst.add(MaterialType.fromValue(mt)));
		
		try{
			study.setTotalNumberOfParticipants(fieldSet.readInt(39));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setTotalNumberOfDonors(fieldSet.readInt(40));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		getListStream(fieldSet.readString(41)).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		sample.setStudy(study);
		
		return sample;
	}

}
