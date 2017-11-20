package com.sam.fuse.camel.pull.model;

import java.io.Serializable;

import javax.ws.rs.core.StreamingOutput;


/**
 * Created by Chathura
 */
public class PullResponse implements Serializable {

	private PullStatus status = PullStatus.SUCCESS;
	private String error;
	
	public PullStatus getStatus() {
		return status;
	}

	public String getStatusDescription() {
		return status.equals(PullStatus.ERROR) ? error : "";
	}

	public void setError(String error) {
		this.status = PullStatus.ERROR;
		this.error = error;
	}	
	
}
