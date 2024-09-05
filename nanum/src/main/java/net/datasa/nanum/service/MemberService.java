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

    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public void join(MemberDTO dto) {
       
        MemberEntity memberEntity = MemberEntity.builder()
                                    .memberId(dto.getMemberId())
                                    .memberPw(passwordEncoder.encode(dto.getMemberPw()))
                                    .memberEmail(dto.getMemberEmail())
                                    .membeNickname(dto.getMemberNickname())
                                    .roleName("ROLE_USER")
                                    .memberStatus(0)
                                    .build();
                                    
        log.debug("DB에 저장되는 값 : {}", memberEntity);

        memberRepository.save(memberEntity);                            

    }

    public boolean idDuplicate(String memberId) {
        
        log.debug("ID가 DB에 존재하는지 여부: {}", memberRepository.existsByMemberId(memberId));

        return memberRepository.existsByMemberId(memberId);
    }

    public boolean nickDuplicate(String memberNickname) {
        log.debug("닉네임이 DB에 존재하는지 여부: {}", memberRepository.existsByMemberNickname(memberNickname));

        return memberRepository.existsByMemberNickname(memberNickname);
    }

    
    
}
