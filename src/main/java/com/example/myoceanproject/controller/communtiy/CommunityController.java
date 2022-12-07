package com.example.myoceanproject.controller.communtiy;

import com.example.myoceanproject.domain.CommunityPostDTO;
import com.example.myoceanproject.service.community.CommunityPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community/*")
public class CommunityController {

    private final CommunityPostService communityPostService;

    // 커뮤니티 페이지
    @GetMapping("/index")
    public String community(){
        return "app/community/community";
    }

    // 커뮤니티 상세 페이지
    @GetMapping("/read")
    public String communityDetail(Long communityPostId, Model model){
        model.addAttribute("communityPostDTO", communityPostService.find(communityPostId));
        return "app/community/detail";
    }

    // 커뮤니티 댓글 페이지
    @GetMapping("/comment")
    public String comment(){
        return "app/community/community_comment";
    }

    // 커뮤니티 글쓰기 페이지
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("communityPostDTO", new CommunityPostDTO());
        return "app/community/community_register";
    }

/*    @GetMapping("/write")
    public String write(){
        return "app/community/community_write";
    }*/

    // 커뮤니티 상세보기 페이지
    @GetMapping("/detail")
    public String detail(){
        return "app/community/detail";
    }

    /* 고민상담 게시판 */
    @GetMapping("/anonymous")
    public String anonymous(){ return "app/anonymous/anonymous"; }

    /* 모임 목록 */
    @GetMapping("/bulletin")
    public String bulletin(){ return "app/bulletin_board/bulletin_board"; }


}
