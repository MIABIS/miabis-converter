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
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Sex;
import org.miabis.exchange.schema.Study;
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
	
	private Disease getDisease(int startIndex, ResultSet rs) throws SQLException{
		Disease disease = new Disease();
		
		disease.setOntology(rs.getString(startIndex++));
		disease.setVersion(rs.getString(startIndex++));
		disease.setCode(rs.getString(startIndex++));
		disease.setDescription(rs.getString(startIndex++));
		disease.setFreeText(rs.getString(startIndex++));
		
		return disease;
	}
	
	private OntologyTerm getOntologyTerm(int startIndex, ResultSet rs) throws SQLException{
		OntologyTerm ontologyTerm = new OntologyTerm();
		
		ontologyTerm.setOntology(rs.getString(startIndex++));
		ontologyTerm.setVersion(rs.getString(startIndex++));
		ontologyTerm.setCode(rs.getString(startIndex++));
		ontologyTerm.setDescription(rs.getString(startIndex++));
		
		return ontologyTerm;
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
		
		sample.setContainer(rs.getString(4));
		
		//Storage Temperature
		try{
			sample.setStorageTemperature(rs.getInt(5));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Sampled Time
		try {
			sample.setSampledTime(XsdDateTimeConverter.unmarshal(rs.getString(6)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Anatomical Site
		sample.setAnatomicalSite(getOntologyTerm(7, rs));
		
		//Disease
		sample.setDisease(getDisease(11, rs));
		
		try {
			sample.setSex(Sex.fromValue(rs.getString(16)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			sample.setAgeLow(rs.getInt(17));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			sample.setAgeHigh(rs.getInt(18));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			sample.setAgeUnit(TimeUnit.fromValue(rs.getString(19)));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Biobank
		Biobank bb = new Biobank();
		bb.setId(rs.getString(20));
		bb.setAcronym(rs.getString(21));
		bb.setName(rs.getString(22));
		bb.setUrl(rs.getString(23));
		bb.setJuristicPerson(rs.getString(24));
		bb.setCountry(rs.getString(25));
		bb.setDescription(rs.getString(26));
		
		//Biobank ContactInfo
		bb.setContactInformation(getContactInformation(27, rs));
		sample.setBiobank(bb);
		
		//Study
		Study study = new Study();
		
		study.setId(rs.getString(36));
		study.setName(rs.getString(37));
		study.setDescription(rs.getString(38));
		
		List<CollectionType> sDesign = study.getStudyDesign();
		encoder.getListStream(rs.getString(39)).forEach(sd -> sDesign.add(CollectionType.fromValue(sd)));
		
		List<DataCategory> sDCat = study.getDataCategory();
		encoder.getListStream(rs.getString(40)).forEach(cat -> sDCat.add(DataCategory.fromValue(cat)));
		
		try{
			study.setTotalNumberOfParticipants(rs.getInt(41));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			study.setTotalNumberOfDonors(rs.getInt(42));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<InclusionCriteria> iLst = study.getInclusionCriteria();
		encoder.getListStream(rs.getString(43)).forEach(i -> iLst.add(InclusionCriteria.fromValue(i)));
		
		//PI
		study.setPrincipalInvestigator(rs.getString(44));
		
		//ContactInfo
		study.setContactInformation(getContactInformation(45, rs));
		
		sample.setStudy(study);
		
		//Sample Collection
		SampleCollection sc = new SampleCollection();
		sc.setId(rs.getString(54));
		sc.setAcronym(rs.getString(55));
		sc.setName(rs.getString(56));
		sc.setDescription(rs.getString(57));
		
		List<DataCategory> dCat = sc.getDataCategory();
		encoder.getListStream(rs.getString(58)).forEach(cat -> dCat.add(DataCategory.fromValue(cat)));
		
		List<CollectionType> ctLst = sc.getCollectionType();
		encoder.getListStream(rs.getString(59)).forEach(ct -> ctLst.add(CollectionType.fromValue(ct)));
		
		sc.setContactInformation(getContactInformation(60, rs));
		
		sample.setSamplecollection(sc);
		return sample;
	}

}
