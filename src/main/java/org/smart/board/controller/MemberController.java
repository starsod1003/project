package org.smart.board.controller;

import org.smart.board.entity.Member;
import org.smart.board.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /*
    로그인 화면 요청
    * */
    @GetMapping("/loginForm")
    public String loginForm() {

        return "account/loginForm";
    }

    /*
    * 로그인 처리 요청
    * */
    @GetMapping("/login")
    public String login(Member member) {
        Member m = memberService.selectMember(member);
        System.out.println(m);
        return "redirect:/";
    }

    /*
    * 회원 가입을 위한 화면 요청
    * */
    @GetMapping("/register")
    public String register() {

        return "account/register";
    }

    @PostMapping("/register")
    public String register(Member member) {
        memberService.insertMember(member);

        return "index";
    }
}
