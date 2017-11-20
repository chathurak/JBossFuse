package com.sam.fuse.camel.pull.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

/**
 * Created by Chathura
 */
public class FileUtil {

	public boolean isFileExists(String file_path) {
		return Files.exists(Paths.get(file_path), LinkOption.NOFOLLOW_LINKS);
	}

	public String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
		} catch (Exception e) {
			return "";
		}
	}

	public byte[] getBytesFromFile(File file) throws IOException { 
	   // Get the size of the file 
		  long length = file.length(); if (length >
		  Integer.MAX_VALUE) { // File is too large 
			  throw new IOException("File is too large!"); } // Create the byte array to hold the data 
		  byte[] bytes = new byte[(int) length];
		  
		  // Read in the bytes 
		  int offset = 0; int numRead = 0; 
		InputStream is = new FileInputStream(file); 
		  try { while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) 
		  { offset += numRead; } } 
		  finally { is.close(); } // Ensure all the bytes have been read in 
		  if (offset < bytes.length) { 
			  throw new IOException("Could not completely read file " + file.getName()); 
			  }
		  return bytes; }
}
