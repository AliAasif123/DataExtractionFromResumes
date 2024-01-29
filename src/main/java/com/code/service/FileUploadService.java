package com.code.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.code.Entity.Resume;
import com.code.repositories.ResumeRepositories;
import com.code.response.InfoGet;

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

			String name = extractName(text);
			String phoneNumber = extractPhoneNumber(text);
			String email = extractEmail(text);
			double extractExperienceInYearsAndMonths = extractExperienceInYears(text);
			String skills = extractSkills(text);

			saveToDatabase(name, phoneNumber, email, extractExperienceInYearsAndMonths, skills);

			return "Information extracted and saved successfully.";
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return "Error extracting information from the resume.";
		}
	}

	private static String extractSkills(String text) {
		int startIdx = text.indexOf("SKILLS");
		if (startIdx == -1) {
			return "";
		}

		int endIdx = text.indexOf("SPECIFIC EXPERTISE", startIdx);
		if (endIdx == -1) {
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
		Pattern pattern = Pattern.compile("\\b\\d{10}\\b");
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "";
	}

	private static String extractName(String text) {
		String[] lines = text.split("\\r?\\n");
		System.out.println("First Line: " + lines[0]);
		return lines[0].trim();
	}

	private static double extractExperienceInYears(String text) {
		Pattern experiencePattern = Pattern.compile("\\b(\\d+(\\.\\d+)?)\\s*years?\\s*of\\s*Java\\s*experience\\b");
		Matcher matcher = experiencePattern.matcher(text);

		// Check if a match is found
		if (matcher.find()) {
			return Double.parseDouble(matcher.group(1));
		} else {
			return 0.0;
		}
	}

	private void saveToDatabase(String name, String phoneNumber, String email, Double extractExperienceInYearsAndMonths,
			String skills) {
		Resume resume = new Resume();

		resume.setName(name);
		resume.setPhoneNumber(phoneNumber);
		resume.setEmail(email);
		resume.setExperience(extractExperienceInYearsAndMonths);
		resume.setSkills(skills);
		resumeRepositories.save(resume);
	}

	public List<InfoGet> gettingResponse() {
		List<Resume> findAll = resumeRepositories.findAll();
		List<InfoGet> convertingEntityToDto = convertingEntityToDto(findAll);
		return convertingEntityToDto;
	}

	public List<InfoGet> convertingEntityToDto(List<Resume> resumes) {
		// InfoGet get = new InfoGet();
		List<InfoGet> collect = resumes.stream()
				.map(i -> new InfoGet(i.getPhoneNumber(), i.getName(), i.getEmail(), i.getExperience(), i.getSkills()))
				.collect(Collectors.toList());
//		BeanUtils.copyProperties(findAll, get);
		return collect;

	}

}
