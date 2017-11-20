package com.sam.fuse.camel.pull.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sam.fuse.camel.pull.model.PullResponse;
import com.sam.fuse.camel.pull.service.FileUtil;

import java.io.File;

import javax.ws.rs.core.MediaType;
/**
 * Created by Chathura
 */
public class FileUtilProcessor implements Processor {

	private FileUtil fileUtil;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String RESPONSE_TEXT = "response_message_details";
	public static final String REQUEST_TEXT = "request_message_details";
	
	@Override
    public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeader(REQUEST_TEXT, exchange.getIn().getHeader("CamelHttpUrl").toString());		
        Object errorMesage = exchange.getIn().getHeader("ERROR_MESSAGE");
        if (errorMesage != null) {
            exchange.getIn().setBody(buildWitsError(errorMesage.toString()));
        } else {
        	buildWitsPullResponse(exchange);       
        }
    }

	public PullResponse buildWitsError(String errorDescription) {
		PullResponse pullResponse = new PullResponse();
		pullResponse.setError(errorDescription);
		return pullResponse;
	}

	public void buildWitsPullResponse(Exchange exchange) {

		String file_path = exchange.getIn().getHeader("FILE_LOCATION", String.class);
		String file_name = exchange.getIn().getHeader("filename", String.class);

		File file = new File(file_path+file_name);
		logger.debug("*********** File path = " + file_path);
		fileUtil = new FileUtil();
		String extension = fileUtil.getFileExtension(file);
		if (fileUtil.isFileExists(file_path)) {
			exchange.getIn().setHeader("Content-Disposition", "attachment; filename=" + file.getName());
			if ("xml".equals(extension)) {
				exchange.getIn().setHeader("Content-Type", MediaType.APPLICATION_XML);
			} else if ("csv".equals(extension)) {
				exchange.getIn().setHeader("Content-Type", MediaType.TEXT_PLAIN);
			} else {
				exchange.getIn().setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
			}
			exchange.getIn().setBody(file);
			exchange.getIn().setHeader(RESPONSE_TEXT, exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_TEXT, String.class));			
		} else {
			String errorMessage = "File not exists ["
					+ file_path+file_name + "]";
			exchange.getIn().setBody(buildWitsError(errorMessage));
			exchange.getIn().setHeader(RESPONSE_TEXT, errorMessage);	
		}
	}
}
