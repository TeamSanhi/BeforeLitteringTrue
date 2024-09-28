package net.datasa.nanum.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.MemberDTO;
import net.datasa.nanum.domain.entity.ImageEntity;
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
     *
     * @param dto 회원가입 입력 정보
     */
    public void join(MemberDTO dto) {

        // Entity에 dto에 담겨있는 값을 담음
        MemberEntity memberEntity = MemberEntity.builder()
                .memberId(dto.getMemberId())
                .memberPw(passwordEncoder.encode(dto.getMemberPw()))
                .memberEmail(dto.getMemberEmail())
                .memberNickname(dto.getMemberNickname())
                .memberFileName("/images/profile-basic.png")
                .roleName("ROLE_USER")
                .memberStatus(0)
                .build();

        log.debug("DB에 저장되는 값 : {}", memberEntity);

        // DB에 저장
        memberRepository.save(memberEntity);

    }

    /**
     * 멤버 일련번호로 멤버 엔티티 탐색
     *
     * @param memberNum 멤버 일련번호
     * @return 멤버 엔티티
     */
    public MemberEntity getMemberByNum(Integer memberNum) {
        MemberEntity member = memberRepository.findById(memberNum).orElse(null);

        return member;
    }

    /**
     * 패스워드 확인
     * 
     * @param userNum   현재 사용자의 일련번호
     * @param enteredPw 입력된 패스워드 값
     * @return 패스워드 일치 여부
     */
    public Boolean checkPassword(Integer userNum, String enteredPw) {
        MemberEntity member = memberRepository.findById(userNum).orElse(null);

        assert member != null;

        return passwordEncoder.matches(enteredPw, member.getMemberPw());
    }

    /**
     * 멤버 탈퇴
     * 
     * @param userNum 현재 사용자의 일련번호
     * @return 탈퇴 여부
     */
    public Boolean deleteMember(Integer userNum) {
        MemberEntity member = memberRepository.findById(userNum).orElse(null);

        if (member != null) {
            member.setMemberStatus(2);
            member.setQuitDate(LocalDateTime.now());
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    public void modify(MemberDTO dto) {

        MemberEntity entity = memberRepository.findByMemberIdEquals(dto.getMemberId());

        if (entity != null) {
            entity.setMemberPw(passwordEncoder.encode(dto.getMemberPw()));
            entity.setMemberEmail(dto.getMemberEmail());
            entity.setMemberNickname(dto.getMemberNickname());
            entity.setMemberFileName(dto.getMemberFileName());
            memberRepository.save(entity);
        }

        log.debug("DB에 저장되는 값 : {}", entity);

    }

    /**
     * imageRepository에서 이미지를 다운로드 하거나 보여주는 함수
     */
    public void profileDownload(Integer memberNum, HttpServletResponse response, String uploadPath) {

        // 전달된 글 번호로 member테이블에서 회원정보 조회
        MemberEntity memberEntity = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // response setHeader 설정
        response.setHeader("Content-Disposition", "attachment;filename=" + memberEntity.getMemberFileName());

        // 저장된 파일 경로와 파일 이름 합한다.
        String fullPath = uploadPath + "/" + memberEntity.getMemberFileName();

        // 서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
        FileInputStream filein = null;
        ServletOutputStream fileout = null;

        try {
            filein = new FileInputStream(fullPath);
            fileout = response.getOutputStream();

            // Spring의 파일 관련 유틸 이용하여 출력
            FileCopyUtils.copy(filein, fileout);

            filein.close();
            fileout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
