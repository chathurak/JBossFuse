package com.sam.fuse.camel.pull.processors;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.InputRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipartFileDownloadProcessor implements Processor {

	private Logger logger = LoggerFactory.getLogger(MultipartFileDownloadProcessor.class);

	/**
	 * This is supposed to set the expected media type of the file. As yet
	 * unimplemented
	 */
	private final String mediaType;

	/**
	 * The directory to store the uploaded files
	 */
	private final String fileDownloadDirectory;

	public MultipartFileDownloadProcessor(String mediaType, String fileDownloadDirectory) {
		super();
		this.mediaType = mediaType;
		this.fileDownloadDirectory = fileDownloadDirectory;
	}

	public String getMediaType() {
		return mediaType;
	}

	public String getFileUploadDirectory() {
		return fileDownloadDirectory;
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		Message message = exchange.getIn();

		String companyCode = (String) message.getHeader("COMPANY_CODE");
		logger.info(String.format("Company Code: %s", companyCode));

		logger.info(String.format("File upload location: %s", this.fileDownloadDirectory));
		logger.info(String.format("File media type: %s", this.mediaType));

		String contentType = (String) message.getHeader(Exchange.CONTENT_TYPE);
		logger.debug(String.format("Request content-type: $s", contentType));

		MediaType mediaType = MediaType.valueOf(contentType);
		if (mediaType == null) {
			throw new Exception(String.format("Expected %s", MediaType.MULTIPART_FORM_DATA.getName()));
		}

		if (!mediaType.getName().contains("multipart/form-data")) {
			throw new Exception(String.format("Expected %s", MediaType.MULTIPART_FORM_DATA.getName()));
		}

		InputStream inputStream = message.getBody(InputStream.class);

		InputRepresentation representation = new InputRepresentation(inputStream, mediaType);

		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(1000240);

		List<FileItem> items = new RestletFileUpload(diskFileItemFactory).parseRepresentation(representation);

		for (FileItem item : items) {

			if (item.isFormField()) {

				String name = item.getFieldName();
				logger.debug(String.format("Processing form field name: %s", name));

				String value = item.getString("UTF-8");
				logger.debug(String.format("Field value: %s", value));

				if (value == null || value.isEmpty()) {
					throw new Exception("Could not field value");
				}

			} else if (!item.isFormField()) {

				String fileName = item.getName();

				logger.debug(String.format("Processing file name: %s", fileName));

				if (fileName == null || fileName.isEmpty()) {
					throw new Exception("Could not parse file");
				}

				String fileContentType = item.getContentType();
				logger.debug(String.format("File Content Type: %s", fileContentType));

				long fileSizeInBytes = item.getSize();
				logger.debug(String.format("File size (bytes): %s", fileSizeInBytes));

				Path filePath = Paths.get(this.fileDownloadDirectory + "/" + companyCode + "/" + fileName);
				if (!filePath.getParent().toFile().exists()) {
					Files.createDirectories(filePath.getParent());
				}
				Files.createFile(filePath);

				logger.info("Wrote file: " + filePath.toAbsolutePath());
				item.write(filePath.toFile());

			}

		}

	}

}