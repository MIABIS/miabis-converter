package org.miabis.converter.transform;

import java.util.List;

import org.miabis.converter.batch.util.MiabisEncoder;
import org.miabis.converter.batch.util.Util;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.CollectionType;
import org.miabis.exchange.schema.DataCategory;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.InclusionCriteria;
import org.miabis.exchange.schema.MaterialType;
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
	
	private MiabisEncoder encoder;
	
	public SampleFieldSetMapper(){
		encoder = new MiabisEncoder();
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
		sample.setAnatomicalSite(encoder.decodeOntologyTerm(fieldSet.readString(columns[5])));
		
		//Biobank
		Biobank bb = new Biobank();
		bb.setId(fieldSet.readString(columns[6]));
		bb.setAcronym(fieldSet.readString(columns[7]));
		bb.setName(fieldSet.readString(columns[8]));
		bb.setUrl(fieldSet.readString(columns[9]));
		bb.setJuristicPerson(fieldSet.readString(columns[10]));
		
		//ContactInfo
		bb.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[11])));
		
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
		encoder.getListStream(fieldSet.readString(columns[18])).forEach(sex -> sexLst.add(Sex.fromValue(sex)));
		
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
		encoder.getListStream(fieldSet.readString(columns[22])).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		encoder.getListStream(fieldSet.readString(columns[23])).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		List<Disease> dLst = sc.getDiseases();
		encoder.getListStream(fieldSet.readString(columns[24])).forEach(d -> dLst.add(encoder.decodeDisease(d)));
		
		sc.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[25])));
		
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
		
		study.setId(fieldSet.readString(columns[26]));
		study.setName(fieldSet.readString(columns[27]));
		study.setDescription(fieldSet.readString(columns[28]));
		
		study.setPrincipalInvestigator(encoder.decodeContactInformation(fieldSet.readString(columns[29])));
		
		//ContactInfo
		study.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[30])));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		encoder.getListStream(fieldSet.readString(columns[31])).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<Sex> sSexLst = study.getSex();
		encoder.getListStream(fieldSet.readString(columns[32])).forEach(sex -> sSexLst.add(Sex.fromValue(sex)));
		
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
		encoder.getListStream(fieldSet.readString(columns[36])).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
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
		encoder.getListStream(fieldSet.readString(columns[39])).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		sample.setStudy(study);
		
		return sample;
	}
}
