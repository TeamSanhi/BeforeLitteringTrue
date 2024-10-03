package net.datasa.nanum.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.MemberRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FindService {
    // memberRepository 생성자 주입
    private final MemberRepository memberRepository;
    // bcrypt password encoder 사용
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 입력한 아이디가 DB에 존재하는지 확인
     * 
     * @param memberId
     * @return
     */
    public boolean isIdAvailable(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    /**
     * 입력한 닉네임이 DB에 존재하는지 확인
     * 
     * @param memberNickname
     * @return
     */
    public boolean isNicknameAvailable(String memberNickname) {
        return memberRepository.existsByMemberNickname(memberNickname);
    }

    /**
     * 비밀번호 변경
     * 
     * @param memberEmail
     * @param memberPw
     */
    public boolean pwFind(String memberEmail, String memberPw) {

        // email로 멤버 entity 찾기
        MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);

        if (memberEntity != null) {
            // 비밀번호 변경
            memberEntity.setMemberPw(bCryptPasswordEncoder.encode(memberPw));
            // entity에 저장
            memberRepository.save(memberEntity);

            // 비밀번호가 변경되어 true 반환
            return true;
        }

        return false;
    }
}
