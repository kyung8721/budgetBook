package com.budgetBook.snsLogin.kakaoService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class KakaoService {
	
	public String getKakaoAccessToken (String code) {
	    String accessToken = "";
	    String refreshToken = "";
	    String requestURL = "https://kauth.kakao.com/oauth/token";

	    try {
	    	
	    	// url 연결
	        URL url = new URL(requestURL);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        
	        // post 방식으로 넘겨주겠다는 설정
	        conn.setRequestMethod("POST");
	        
	        // POST 요청을 수행하려면 setDoOutput()을 true로 설정한다.
	        conn.setDoOutput(true);

	        // POST 요청에서 필요한 파라미터를 OutputStream을 통해 전송
	        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
	        String sb = "grant_type=authorization_code" +
	                "&client_id=REST_API_KEY 입력" + // REST_API_KEY
	                "&redirect_uri=http://localhost:8080/app/login/kakao" + // REDIRECT_URI
	                "&code=" + code;
	        bufferedWriter.write(sb);
	        bufferedWriter.flush();

	        int responseCode = conn.getResponseCode(); // 200이라면 성공
	        System.out.println("responseCode : " + responseCode);

	        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line = "";
	        String result = "";

	        while ((line = bufferedReader.readLine()) != null) {
	            result +=line;
	        }
	        System.out.println("result : " + result);
	        
	        
	        Gson gson = new Gson();
            JsonElement element = gson.fromJson(result, JsonElement.class); // string을 jsonElement로 변환

	        accessToken = element.getAsJsonObject().get("access_token").getAsString();
	        refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

	        bufferedReader.close();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return accessToken;
	}
	
	// 사용자 조회
	public HashMap<String, Object> getUserInfo(String accessToken) {
	    HashMap<String, Object> userInfo = new HashMap<>();
	    String postURL = "https://kapi.kakao.com/v2/user/me";

	    try {
	        URL url = new URL(postURL);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        
	        // Request Header값 셋팅 setRequestProperty(String key, String value)
	        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

	        int responseCode = conn.getResponseCode();

	        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line = "";
	        String result = "";

	        while ((line = br.readLine()) != null) {
	            result += line;
	        }
	        System.out.println("response body : " + result);

	        Gson gson = new Gson();
            JsonElement element = gson.fromJson(result, JsonElement.class); // string을 jsonElement로 변환
            
	        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
	        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

	        // String nickname = properties.getAsJsonObject().get("nickname").getAsString();
	        String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
	        String profileImagePath = kakaoAccount.getAsJsonObject().get("profile_image_url").getAsString();
	        
	        // userInfo.put("nickname", nickname);
	        userInfo.put("email", email);
	        userInfo.put("profileImagePath", profileImagePath);

	    } catch (IOException exception) {
	        exception.printStackTrace();
	    }

	    return userInfo;
	}
	
	// 사용자 가입
	public Map<String, Object> createKakaoUser(String accesstoken) {

        String reqURL = "https://kauth.kakao.com/oauth/token";
        Map<String, Object> resultMap = new HashMap<>();

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accesstoken); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = buffer.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(result, JsonElement.class);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            
            resultMap.put("id", id);
            resultMap.put("emial", email);
            
            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
       
        
        return resultMap;
    }
}
