package com.example.filestore;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.example.metrics.AzureDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class AzureSoftwareFileStorage
implements ISoftwareFileStorage
{
	private static final Logger logger = LoggerFactory.getLogger(AzureSoftwareFileStorage.class);

	private final String storageLogin;
	private final String storagePassword;
	private final String storageContainer;

	/** Optional so integration tests won't require a client; don't want to send telemetry in tests */
	private TelemetryClient telemetryClient;


	@Autowired
	public AzureSoftwareFileStorage(
			@Value("${storage.files.cloud.login}") String storageLogin,
			@Value("${storage.files.cloud.pass}") String storagePassword,
			@Value("${storage.files.cloud.public.container}") String storageContainer)
	{
		this.storageLogin = storageLogin;
		this.storagePassword = storagePassword;
		this.storageContainer = storageContainer;
	}


	@Autowired(required=false)
	public void setTelemetryClient(final TelemetryClient telemetryClient)
	{
		this.telemetryClient = telemetryClient;
	}


	/**
	 * @return A client that connects to Amazon with our supplied credentials
	 */
	private CloudStorageAccount getClient()
	{
		final String storageConnectionString = String.format(
				"DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s", storageLogin, storagePassword);
		try
		{
			return CloudStorageAccount.parse(storageConnectionString);
		}
		catch (URISyntaxException | InvalidKeyException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getFileNames()
	{
		final ArrayList<String> returnList = new ArrayList<>();
		logger.info("Retrieving files from Azure: {}", storageContainer);

		final CloudStorageAccount client = getClient();
		final CloudBlobClient blobClient = client.createCloudBlobClient();

		final AzureDependency azureDependency = createTracker(blobClient, "listBlobs");
		try
		{
			final CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
			for (final ListBlobItem blobItem : container.listBlobs())
			{
				if (blobItem instanceof CloudBlob)
				{
					returnList.add(((CloudBlob) blobItem).getName());
				}
			}
			azureDependency.trackSuccess();
		}
		catch (Exception e)
		{
			azureDependency.trackFailure();
			throw new RuntimeException("Error retrieving file list", e);
		}

		return returnList;
	}


	@Override
	public URL getFile (final String fileName)
	{
		logger.info("Redirecting to file from Azure: {}/{}", storageContainer, fileName);

		final CloudStorageAccount client = getClient();
		final CloudBlobClient blobClient = client.createCloudBlobClient();

		final AzureDependency azureDependency = createTracker(blobClient, "getFile");
		try
		{
			final CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
			final CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			azureDependency.trackSuccess();
			return blob.getUri().toURL();
		}
		catch (Exception e)
		{
			azureDependency.trackFailure();
			throw new RuntimeException("Error retrieving file: " + fileName, e);
		}
	}

	@Override
	public void saveFile (final String fileName, final long fileSize, final Date lastModified, final String mimeType, final InputStream in)
	throws IOException
	{
		logger.info("Uploading file to Azure: {}/{}", storageContainer, fileName);

		final CloudStorageAccount client = getClient();
		final CloudBlobClient blobClient = client.createCloudBlobClient();

		final AzureDependency azureDependency = createTracker(blobClient, "saveFile");
		try
		{
			final CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
			final CloudBlob blob = container.getBlockBlobReference(fileName);
			blob.getProperties().setContentType(mimeType);
			blob.getProperties().setCacheControl("public,max-age=300"); // allow clients to cache the file for up to 5 minutes (should probably also set HttpExpires, which was the predecessor...)

			blob.upload(in, fileSize);
			logger.info("Upload track");
			azureDependency.trackSuccess();
		}
		catch (Exception e)
		{
			azureDependency.trackFailure();
			throw new RuntimeException("Error uploading file", e);
		}
	}


	@Override
	public void deleteFile (final String fileName)
	throws IOException
	{
		logger.info("Deleting file from Azure: {}/{}", storageContainer, fileName);

		final CloudStorageAccount client = getClient();
		final CloudBlobClient blobClient = client.createCloudBlobClient();

		final AzureDependency azureDependency = createTracker(blobClient, "deleteFile");
		try
		{
			final CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
			final CloudBlob blob = container.getBlockBlobReference(fileName);

			blob.deleteIfExists();
			azureDependency.trackSuccess();
		}
		catch (Exception e)
		{
			azureDependency.trackFailure();
			throw new RuntimeException("Error deleting file", e);
		}
	}

	@Override
	public boolean isOk() throws Exception
	{
		final CloudStorageAccount client = getClient();
		final CloudBlobClient blobClient = client.createCloudBlobClient();
		final CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
		final boolean exists = container.exists();
		createTracker(blobClient, "ping").track(exists);
		return exists;
	}



	private AzureDependency createTracker(CloudBlobClient blobClient, final String action)
	{
		// Note: if telemetryClient is null, Azure lib won't try to track the dependency
		final String blobStorageUrl = blobClient.getStorageUri().getPrimaryUri().toString() + "/" + storageContainer;
		return new AzureDependency(telemetryClient, blobStorageUrl, action);
	}

}
