package com.knuaf.oneday.controller;

import com.knuaf.oneday.dto.MypageRequest;
import com.knuaf.oneday.dto.SignupRequest;
import com.knuaf.oneday.entity.User;
import com.knuaf.oneday.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@CrossOrigin(origins = "http://localhost:8080") // 3000번 포트 허용

public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUSer(@ModelAttribute SignupRequest request){
        userService.register(request);
        //after signup success, redirect to login page
        return "redirect:/home";
    }

    @GetMapping("/mypage")
    public String showMyPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login"; // 로그인 안 했으면 로그인 창으로
        }

        // DB에서 내 정보 가져오기
        User user = userService.getMyInfo(userDetails.getUsername());

        // HTML로 데이터 전달 ("user"라는 이름으로 사용 가능)
        model.addAttribute("user", user);

        return "mypage"; // templates/mypage.html 파일을 찾아감
    }

    // ✅ 2. 마이페이지 정보 수정 처리 (POST)
    // HTML form은 기본적으로 PUT을 지원하지 않아 POST를 사용합니다.
    @PostMapping("/mypage/update")
    public String updateMyPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute MypageRequest updateDto) { // @RequestBody -> @ModelAttribute 변경

        // 서비스 호출하여 정보 수정
        userService.updateUserInfo(userDetails.getUsername(), updateDto);

        // 수정 후 다시 마이페이지로 돌아가서 변경된 값 확인
        return "redirect:/mypage";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }
}
