package org.miabis.converter.batch.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.miabis.converter.batch.util.MiabisEncoder;
import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.CollectionType;
import org.miabis.exchange.schema.ContactInformation;
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
import org.springframework.jdbc.core.RowMapper;

public class SampleRowMapper implements RowMapper<Sample> {

	private MiabisEncoder encoder;
	
	public SampleRowMapper(){
		encoder = new MiabisEncoder();
	}
	
	private ContactInformation getContactInformation(int startIndex, ResultSet rs) throws SQLException{
		ContactInformation bbCi = new ContactInformation();
		
		bbCi.setId(rs.getString(startIndex++));
		bbCi.setFirstname(rs.getString(startIndex++));
		bbCi.setLastname(rs.getString(startIndex++));
		bbCi.setPhone(rs.getString(startIndex++));
		bbCi.setEmail(rs.getString(startIndex++));
		bbCi.setAddress(rs.getString(startIndex++));
		bbCi.setZip(rs.getString(startIndex++));
		bbCi.setCity(rs.getString(startIndex++));
		bbCi.setCountry(rs.getString(startIndex++));
		
		return bbCi;
	}
	
	@Override
	public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Sample sample = new Sample();
		
		sample.setId(rs.getString(1));
		sample.setParentSampleId(rs.getString(2));
		
		//Material Type
		try{
			sample.setMaterialType(MaterialType.fromValue(rs.getString(3)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Storage Temperature
		try{
			sample.setStorageTemperature(Temperature.fromValue(rs.getString(4)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Sampled Time
		try {
			sample.setSampledTime(XsdDateTimeConverter.unmarshal(rs.getString(5)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Anatomical Site
		sample.setAnatomicalSite(encoder.decodeOntologyTerm(rs.getString(6)));
		
		//Biobank
		Biobank bb = new Biobank();
		bb.setId(rs.getString(7));
		bb.setAcronym(rs.getString(8));
		bb.setName(rs.getString(9));
		bb.setUrl(rs.getString(10));
		bb.setJuristicPerson(rs.getString(11));
		bb.setCountry(rs.getString(12));
		bb.setDescription(rs.getString(13));
		
		//Biobank ContactInfo
		bb.setContactInformation(getContactInformation(14, rs));
		sample.setBiobank(bb);
		
		//Study
		Study study = new Study();
		
		study.setId(rs.getString(23));
		study.setName(rs.getString(24));
		study.setDescription(rs.getString(25));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		encoder.getListStream(rs.getString(26)).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<Sex> sSexLst = study.getSex();
		encoder.getListStream(rs.getString(27)).forEach(sex -> sSexLst.add(Sex.fromValue(sex)));
		
		try{
			study.setAgeLow(rs.getInt(28));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setAgeHigh(rs.getInt(29));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setAgeUnit(TimeUnit.fromValue(rs.getString(30)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> sDCat = study.getDataCategory();
		encoder.getListStream(rs.getString(31)).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		try{
			study.setTotalNumberOfParticipants(rs.getInt(32));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setTotalNumberOfDonors(rs.getInt(33));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		encoder.getListStream(rs.getString(34)).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		//ContactInfo
		study.setContactInformation(getContactInformation(35, rs));
		
		//PI
		study.setPrincipalInvestigator(getContactInformation(44, rs));
		
		sample.setStudy(study);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(rs.getString(53));
		sc.setAcronym(rs.getString(54));
		sc.setName(rs.getString(55));
		sc.setDescription(rs.getString(56));
		
		List<Sex> sexLst = sc.getSex();
		encoder.getListStream(rs.getString(57)).forEach(sex -> sexLst.add(Sex.fromValue(sex)));
		
		try{
			sc.setAgeLow(rs.getInt(58));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeHigh(rs.getInt(59));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		try{
			sc.setAgeUnit(TimeUnit.fromValue(rs.getString(60)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<DataCategory> dCat = sc.getDataCategory();
		encoder.getListStream(rs.getString(61)).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		encoder.getListStream(rs.getString(62)).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		List<Disease> dLst = sc.getDiseases();
		encoder.getListStream(rs.getString(63)).forEach(d -> dLst.add(encoder.decodeDisease(d)));
		
		sc.setContactInformation(getContactInformation(64, rs));
		
		sample.setSamplecollection(sc);
		System.out.println(sc);
		return sample;
	}

}
