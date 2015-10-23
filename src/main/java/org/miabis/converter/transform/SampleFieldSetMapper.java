package org.miabis.converter.transform;

import java.util.List;

import org.miabis.converter.batch.util.MiabisEncoder;
import org.miabis.converter.batch.util.Util;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.CollectionType;
import org.miabis.exchange.schema.DataCategory;
import org.miabis.exchange.schema.InclusionCriteria;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Sex;
import org.miabis.exchange.schema.Study;
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
			sample.setStorageTemperature(fieldSet.readInt(columns[3]));
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
		
		// Disease
		sample.setDisease(encoder.decodeDisease(fieldSet.readString(columns[6])));
		
		//Sex
		try{
			sample.setSex(Sex.fromValue(fieldSet.readString(columns[7])));
		}catch(Exception e){
			e.printStackTrace();
		}
				
		//Age
		try{
			sample.setAgeLow(fieldSet.readInt(columns[8]));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sample.setAgeHigh(fieldSet.readInt(columns[9]));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sample.setAgeUnit(TimeUnit.fromValue(fieldSet.readString(columns[10])));
		}catch(Exception e){
			e.printStackTrace();
		}
				
		//Container
		sample.setContainer(fieldSet.readString(columns[11]));
		
		//Biobank
		Biobank bb = new Biobank();
		bb.setId(fieldSet.readString(columns[12]));
		bb.setAcronym(fieldSet.readString(columns[13]));
		bb.setName(fieldSet.readString(columns[14]));
		bb.setUrl(fieldSet.readString(columns[15]));
		bb.setJuristicPerson(fieldSet.readString(columns[16]));
		
		//ContactInfo
		bb.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[17])));
		
		bb.setDescription(fieldSet.readString(columns[18]));
		bb.setCountry(fieldSet.readString(columns[19]));
		sample.setBiobank(bb);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(fieldSet.readString(columns[20]));
		sc.setAcronym(fieldSet.readString(columns[21]));
		sc.setName(fieldSet.readString(columns[22]));
		sc.setDescription(fieldSet.readString(columns[23]));
		
		List<DataCategory> dCat = sc.getDataCategory();
		encoder.getListStream(fieldSet.readString(columns[24])).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		encoder.getListStream(fieldSet.readString(columns[25])).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		sc.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[26])));
		
		sample.setSamplecollection(sc);
		
		//Study
		Study study = new Study();
		
		study.setId(fieldSet.readString(columns[27]));
		study.setName(fieldSet.readString(columns[28]));
		study.setDescription(fieldSet.readString(columns[29]));
		
		study.setPrincipalInvestigator(fieldSet.readString(columns[30]));
		
		//ContactInfo
		study.setContactInformation(encoder.decodeContactInformation(fieldSet.readString(columns[31])));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		encoder.getListStream(fieldSet.readString(columns[32])).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<DataCategory> sDCat = study.getDataCategory();
		encoder.getListStream(fieldSet.readString(columns[33])).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		try{
			study.setTotalNumberOfParticipants(fieldSet.readInt(columns[34]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setTotalNumberOfDonors(fieldSet.readInt(columns[35]));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		encoder.getListStream(fieldSet.readString(columns[36])).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		sample.setStudy(study);
		
		return sample;
	}
}
