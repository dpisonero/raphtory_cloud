import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Upload {
    public static void uploadObject(
            String projectId, String bucketName, String objectName, String filePath) throws IOException {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";


        // The path to your file to upload
        // String filePath = "path/to/your/file"

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

        System.out.println(
                "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
    }

    public static void downloadObject(

            String projectId, String bucketName, String objectName, String destFilePath) {
            // The ID of your GCP project
            // String projectId = "your-project-id";

            // The ID of your GCS bucket
            // String bucketName = "your-unique-bucket-name";

            // The ID of your GCS object
            // String objectName = "your-object-name";

            // The path to which the file should be downloaded
            // String destFilePath = "/local/path/to/file.txt";

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

            Blob blob = storage.get(BlobId.of(bucketName, objectName));
            blob.downloadTo(Paths.get(destFilePath));

            System.out.println(
                    "Downloaded object "
                            + objectName
                            + " from bucket name "
                            + bucketName
                            + " to "
                            + destFilePath);
    }

    public static void main(String[] args) throws IOException {

        // Instantiates a client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // The name for the new bucket
        String bucketName = "first-bucket-test-raphtory-mine";

        // Creates the new bucket
        try {
            Bucket bucket = storage.create(BucketInfo.of(bucketName));
            System.out.printf("Bucket %s created.%n", bucket.getName());
        }
        catch (com.google.cloud.storage.StorageException SE) {
            System.out.printf("Bucket already created. The name is %s.%n", bucketName);
        }

        //uploadObject("ardent-quarter-347510", bucketName, "Test", "C:\\Users\\david\\Downloads\\FolderTest\\Test.txt");
        //downloadObject("flink-test-331515", "flink_test_upload", "lotr.csv", "C:\\Users\\david\\Downloads\\lotr.csv");

        // Code to read a file stored in a Cloud Bucket, line by line
        Credentials credentials = GoogleCredentials.fromStream(new FileInputStream("key.json"));
        StorageOptions options = StorageOptions.newBuilder()
                .setProjectId("ardent-quarter-347510").setCredentials(credentials).build();
        Blob blob = storage.get(bucketName, "Test");
        ReadChannel readChannel = blob.reader();
        BufferedReader br = new BufferedReader(Channels.newReader(readChannel, StandardCharsets.UTF_8));
        String strCurrentLine;
        while((strCurrentLine = br.readLine()) != null) {
            System.out.println(strCurrentLine);
        }
    }
}
