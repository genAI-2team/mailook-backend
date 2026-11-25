package com.mailook.user.controller;


// import java.security.Principal;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.server.ResponseStatusException;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailook.global.security.JwtProvider;
import com.mailook.user.dto.LoginRequest;
import com.mailook.user.dto.LoginReponse;
import com.mailook.user.entity.Login;
import com.mailook.user.service.Loginservice;

@RestController
@RequestMapping("/api/maiLookLogin")
public class loginController {
    private final Loginservice loginService;
    private final JwtProvider jwtProvider;

    public loginController(Loginservice loginService, JwtProvider jwtProvider) {
        this.loginService = loginService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginReponse> login(@RequestBody LoginRequest loginRequest) {
        // 요청 DTO를 엔티티로 변환한다.
        Login login = new Login();
    
        login.setEmail(loginRequest.getEmail());
        login.setPassword(loginRequest.getPassword());
       

        // 서비스에 로그인 처리를 위임한다: success, wrongEmail, wrongPassword.
        String result = loginService.login(login);
                 
        // 응답 생성은 MemberController처럼 Controller에서 담당한다.
        LoginReponse response = new LoginReponse();
      
        if (!"success".equals(result)) {
            response.setStatus("fail");
     
            response.setMessage(Loginservice.WRONG_CREDENTIALS);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Login dbLogin = loginService.getLogin(loginRequest.getEmail().trim());
        response.setStatus("success");
        response.setMessage("로그인되었습니다.");
        response.setUserId(dbLogin.getUserId());
        response.setEmail(dbLogin.getEmail());
        response.setNickname(dbLogin.getNickname());
        response.setCreatedAt(dbLogin.getCreatedAt());
        response.setLastLoginAt(dbLogin.getLastLoginAt());
        response.setAccessToken(jwtProvider.createToken(dbLogin));
        return ResponseEntity.ok(response);
    }

//     @PostMapping("/insert")
//     @ResponseStatus(HttpStatus.CREATED)
//     public LoginReponse insert(@RequestBody LoginRequest loginRequest) {
//         Login login = new Login();
//         login.setEmail(loginRequest.getEmail());
//         login.setPassword(loginRequest.getPassword());
//         login.setNickname(loginRequest.getNickname());
//         loginService.insert(login, loginRequest.getLocationConsent());

//         Login dbLogin = loginService.selectById(login.getEmail(), login.getUserId());
//         LoginReponse response = new LoginReponse();
//         response.setStatus("success");
//         response.setMessage("가입되었습니다.");
//         response.setUserId(dbLogin.getUserId());
//         response.setEmail(dbLogin.getEmail());
//         response.setNickname(dbLogin.getNickname());
//         response.setCreatedAt(dbLogin.getCreatedAt());
//         response.setLastLoginAt(dbLogin.getLastLoginAt());
//         return response;
//     }

//     @GetMapping("/selectById")
//     public LoginReponse selectById(@RequestParam("userId") Long userId, Principal principal) {
//         Login dbLogin = loginService.selectById(principal.getName(), userId);
//         LoginReponse response = new LoginReponse();
//         response.setStatus("success");
//         response.setUserId(dbLogin.getUserId());
//         response.setEmail(dbLogin.getEmail());
//         response.setNickname(dbLogin.getNickname());
//         response.setCreatedAt(dbLogin.getCreatedAt());
//         response.setLastLoginAt(dbLogin.getLastLoginAt());
//         return response;
//     }

//     @GetMapping("/selectCount")
//     public Map<String, Integer> selectCount(Principal principal) {
//         int totalRows = loginService.selectCount(principal.getName());
//         return Map.of("count", totalRows);
//     }

//     @GetMapping("/selectPage")
//     public Map<String, Object> selectPage(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
//             @RequestParam(value = "size", defaultValue = "10") int size, Principal principal) {
//         List<Login> list = loginService.selectPage(principal.getName(), pageNo, size);
//         List<LoginReponse> data = new ArrayList<>();
//         for (Login dbLogin : list) {
//             LoginReponse response = new LoginReponse();
//             response.setStatus("success");
//             response.setUserId(dbLogin.getUserId());
//             response.setEmail(dbLogin.getEmail());
//             response.setNickname(dbLogin.getNickname());
//             response.setCreatedAt(dbLogin.getCreatedAt());
//             response.setLastLoginAt(dbLogin.getLastLoginAt());
//             data.add(response);
//         }
//         int totalRows = loginService.selectCount(principal.getName());
//         return Map.of("pager", Map.of("pageNo", pageNo, "size", size, "totalRows", totalRows), "data", data);
//     }

//     @PutMapping("/update")
//     public LoginReponse update(@RequestParam("userId") Long userId, @RequestBody LoginRequest loginRequest,
//             Principal principal) {
//         Login login = new Login();
//         login.setUserId(userId);
//         login.setEmail(loginRequest.getEmail());
//         login.setPassword(loginRequest.getPassword());
//         login.setNickname(loginRequest.getNickname());
//         int rows = loginService.update(principal.getName(), login, loginRequest.getCurrentPassword());
//         if (rows != 1) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

//         String email = loginRequest.getEmail() == null ? principal.getName() : loginRequest.getEmail().trim();
//         Login dbLogin = loginService.selectById(email, userId);
//         LoginReponse response = new LoginReponse();
//         response.setStatus("success");
//         response.setMessage("수정되었습니다. 다시 로그인해 주세요.");
//         response.setUserId(dbLogin.getUserId());
//         response.setEmail(dbLogin.getEmail());
//         response.setNickname(dbLogin.getNickname());
//         response.setCreatedAt(dbLogin.getCreatedAt());
//         response.setLastLoginAt(dbLogin.getLastLoginAt());
//         return response;
//     }

//     @DeleteMapping("/deleteById")
//     @ResponseStatus(HttpStatus.NO_CONTENT)
//     public void deleteById(@RequestParam("userId") Long userId, @RequestBody LoginRequest body,
//             Principal principal) {
//         int rows = loginService.deleteById(principal.getName(), userId, body.getCurrentPassword());
//         if (rows != 1) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
//     }

//     @PostMapping("/logout")
//     @ResponseStatus(HttpStatus.NO_CONTENT)
//     public void logout(Principal principal) {
//         loginService.logout(principal.getName());
//     }


}
