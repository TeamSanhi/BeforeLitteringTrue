package net.datasa.nanum.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindService {
    private final MemberRepository memberRepository;
        /**
     * 입력한 아이디가 DB에 존재하는지 확인
     * @param memberId  입력한 아이디
     * @return MemberController -> idCheck()
     */
    public boolean idDuplicate(String memberId) {
        
        log.debug("ID가 DB에 존재하는지 여부: {}", memberRepository.existsByMemberId(memberId));

        return memberRepository.existsByMemberId(memberId);
    }

    /**
    * 입력한 닉네임이 DB에 존재하는지 확인
    * @param memberNickname  입력한 닉네임
    * @return MemberController -> idCheck()
    */
    public boolean nickDuplicate(String memberNickname) {
        log.debug("닉네임이 DB에 존재하는지 여부: {}", memberRepository.existsByMemberNickname(memberNickname));

        return memberRepository.existsByMemberNickname(memberNickname);
    }
}
