package net.datasa.nanum.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.MemberRepository;

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
                                    
        memberRepository.save(memberEntity);                            

    }


    
}
