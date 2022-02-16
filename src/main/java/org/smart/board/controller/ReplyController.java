package org.smart.board.controller;

import org.smart.board.entity.Reply;
import org.smart.board.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    ReplyService replyService;

    @PostMapping("/replyList")
    @ResponseBody
    public List<Reply> replyList(Long boardseq) {

        List<Reply> replyList = replyService.list(boardseq);
        return replyList;
    }

    @PostMapping("/replyWrite")
    @ResponseBody
    public String replyWrite(Reply reply, @AuthenticationPrincipal UserDetails user) {
        String loginId = user.getUsername();
        reply.setUsrid(loginId);
        System.out.println("=========================" + reply);
        int result = replyService.insert(reply);

        String message = "";
        if(result  == 1) message = "댓글을 저장했습니다.";
        else if(result == 0) message = "댓글을 저장하지 못했습니다.";

        return message;
    }
    @PostMapping("/replyDelete")
    @ResponseBody
    public String replyDelete(Reply reply, @AuthenticationPrincipal UserDetails user) { // 로그인한 사람의 아이디와, 댓글 쓴 사람의 아이디가 같을 때만 삭제
        System.out.println("삭제할 데이터" + reply);

        String loginId = user.getUsername();
        Reply r = replyService.findOne(reply.getReplyseq());

        String message = "";
        if(!(loginId.equals(r.getUsrid()))) {
            message = "본인이 쓸 글만 삭제가 돼";
        } else {
            int result = replyService.delete(reply.getReplyseq());
            if(result == 1) message = "삭제되었습니다";
        }

        return message;
    }
}
