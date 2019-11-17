package com.hanpass.bangladeshIncentive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/bangladeshIncentive")
@CrossOrigin(origins="*")
public class FileServeController {
	
	@RequestMapping(value="/ping")
	public String ping() throws Exception {
		return "" + new Date();
	}
	
	
	public static final String FILE_DIR = "C://Users/lee/Downloads";
	
	
	
	// 파일 목록 제공 JSON
	
	// 파일 업로드
	@PostMapping(value="/upload")
	public String upload(@RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
		
		for ( MultipartFile multipartFile : multipartFiles) {
			
			log.debug("UPLOADED " + multipartFile.getName());
			System.out.print("UPLOADED " + multipartFile.getName() + "\t" + multipartFile.getOriginalFilename());
			System.out.println(multipartFile.getContentType() + "\t" + multipartFile.getSize());
		}
		
		return "OK";
		
		//FileUtils.copyInputStreamToFile(source, destination);
	}
	
	
	
	/**
	 * 파일 서빙
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	//@GetMapping(value="/files/{fileName}", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@GetMapping(value="/files/{fileName}")
	public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) throws IOException {
		log.info("파일 제공 [" + fileName + "]");
		
		// 1. 세션 체크 : 우리 DB의 세션을 체크해서 접근 여부를 결정한다.
		// Header에서 HANPASS SESSION 정보를 가져온다.
		// session DB에서 값을 비교한 후, 로그인 여부를 확인한다.
		
		
		
		// 2. 파일 다운로드를 실시한다.
				
		ResponseEntity<byte[]> entity = null;		
		
		File file = new File(FILE_DIR + "/" + fileName);
		if ( !file.exists() )
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, fileName);
		
		InputStream is = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(getMediaTypeFromFileName(fileName));
			
			is = new FileInputStream(file);
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(is), headers, HttpStatus.CREATED);
		} catch(Exception ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, fileName, ex);
		} finally {
			try { is.close(); } catch(Exception ignore) {}
		}
		
		return entity;
		
	}
	
	public MediaType getMediaTypeFromFileName(String fileName) {
		if ( fileName == null ) return null;
		
		fileName = fileName.trim();
		if ( fileName.equals("") ) return null;		
				
		String extName = fileName.toUpperCase().substring(fileName.lastIndexOf(".")+1);
		if ( extName.equals("") ) return null;
		
		if ( extName.equals("JPG") || extName.equals("JPEG") ) return MediaType.IMAGE_JPEG;
		if ( extName.equals("GIF") ) return MediaType.IMAGE_GIF;
		if ( extName.equals("PNG") ) return MediaType.IMAGE_PNG;
		
		if ( extName.equals("PDF") ) return MediaType.APPLICATION_PDF;
		
		
		return null;
	}

}
