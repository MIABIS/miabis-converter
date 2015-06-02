package org.miabis.converter.transform;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Study;
import org.miabis.exchange.schema.Temperature;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "sample")
public class IndexableSample{
	
	protected String id;
    protected String parentSampleId;
    
    @Field(type = FieldType.Nested)
    protected List<MaterialType> materialType;
    protected List<Temperature> storageTemperature;
    protected LocalDateTime sampledTime;
    protected OntologyTerm anatomicalSite;
    
    @Field(type = FieldType.Nested)
    protected Biobank biobank;
    
    @Field(type = FieldType.Nested)
    protected SampleCollection samplecollection;
    
    @Field(type = FieldType.Nested)
    protected Study study;
	
    public IndexableSample(){}
    
    public IndexableSample(Sample sample){
    	
    	this.id = sample.getId();
    	this.parentSampleId = sample.getParentSampleId();
    	
    	this.materialType = sample.getMaterialType();
    	this.storageTemperature = sample.getStorageTemperature();
        this.sampledTime = sample.getSampledTime();
        this.anatomicalSite = sample.getAnatomicalSite();
        
        this.biobank = sample.getBiobank();
        this.samplecollection = sample.getSamplecollection();
        this.study = sample.getStudy();
    	
    }
    
    public String getId() {
		return id;
	}
	
    public void setId(String id) {
		this.id = id;
	}
	
    public String getParentSampleId() {
		return parentSampleId;
	}
	
    public void setParentSampleId(String parentSampleId) {
		this.parentSampleId = parentSampleId;
	}
	
    public List<MaterialType> getMaterialType() {
		return materialType;
	}
	
    public void setMaterialType(List<MaterialType> materialType) {
		this.materialType = materialType;
	}
	
    public List<Temperature> getStorageTemperature() {
		return storageTemperature;
	}
	
    public void setStorageTemperature(List<Temperature> storageTemperature) {
		this.storageTemperature = storageTemperature;
	}
	
    public LocalDateTime getSampledTime() {
		return sampledTime;
	}
	
    public void setSampledTime(LocalDateTime sampledTime) {
		this.sampledTime = sampledTime;
	}
	
    public OntologyTerm getAnatomicalSite() {
		return anatomicalSite;
	}
	
    public void setAnatomicalSite(OntologyTerm anatomicalSite) {
		this.anatomicalSite = anatomicalSite;
	}
	
    public Biobank getBiobank() {
		return biobank;
	}
	
    public void setBiobank(Biobank biobank) {
		this.biobank = biobank;
	}
	
    public SampleCollection getSamplecollection() {
		return samplecollection;
	}
	
    public void setSamplecollection(SampleCollection samplecollection) {
		this.samplecollection = samplecollection;
	}
	
    public Study getStudy() {
		return study;
	}
	
    public void setStudy(Study study) {
		this.study = study;
	}
}
