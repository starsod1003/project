package org.smart.board.service;

import org.smart.board.entity.Reply;

import java.util.List;

public interface ReplyService {
    public List<Reply> list(Long boardseq);
    public int insert(Reply reply);
    public Reply findOne(Long replyseq);
    public int delete(Long replyseq);
}
