package aws;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;



public class BucketInfo {

    private final AmazonS3 s3;
    private final String bucketName;
    
    
    private int numberOfFiles;
    private int totalSize;
    private double cost;
    private String region;
    private Date creationDate;
    private Date lastModified;
    private List<String> storageClasses;
    
    
	public BucketInfo(AmazonS3 s3, String bucketName, Date creationDate) {
		
		//Initialize variables
		super();
		this.s3 = s3;
		this.bucketName = bucketName;
		this.creationDate = creationDate;
		this.numberOfFiles=0;
		this.totalSize=0;
		this.cost=0;
		
		//Return bucket region
		this.region = s3.headBucket(new HeadBucketRequest(bucketName)).getBucketRegion();
		this.storageClasses = new ArrayList<String>();
		
		//Query list of objects in bucket
    	ListObjectsV2Result result = s3.listObjectsV2(bucketName);
    	List<S3ObjectSummary> objects = result.getObjectSummaries();
    	
    	//Sort objects by last modified date
    	Collections.sort(objects, new Comparator<S3ObjectSummary>() {
    	    public int compare(S3ObjectSummary o1, S3ObjectSummary o2) {
    	        return o1.getLastModified().compareTo(o2.getLastModified());
    	    }
    	});
    	
    	//get the most recent boject's last modified date
    	if(objects.size() != 0 ) {
    		this.lastModified = objects.get(0).getLastModified();
    	}
    	
    	//Get Storage calsses and calculate total size and number of files in bucket
    	for (S3ObjectSummary os : objects) {
    		if(!os.getKey().endsWith("/")) {
    			storageClasses.add(os.getStorageClass());
    			numberOfFiles++;
    			totalSize+=os.getSize();
    		}
            
        }
    	
    	//Calculate cost of bucket
    	String pattern = "yyyy-MM-dd";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	String creationDateFormated = simpleDateFormat.format(creationDate);
    	String toDayFormated = simpleDateFormat.format(new Date());
    	AwsCostExplorer awsCostExplorer = new AwsCostExplorer(creationDateFormated, toDayFormated);
    	JSONObject jsonObject = new JSONObject(awsCostExplorer.getCostAndUsageResult().toString().replace("BlendedCost=", "BlendedCost:"));
    	jsonObject.getJSONArray("ResultsByTime").forEach(p -> {
    		JSONObject period = (JSONObject) p;
    		cost = cost + (double) period.query("/Total/BlendedCost/Amount");
    	});
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public int getTotalSize() {
		return totalSize;
	}


	public Date getLastModified() {
		return lastModified;
	}

	public String getRegion() {
		return region;
	}

	public String getBucketName() {
		return bucketName;
	}

	public AmazonS3 getS3() {
		return s3;
	}

	public List<String> getStorageClasses() {
		return storageClasses;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public double getCost() {
		return cost;
	}
	
    

}
