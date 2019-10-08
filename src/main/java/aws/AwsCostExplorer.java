package aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;

public class AwsCostExplorer {
	
	private final String startDate, endDate;

	public AwsCostExplorer(String startDate, String endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public GetCostAndUsageResult getCostAndUsageResult() {
		
		//Create client object for AWS
		final AWSCostExplorer awsCostExplorerClient = AWSCostExplorerClientBuilder.standard()
				.withRegion(Regions.DEFAULT_REGION)
				.withCredentials(new ProfileCredentialsProvider())
	            .build();
		//Query monthly Cost from AWS 
	    GetCostAndUsageRequest request = new GetCostAndUsageRequest()
	    		.withTimePeriod(new DateInterval().withStart(startDate).withEnd(endDate))
	            .withGranularity("MONTHLY")
	            .withMetrics("BlendedCost");
	    
	    GetCostAndUsageResult result = awsCostExplorerClient.getCostAndUsage(request);
	    
	    return result;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	
	
	

}
