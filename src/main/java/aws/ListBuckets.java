package aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glue.model.Predicate;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ListBuckets {
	
	/*
	 * Sort / filter row results
	 */
	public static ArrayList<BucketInfo> bucketInfosSort(ArrayList<BucketInfo> bucketInfos, int groupByRegion, String regexBucketName, String storageType){
		
		//groupe by region
		if(groupByRegion == 1) {
			Comparator<BucketInfo> comparator = (BucketInfo o1, BucketInfo o2) -> o1.getRegion().compareTo( o2.getRegion() );
			bucketInfos.sort(comparator);
		}
		
		//filter using regex phrase if not *
		if(!regexBucketName.equals("*")) {
			try {
				bucketInfos = (ArrayList<BucketInfo>) bucketInfos.stream().filter(b -> b.getBucketName().matches(regexBucketName)).collect(Collectors.toList());
			} catch (Exception e) {
				System.out.println("Your regex is bad, and you should feel bad!");
			}
		}
		
		//filter by class storage if "ALL" is not set
		if(!storageType.equals("ALL")) {
			bucketInfos = (ArrayList<BucketInfo>) bucketInfos.stream().filter(b -> b.getStorageClasses().contains(storageType)).collect(Collectors.toList());
		}
		
		
		return bucketInfos;
		
	}
	
	/*
	 * Printout buckets 
	 */
	public static void displayBuckets(ArrayList<BucketInfo> bucketInfos, int sizeOption, String sizeOptionString) {
		
		System.out.println("Your Amazon S3 buckets are:");
        System.out.println("Name \t\t\t\t| Creation Date \t\t\t| Number of files \t\t| Total size of files \t\t| Last modification of recent file \t\t| Cost");
        
		for(BucketInfo bucketInfo : bucketInfos) {
			System.out.print(bucketInfo.getBucketName() + "\t\t ");
			System.out.print(bucketInfo.getCreationDate() + "\t\t ");
			System.out.print(bucketInfo.getNumberOfFiles() + "\t\t ");
			System.out.print(((double) bucketInfo.getTotalSize() / sizeOption) + " " +sizeOptionString + "\t\t ");
			System.out.print(bucketInfo.getLastModified() + "\t\t ");
			System.out.print(bucketInfo.getCost() + " USA/month" + "\t\t ");
			System.out.println();
		}
	}
	
	
    public static void main(String[] args) {
    	
    	int sizeOption = -1;
    	int groupByRegioin = -1; 
    	String regexBucketName = "";
    	String sizeOptionString="";
    	String storageType="";
    	Scanner input = new Scanner(System.in);
    	ArrayList<BucketInfo> bucketInfos = new ArrayList<BucketInfo>();
    	
    	
    	
    	
    	
    	/*
    	 * Choose the option to display the content size of the bucket
    	 */
    	do {
    		System.out.print("\n1) Bytes \n2) KB \n3) MB \n4) GB \nDisplay results by: ");
    		try {
				int inputHolder = input.nextInt();
				switch(inputHolder) {
				  case 1:
					  sizeOption = 1;
					  sizeOptionString="Bytes";
				    break;
				  case 2:
					  sizeOption = 1000;
					  sizeOptionString="KB";
				    break;
				  case 3:
					  sizeOption = 1000000;
					  sizeOptionString="MB";
				    break;
				  case 4:
					  sizeOption = 1000000000;
					  sizeOptionString="GB";
				    break;
				  default:
					  throw new IllegalArgumentException();
				    
				}
			} catch (Exception e) {
				//reset values and skip over the next line 
				sizeOption = -1;
				input.nextLine();
				System.out.println("***Error, please choose a valide value\n");
			}

    	}while(sizeOption == -1);
    	
    	/*
    	 * Choose if the bucket must be grouped by region
    	 */
    	do {
    		System.out.print("\n1) No \n2)Yes \nGroup buckets by region?: ");
    		try {
				int inputHolder = input.nextInt();
				switch(inputHolder) {
				  case 1:
					  groupByRegioin = 0;
				    break;
				  case 2:
					  groupByRegioin = 1;
				    break;
				  default:
					  throw new IllegalArgumentException();
				    
				}
			} catch (Exception e) {
				//reset values and skip over the next line 
				groupByRegioin = -1;
				input.nextLine();
				System.out.println("***Error, please choose a valide value\n");
			}

    	}while(groupByRegioin == -1);
    	
    	/*
    	 * Choose to filter bucket name by using regex expression
    	 */
    	do {
    		System.out.print("\n1) No \n2)Yes \nFilter by bucket name?: ");
    		try {
				int inputHolder = input.nextInt();
				switch(inputHolder) {
				  case 1:
					  regexBucketName = "*";
				    break;
				  case 2:
					  System.out.print("Enter a valid regex expression :");
					  regexBucketName = input.next();
				    break;
				  default:
					  throw new IllegalArgumentException();
				}
			} catch (Exception e) {
				//reset values and skip over the next line 
				regexBucketName = "";
				input.nextLine();
				System.out.println("***Error, please choose a valide value\n");
			}

    	}while(regexBucketName.isEmpty());
    	
    	/*
    	 * Choose bucket with a specific storage type to be displayed 
    	 */
    	do {
    		System.out.print("\n1) ALL \n2)STANDARD \n3)STANDARD_IA \n4)INTELLIGENT_TIERING \n5)ONEZONE_IA \n6)GLACIER \n7)DEEP_ARCHIVE \n8)RRS \nSelect bucket with storage type: ");
    		try {
				int inputHolder = input.nextInt();
				switch(inputHolder) {
				  case 1:
					  storageType = "ALL";
				    break;
				  case 2:
					  storageType = "STANDARD";
					  break;
				  case 3:
					  storageType = "STANDARD_IA";
					  break;
				  case 4:
					  storageType = "INTELLIGENT_TIERING";
					  break;
				  case 5:
					  storageType = "ONEZONE_IA";
					  break;
				  case 6:
					  storageType = "GLACIER";
					  break;
				  case 7:
					  storageType = "DEEP_ARCHIVE";
					  break;
				  case 8:
					  storageType = "RRS";
				    break;
				  default:
					  throw new IllegalArgumentException();
				}
			} catch (Exception e) {
				//reset values and skip over the next line 
				storageType = "";
				input.nextLine();
				System.out.println("***Error, please choose a valide value\n");
			}

    	}while(storageType.isEmpty());
    	
    	
    	/*
    	 * Fluent builder for AmazonS3.
    	 * Using the default region of the account.
    	 * Credentials provider based on AWS configuration profiles.
    	 * Enable global bucket access.
    	 */
    	final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
    			.withRegion(Regions.DEFAULT_REGION)
    			.withCredentials(new ProfileCredentialsProvider())
    			.withForceGlobalBucketAccessEnabled(true)
    			.build();
        
    	/*
    	 * Returns a list of all buckets owned by the authenticated sender of the request.
    	 */
        List<Bucket> buckets = s3.listBuckets();

        
        /*
         * Get informations about each bucket
         */
        for (Bucket bucket : buckets) {
        	BucketInfo bucketInfo = new BucketInfo(s3, bucket.getName(), bucket.getCreationDate());
        	bucketInfos.add(bucketInfo);
        	
        }
        
        
        bucketInfos = bucketInfosSort(bucketInfos, groupByRegioin, regexBucketName, storageType);
        displayBuckets(bucketInfos, sizeOption, sizeOptionString);
        
        
        
        
    }	

}
