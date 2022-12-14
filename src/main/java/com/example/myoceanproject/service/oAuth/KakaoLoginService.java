package com.example.myoceanproject.service.oAuth;
import com.example.myoceanproject.domain.QUserDTO;
import com.example.myoceanproject.domain.UserDTO;
import com.example.myoceanproject.entity.User;
import com.example.myoceanproject.repository.UserRepository;
import com.example.myoceanproject.type.UserAccountStatus;
import com.example.myoceanproject.type.UserLoginMethod;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.myoceanproject.entity.QUser.user;

@Service
@Slf4j
public class KakaoLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    public String getKaKaoAccessToken(String code){
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=e95b1771a295ce3312e3c0ef9a45a424"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:15000/login/kakaoLogin"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            log.info("access_token : " + access_Token);
            log.info("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public User getKakaoInfo(String token) throws Exception {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            log.info("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            boolean hasEmail=false;
            int id = element.getAsJsonObject().get("id").getAsInt();
            if(element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email_needs_agreement").getAsBoolean()==false) {
                hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            }else{
                hasEmail=false;
            }
            String nickname=element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();
            String userImage=element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();
            String userOauthId=element.getAsJsonObject().get("id").getAsString();
            String email = "";

            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }else{
                email="secureLevelTwo";
            }

            log.info("nickname :"+nickname);
            log.info("id : " + id);
            log.info("email : " + email);

            List<UserDTO> users =jpaQueryFactory.select(new QUserDTO(
                    user.userId,
                    user.userPassword,
                    user.userNickname,
                    user.userAccountStatus,
                    user.userFileName,
                    user.userFilePath,
                    user.userFileSize,
                    user.userFileUuid,
                    user.userEmail,
                    user.userLoginMethod,
                    user.userTotalPoint,
                    user.createDate,
                    user.updatedDate,
                    user.userOauthId
            )).from(user).where(user.userEmail.eq(email).and(user.userOauthId.like(userOauthId))).fetch();

            UserDTO userDTO=new UserDTO();
            log.info("user:"+user);

            User user=new User();
            if(users.size()==1){
                userDTO.setUserEmail(users.get(0).getUserEmail());
                userDTO.setUserNickname(users.get(0).getUserNickname());
                userDTO.setUserLoginMethod(users.get(0).getUserLoginMethod());
                userDTO.setUserOauthId(users.get(0).getUserOauthId());
                user=userDTO.toEntity();
                user.setUserLoginMethod(UserLoginMethod.change(userDTO.getUserLoginMethod()));
                user.setUserId(users.get(0).getUserId());
            }else{
                userDTO.setUserEmail(email);
                user=userDTO.toEntity();
            }



            br.close();
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void logoutKakao(String token){
        String reqURL ="https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + token);
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            if(responseCode ==400)
                throw new RuntimeException("카카오 로그아웃 도중 오류 발생");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String br_line = "";
            String result = "";
            while ((br_line = br.readLine()) != null) {
                result += br_line;
            }
            log.info("결과");
            log.info(result);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
