package com.example.endpoints.mvc;

import com.example.filestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
@RequestMapping("/html/files")
public class FileServerService
{
	private static final Logger logger = LoggerFactory.getLogger(FileServerService.class);

	private final ISoftwareFileStorage sharedStorage;



	@Autowired
	public FileServerService(final ISoftwareFileStorage sharedStorage)
	{
		this.sharedStorage = sharedStorage;
	}


	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView listFiles(final HttpServletRequest request)
	{
        logger.info("Listing all files");

		// Set the name of the thymeleaf template (resources/templates/fileManager.html)
		final ModelAndView modelAndView = new ModelAndView("fileManager");

		// Pass in the following variables to be accessible inside thymeleaf template
		modelAndView.addObject("requestURI", request.getRequestURI());
		modelAndView.addObject("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		modelAndView.addObject("normalFiles", sharedStorage.getFileNames());

		return modelAndView;
	}


	@RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity uploadFile(@RequestParam("file") final MultipartFile upload)
	throws IOException
	{
		try (final InputStream is = upload.getInputStream()) // try block will close InputStream
		{
			final String fileName = Paths.get(upload.getOriginalFilename()).getFileName().toString();
			final long fileSize = upload.getSize();
			final Date lastMod = new Date(); // XXX: we'd need the caller to pass in the file size to preserve this...
			final String mimeType = upload.getContentType();

			logger.info("Uploading file: {}", fileName); // since we're using http upload, this log won't occur until the file is completely sent to us
			sharedStorage.saveFile(fileName, fileSize, lastMod, mimeType, is);
		}

		return ResponseEntity.ok("redirect:/html/files");
	}


	@RequestMapping(value = "/delete/{fileName:.+}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity deleteFile(@PathVariable("fileName") final String fileName)
	throws IOException
	{
		logger.info("Deleting file: {}", fileName);
		sharedStorage.deleteFile(fileName);

		return ResponseEntity.ok("redirect:/html/files");
	}

}
