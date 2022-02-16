package org.smart.board.dao;

import org.apache.ibatis.annotations.Mapper;
import org.smart.board.entity.Reply;

import java.util.List;

@Mapper
public interface ReplyDao {
    /*
    * 특정 boardseq에 해당하는 모든 댓글 목록 리턴
    * */
    public List<Reply> list(Long boardseq);

    /*
    * 댓글 등록
    * */
    public int insert(Reply reply);

    public Reply findOne(Long replyseq);
    public int delete(Long replyseq);
}
