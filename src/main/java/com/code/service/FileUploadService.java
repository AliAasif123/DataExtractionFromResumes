package com.code.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.code.Entity.Resume;
import com.code.repositories.ResumeRepositories;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FileUploadService {

	private final ResumeRepositories resumeRepositories;

	public String uploadFile(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			PDDocument document = PDDocument.load(inputStream);
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			String text = pdfTextStripper.getText(document);

			// Extract information from the resume text
			String name = extractName(text);
			String phoneNumber = extractPhoneNumber(text);
			String email = extractEmail(text);
			double extractExperienceInYearsAndMonths = extractExperienceInYears(text);
			String skills = extractSkills(text);

			// TODO: Save the extracted information to the database

			saveToDatabase(name, phoneNumber, email, extractExperienceInYearsAndMonths, skills);

			return "Information extracted and saved successfully.";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Error extracting information from the resume.";
		}
	}

	private static String extractSkills(String text) {
		int startIdx = text.indexOf("Skills");
		if (startIdx == -1) {
			// "Skills" section not found
			return "";
		}

		int endIdx = text.indexOf("AWARDS", startIdx);
		if (endIdx == -1) {
			// "AWARDS" section not found after "Skills"
			return text.substring(startIdx).trim();
		}

		return text.substring(startIdx, endIdx).trim();
	}

	private static String extractEmail(String text) {
		Pattern pattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "";
	}

	private static String extractPhoneNumber(String text) {
		// Assuming a basic phone number pattern
		Pattern pattern = Pattern.compile("\\b\\d{10}\\b");
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "";
	}

	private static String extractName(String text) {
		String[] lines = text.split("\\r?\\n");
		System.out.println("First Line: " + lines[0]);
		return lines[0].trim();
	}

//	private static String extractExperience(String text) {
//		// Extracting the experience section
//		int startIdx = text.indexOf("Experience");
//		int endIdx = text.indexOf("Skills");
//		return startIdx != -1 && endIdx != -1 ? text.substring(startIdx, endIdx).trim() : "";
//	}

//	    private static String extractExperience(String text) {
//	        int startIdx = text.indexOf("Experience");
//	        if (startIdx == -1) {
//	            // "Experience" section not found
//	            return "";
//	        }
//
//	        int endIdx = text.indexOf("Skills", startIdx);
//	        if (endIdx == -1) {
//	            // "Skills" section not found after "Experience"
//	            return text.substring(startIdx).trim();
//	        }
//
//	        return text.substring(startIdx, endIdx).trim();
//	    }
	

	       

	    private static double extractExperienceInYears(String text) {
	        int startIdx = text.indexOf("Professional Experience");
	        if (startIdx == -1) {
	            // "Professional Experience" section not found
	            return 0.0;
	        }

	        int endIdx = text.indexOf("Extra Curricular Activities", startIdx);
	        if (endIdx == -1) {
	            // "Extra Curricular Activities" section not found after "Professional Experience"
	            return 0.0;
	        }

	        String experienceSection = text.substring(startIdx, endIdx).trim();

	        String[] words = experienceSection.split("\\s+");
	        double totalExperience = 0.0;

	        for (int i = 0; i < words.length; i++) {
	            if (words[i].matches("\\d+")) {
	                int years = Integer.parseInt(words[i]);
	                totalExperience += years;

	                if (i + 1 < words.length && words[i + 1].matches("\\d+")) {
	                    int months = Integer.parseInt(words[i + 1]);
	                    totalExperience += months / 12.0;
	                }
	            }
	        }

	        return totalExperience;
	    }
	



	private void saveToDatabase(String name, String phoneNumber, String email, Double extractExperienceInYearsAndMonths, String skills) {
		Resume resume = new Resume();

		resume.setName(name);
		resume.setPhoneNumber(phoneNumber);
		resume.setEmail(email);
		resume.setExperience(extractExperienceInYearsAndMonths);
		resume.setSkills(skills);
		resumeRepositories.save(resume);
	}
}
