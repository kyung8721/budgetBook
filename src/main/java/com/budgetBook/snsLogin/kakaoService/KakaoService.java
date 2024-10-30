package com.budgetBook.snsLogin.kakaoService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.boot.json.JsonParser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KakaoService {
	/**
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

	        // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line = "";
	        String result = "";

	        while ((line = bufferedReader.readLine()) != null) {
	            result +=line;
	        }
	        
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
	
	public HashMap<String, Object> getUserInfo(String accessToken) {
	    HashMap<String, Object> userInfo = new HashMap<>();
	    String postURL = "https://kapi.kakao.com/v2/user/me";

	    try {
	        URL url = new URL(postURL);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");

	        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

	        int responseCode = conn.getResponseCode();
	        System.out.println("responseCode : " + responseCode);

	        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line = "";
	        StringBuilder result = new StringBuilder();

	        while ((line = br.readLine()) != null) {
	            result.append(line);
	        }
	        System.out.println("response body : " + result);

	        JsonElement element = JsonParser.parseString(result.toString());
	        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
	        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

	        String nickname = properties.getAsJsonObject().get("nickname").getAsString();
	        String email = kakaoAccount.getAsJsonObject().get("email").getAsString();

	        userInfo.put("nickname", nickname);
	        userInfo.put("email", email);

	    } catch (IOException exception) {
	        exception.printStackTrace();
	    }

	    return userInfo;
	} **/
}
