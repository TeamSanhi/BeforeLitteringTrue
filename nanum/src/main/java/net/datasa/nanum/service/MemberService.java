package net.datasa.nanum.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    // 비밀번호 암호화
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    /**
     * 회원가입 창에서 입력한 정보를 dto로 받아와 entitiy에 담고 저장
     * @param dto   회원가입 입력 정보
     */
    public void join(MemberDTO dto) {
       
        // Entity에 dto에 담겨있는 값을 담음
        MemberEntity memberEntity = MemberEntity.builder()
                                    .memberId(dto.getMemberId())
                                    .memberPw(passwordEncoder.encode(dto.getMemberPw()))
                                    .memberEmail(dto.getMemberEmail())
                                    .memberNickname(dto.getMemberNickname())
                                    .roleName("ROLE_USER")
                                    .memberStatus(0)
                                    .build();
                                    
        log.debug("DB에 저장되는 값 : {}", memberEntity);

        // DB에 저장
        memberRepository.save(memberEntity);                            

    }

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
