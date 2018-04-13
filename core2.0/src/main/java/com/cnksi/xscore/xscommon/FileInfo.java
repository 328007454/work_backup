package com.cnksi.xscore.xscommon;

import java.util.Locale;

public class FileInfo {
	enum FileType {
		FILE, DIRECTORY;
	}

	private FileType fileType;
	private String fileName;
	private String filePath;

	public FileInfo(String filePath, String fileName, boolean isDirectory) {
		this.filePath = filePath;
		this.fileName = fileName;
		fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
	}

	public boolean isFile() {
		if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
			return false;
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase(Locale.CHINA);
		if (!isDirectory() && (fileSuffix.endsWith(".jpg") || fileSuffix.endsWith(".png") || fileSuffix.endsWith(".git") || fileSuffix.endsWith(".txt") || fileSuffix.endsWith(".doc") || fileSuffix.endsWith(".docx")))
			return true;
		else
			return false;
	}

	public boolean isDirectory() {
		if (fileType == FileType.DIRECTORY)
			return true;
		else
			return false;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "FileInfo [fileType=" + fileType + ", fileName=" + fileName + ", filePath=" + filePath + "]";
	}
}