package org.miabis.converter.transform;

import java.time.LocalDateTime;

import org.miabis.exchange.schema.Biobank;
import org.miabis.exchange.schema.Disease;
import org.miabis.exchange.schema.MaterialType;
import org.miabis.exchange.schema.OntologyTerm;
import org.miabis.exchange.schema.Sample;
import org.miabis.exchange.schema.SampleCollection;
import org.miabis.exchange.schema.Sex;
import org.miabis.exchange.schema.Study;
import org.miabis.exchange.schema.TimeUnit;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Annotated version of the sample class so it can be indexed by elastic search.
 * @author jvillaveces
 */
@Document(indexName = "sample")
public class IndexableSample{
	
	protected String id;
    protected String parentSampleId;
    protected String container;
    
    @Field(type = FieldType.Nested)
    protected MaterialType materialType;
    protected int storageTemperature;
    protected LocalDateTime sampledTime;
    
    @Field(type = FieldType.Nested)
    protected OntologyTerm anatomicalSite;
    
    @Field(type = FieldType.Nested)
    protected Disease disease;
    protected Sex sex;
   
    protected int ageLow;
    protected int ageHigh;
    protected TimeUnit ageUnit;
    
    
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
    	
    	if(sample.getStorageTemperature() != null){
    		this.storageTemperature = sample.getStorageTemperature();
    	}
        
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
	
    public MaterialType getMaterialType() {
		return materialType;
	}
	
    public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}
	
    public int getStorageTemperature() {
		return storageTemperature;
	}
	
    public void setStorageTemperature(int storageTemperature) {
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
