package com.budgetBook.common;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;


public class FileManager {
	
	// 기본 저장 경로 상수로 저장
	public static final String FILE_UPLOAD_PATH = "/home/ec2-user/upload"; // 리눅스 저장 위치
	
	// 파일 저장
	public static String saveFile(int userId, MultipartFile imageFile) {
		if(imageFile == null) {
			return null;
		}
		
		// userId_현재시간정보 폴더 > 이미지 파일
		// 폴더 이름 지정
		String directoryName = "/" + userId + "_" + System.currentTimeMillis();
		
		// 전체 경로 지정
		String directoryPath = FILE_UPLOAD_PATH + directoryName;
		
		// 파일 객체 생성
		File directory = new File(directoryPath);
		
		// 앞의 경로를 기반으로 디렉토리 생성
		if(!directory.mkdir()) {
			// 폴더 생성 실패 시
			return null;
		}
		
		// 파일 저장
		String imageFileNameDeleteSpace = imageFile.getOriginalFilename().replace(" ", "-"); // 이미지 내에 공백이 있으면 -로 변경
		String filePath = directoryPath + "/" + imageFileNameDeleteSpace;
		
		// 실제 파일
		try {
			byte[] bytes = imageFile.getBytes();
			Path path = Paths.get(filePath);
			Files.write(path, bytes); // 경로, 실제 데이터
		} catch (IOException e) {
			// 파일 저장 실패
			return null;
		}
		
		// 저장된 파일을 접근할 URL Path 만들기
		return "/images" + directoryName + "/" + imageFileNameDeleteSpace;
	}
	
	// 파일 삭제
	public static boolean deleteImageFile(String filePath) {
		
		// filePath가 null인 경우 중단
		if(filePath == null) {
			return false;
		}
		// 원래 이미지 파일 경로 복원
		String fullFilePath = FILE_UPLOAD_PATH + filePath.replace("/images", "/");
		
		// 이미지 경로 Path 객체 생성
		Path path = Paths.get(fullFilePath);
		// 이미지 저장 디렉토리 Path 객체 생성
		Path directoryPath = path.getParent(); // path.getParent() : path의 상위 경로를 찾아줌
		
		// 파일과 디렉토리가 존재하는지 확인 후 삭제
		if(Files.exists(path)){
			try {
				Files.delete(path); // 이미지 삭제
				Files.delete(directoryPath); // 이미지 디렉토리 삭제
			} catch (IOException e) {
				return false;
			}
			return true;
		}else {
			return false;
		}
		
	}
}