package org.smart.board.service;

import org.smart.board.dao.MemberDao;
import org.smart.board.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberDao memberDao;

    @Override
    public int insertMember(Member member) {
        String encodedPwd = passwordEncoder.encode(member.getUsrpwd());
        member.setUsrpwd(encodedPwd);

        int result = memberDao.insertMember(member);

        return result;
    }

    @Override
    public Member selectMember(Member member) {
        String encodedPwd = passwordEncoder.encode(member.getUsrpwd()); // 비밀번호 암호화
        member.setUsrpwd(encodedPwd); // 암호화한 비밀번호를 멤버객체에 넣음

        Member m = memberDao.selectMember(member);

        return m;
    }
}
