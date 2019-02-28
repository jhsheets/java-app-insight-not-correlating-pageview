package com.example.filestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * A place where we store software files that will be requested by Runtime.
 * Generified to indicate the type of result returned from {@link #getFile(String)}
 */
public interface ISoftwareFileStorage
{
	/** Get the names of all available files */
	List<String> getFileNames();

	/** Return a {@link java.net.URL} for redirection */
	URL getFile(final String fileName);

	/** Save/upload a file, overwriting any existing file */
	void saveFile(final String fileName, final long fileSize, final Date lastModified, final String mimeType, final InputStream is) throws IOException;

	/** Delete a file */
	void deleteFile(final String fileName) throws IOException;

	boolean isOk() throws Exception;
}
