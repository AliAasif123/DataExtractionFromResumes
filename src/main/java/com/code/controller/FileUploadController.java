package com.code.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.code.response.FileUploadResponse;
import com.code.response.InfoGet;
import com.code.service.FileUploadService;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@PostMapping("/upload")
	public ResponseEntity<FileUploadResponse> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message = fileUploadService.uploadFile(file);
		FileUploadResponse response = new FileUploadResponse(message);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get")
	public ResponseEntity<List<InfoGet>> gettingData() {
		//fileUploadService.gettingResponse();
		return new ResponseEntity<>(fileUploadService.gettingResponse(),HttpStatus.OK);
	}
}