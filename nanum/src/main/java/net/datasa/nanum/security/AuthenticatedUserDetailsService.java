package net.datasa.nanum.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.MemberRepository;


/**
 * 사용자 인증 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("로그인 시도 : {}", id);

        MemberEntity memberEntity = memberRepository.findByMemberIdEquals(id);

        log.debug("조회정보 : {}", memberEntity);

        // 인증정보 생성
        AuthenticatedUser user = AuthenticatedUser.builder()
                .num(memberEntity.getMemberNum())
                .id(memberEntity.getMemberId())
                .password(memberEntity.getMemberPw())
                .email(memberEntity.getMemberEmail())
                .nickname(memberEntity.getMembeNickname())
                .roleName(memberEntity.getRoleName())
                .status(memberEntity.getMemberStatus())
                .build();

        return user;
    }
}
