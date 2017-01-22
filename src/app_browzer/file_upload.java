/**
 *
 * @author Pawan
 * This java file uploads a file to AWS S3 Bucket. 
 * Requirements: AWS Bucket Name,AWS Access Key ID,AWS Secret Key
 * Optional:AWS Region
 */
package app_browzer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class file_upload {
    private static final String SUFFIX = "/";
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
 
        try {
 
            Object obj = parser.parse(new FileReader(
                    "D:\\config.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            /**
             * Get bucket name,secret etc from config.json file
             */
            String aws_bucket_name=(String) jsonObject.get("AWS Bucket Name");
            String aws_access_key=(String) jsonObject.get("AWS Access Key ID");
            String aws_secret_key=(String) jsonObject.get("AWS Secret Key");
            String aws_region_code=(String) jsonObject.get("AWS Region");
         
          //  System.out.println(aws_bucket_name+" "+aws_access_key+" "+aws_secret_key+" "+aws_region_code);
            
            // credentials object identifying user for authentication
		
		AWSCredentials credentials = new BasicAWSCredentials(
				aws_access_key, 
				aws_secret_key);
		
		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);
		
		// create bucket - name must be unique for all S3 users
		String bucketName = aws_bucket_name;
		s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) {
			System.out.println(" - " + bucket.getName());
		}
		
		// create folder into bucket
		String folderName = "<YOUR FOLDER NAME>";//Your folder name
		createFolder(bucketName, folderName, s3client);
		
		// upload file to folder and set it to public
		String fileName = folderName + SUFFIX + "<YOUR_FILENAME>";
		s3client.putObject(new PutObjectRequest(bucketName, fileName, 
				new File("<YOUR_LOCAL_FILE_PATH>"))
				.withCannedAcl(CannedAccessControlList.PublicRead));
		
               
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);

		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	
}
