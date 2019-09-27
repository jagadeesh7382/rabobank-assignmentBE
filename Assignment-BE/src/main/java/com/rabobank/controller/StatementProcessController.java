/**
 * @author Jagadeesh
 * assignment
 */
package com.rabobank.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.constant.RabobankConstants;
import com.rabobank.model.AppResponse;
import com.rabobank.model.Record;
import com.rabobank.service.ExtractorService;
import com.rabobank.service.ValidatorService;

/**
 * @author Jagadeesh
 */

@RestController
@RequestMapping("/rabobank")
public class StatementProcessController {

	@Autowired
	private ValidatorService validatorService;

	@Autowired
	private ExtractorService extractorService;

	
	@RequestMapping(value = "/processStatment", method = RequestMethod.POST)
	public @ResponseBody AppResponse handleFileUpload(@RequestParam("file") MultipartFile multipart) throws Exception {
		AppResponse appResponse = new AppResponse();
		if (!multipart.isEmpty()) {
			if (multipart.getContentType().equalsIgnoreCase(RabobankConstants.FILE_TYPE_CSV)) {
				List<Record> errorRecords = new ArrayList<Record>();
				File csvFile = new File(multipart.getOriginalFilename());
				multipart.transferTo(csvFile);
				List<Record> extractedRecords = extractorService.extractStatmentFromCSV(csvFile);
				errorRecords.addAll(validatorService.getDuplicateRecords(extractedRecords));
				errorRecords.addAll(validatorService.getEndBalanceErrorRecords(extractedRecords));				
				if (!errorRecords.isEmpty()) {
					appResponse.setResponseCode(RabobankConstants.HTTP_CODE_SUCCESS);
					appResponse.setResponseMessage(RabobankConstants.VALIDATION_ERROR);
					appResponse.setRecords(errorRecords);
				} else {
					appResponse.setResponseCode(RabobankConstants.HTTP_CODE_SUCCESS);
					appResponse.setResponseMessage(RabobankConstants.VALIDATION_ERROR);
				}				
			} else if (multipart.getContentType().equalsIgnoreCase(RabobankConstants.FILE_TYPE_XML)) {
				List<Record> errorRecords = new ArrayList<Record>();
				File xmlFile = new File(multipart.getOriginalFilename());
				multipart.transferTo(xmlFile);
				List<Record> extractedRecords = extractorService.extractStatmentFromXML(xmlFile);
				errorRecords.addAll(validatorService.getDuplicateRecords(extractedRecords));
				errorRecords.addAll(validatorService.getEndBalanceErrorRecords(extractedRecords));
				if (!errorRecords.isEmpty()) {
					appResponse.setResponseCode(RabobankConstants.HTTP_CODE_SUCCESS);
					appResponse.setResponseMessage(RabobankConstants.VALIDATION_ERROR);
					appResponse.setRecords(errorRecords);
				} else {
					appResponse.setResponseCode(RabobankConstants.HTTP_CODE_SUCCESS);
					appResponse.setResponseMessage(RabobankConstants.VALIDATION_ERROR);
				}
			} else {
				appResponse.setResponseCode(RabobankConstants.HTTP_CODE_INVALID_INPUT);
				appResponse.setResponseMessage(RabobankConstants.UNSUPORTED_FILE_FORMAT);
			}
		} else {
			appResponse.setResponseCode(RabobankConstants.HTTP_CODE_INVALID_INPUT);
			appResponse.setResponseMessage(RabobankConstants.INVALID_INPUT);
		}
		return appResponse;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody AppResponse handleException(HttpServletRequest request, Exception ex) {
		AppResponse appResponse = new AppResponse();
		appResponse.setResponseCode(RabobankConstants.HTTP_CODE_ERROR);
		appResponse.setResponseMessage(RabobankConstants.UNEXPECTED_SERVER_ERROR);
		return appResponse;
	}

}
