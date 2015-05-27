package org.miabis.converter.transform;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
import org.miabis.exchange.schema.TimeUnit;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class SampleFieldSetMapper implements FieldSetMapper<Sample>{

	private final String DELIMITER = "\\|";
	private final String CONTACT_DELIMITER = ",";
	private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	
	private XMLGregorianCalendar toXMLGregorianCalendar(String str) throws ParseException, DatatypeConfigurationException{
		Date date = DATE_FORMAT.parse(str);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), date.getHours(), date.getMinutes(), date.getSeconds(), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
		return xmlDate;
	}
	
	private ContactInformation decodeContactInformation(String str){
		String[] values = str.split(CONTACT_DELIMITER);
		
		ContactInformation ci = new ContactInformation();
		ci.setFirstname(values[0]);
		ci.setLastname(values[1]);
		ci.setPhone(values[2]);
		ci.setEmail(values[3]);
		ci.setAddress(values[4]);
		ci.setZip(values[5]);
		ci.setCity(values[6]);
		ci.setCountry(values[7]);
		
		return ci;
	}
	
	private Disease decodeDisease(String str){
		String[] values = str.split(CONTACT_DELIMITER);
		
		Disease d = new Disease();
		d.setOntology(values[0]);
		d.setVersion(values[1]);
		d.setCode(values[2]);
		d.setDescription(values[3]);
		d.setFreeText(values[4]);
		
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
		List<String> stLst = sample.getStorageTemperature();
		stLst.addAll(Arrays.asList(fieldSet.readString(3).split(DELIMITER)));
		
		//Sampled Time
		try {
			sample.setSampledTime(toXMLGregorianCalendar(fieldSet.readString(4)));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		
		// Anatomical Site
		String[] values = fieldSet.readString(5).split(CONTACT_DELIMITER);
		if(values.length == 4){
			OntologyTerm aSite = new OntologyTerm();
			aSite.setOntology(values[0]);
			aSite.setVersion(values[1]);
			aSite.setCode(values[2]);
			aSite.setDescription(values[3]);
			
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
		sample.setBiobank(bb);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(fieldSet.readString(12));
		sc.setAcronym(fieldSet.readString(13));
		sc.setName(fieldSet.readString(14));
		sc.setDescription(fieldSet.readString(15));
		
		List<Sex> sexLst = sc.getSex();
		getListStream(fieldSet.readString(16)).forEach(sex -> sexLst.add(Sex.fromValue(sex)));
		
		sc.setAgeLow(fieldSet.readInt(17));
		sc.setAgeHigh(fieldSet.readInt(18));
		sc.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(19)));
		
		List<DataCategory> dCat = sc.getDataCategory();
		getListStream(fieldSet.readString(20)).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<MaterialType> scMtLst = sc.getMaterialType();
		getListStream(fieldSet.readString(21)).forEach(mt -> scMtLst.add(MaterialType.fromValue(mt)));
		
		//Storage Temperature
		stLst = sc.getStorageTemperature();
		stLst.addAll(Arrays.asList(fieldSet.readString(22).split(DELIMITER)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		getListStream(fieldSet.readString(23)).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		List<Disease> dLst = sc.getDiseases();
		getListStream(fieldSet.readString(24)).forEach(d -> dLst.add(decodeDisease(d)));
		
		List<ContactInformation> scCLst = sc.getContactInformation();
		getListStream(fieldSet.readString(25)).forEach(ci -> scCLst.add(decodeContactInformation(ci)));
		
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
		
		study.setId(fieldSet.readString(26));
		study.getPrincipalInvestigator().addAll(Arrays.asList(fieldSet.readString(27).split(DELIMITER)));
		
		//ContactInfo
		List<ContactInformation> cLst = study.getContactInformation();
		getListStream(fieldSet.readString(28)).forEach(ci -> cLst.add(decodeContactInformation(ci)));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		getListStream(fieldSet.readString(29)).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<Sex> sSexLst = study.getSex();
		getListStream(fieldSet.readString(30)).forEach(sex -> sSexLst.add(Sex.fromValue(sex)));
		
		study.setAgeLow(fieldSet.readInt(31));
		study.setAgeHigh(fieldSet.readInt(32));
		study.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(33)));
		
		List<DataCategory> sDCat = study.getDataCategory();
		getListStream(fieldSet.readString(34)).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		List<MaterialType> sMtLst = study.getMaterialType();
		getListStream(fieldSet.readString(35)).forEach(mt -> sMtLst.add(MaterialType.fromValue(mt)));
		
		study.setTotalNumberOfParticipants(new BigInteger(fieldSet.readString(36)));
		study.setTotalNumberOfDonors(new BigInteger(fieldSet.readString(37)));
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		getListStream(fieldSet.readString(38)).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		sample.setStudy(study);
		
		return sample;
	}

}
