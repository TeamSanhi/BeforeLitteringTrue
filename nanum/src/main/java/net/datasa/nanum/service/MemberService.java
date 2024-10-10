package net.datasa.nanum.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                .memberFileName("defaultImage.png")
                .roleName("ROLE_USER")
                .memberStatus(0)
                .memberPoint(0)
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
            if (dto.getMemberPw().equals("")) {
                entity.setMemberEmail(dto.getMemberEmail());
                entity.setMemberNickname(dto.getMemberNickname());
            } else {
                entity.setMemberPw(passwordEncoder.encode(dto.getMemberPw()));
                entity.setMemberEmail(dto.getMemberEmail());
                entity.setMemberNickname(dto.getMemberNickname());
            }
            memberRepository.save(entity);
        }

        log.debug("DB에 저장되는 값 : {}", entity);

    }

    /**
     * imageRepository에서 이미지를 다운로드 하거나 보여주는 함수
     */
    public void profileDownload(Integer memberNum, HttpServletResponse response, String uploadPath) {

        // 회원 정보 조회
        MemberEntity memberEntity = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        log.debug("profileDownload memberEntity : {}", memberEntity);

        String fileName = memberEntity.getMemberFileName();
        String fullPath = Paths.get(uploadPath, fileName).toString();

        File file = new File(fullPath);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 원본 파일명 추출 (UUID 제거)
        String originalFileName = fileName.substring(fileName.indexOf("_") + 1);

        // URL 인코딩 (RFC 5987)
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백은 %20으로 인코딩

        // ASCII-only 파일명 생성 (비ASCII 문자는 '_'로 대체)
        String asciiFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        // Content-Disposition 헤더 설정
        String contentDisposition = "attachment; filename=\"" + asciiFileName + "\"; filename*=UTF-8''"
                + encodedFileName;

        response.setHeader("Content-Disposition", contentDisposition);

        // Content-Type 설정
        try {
            String contentType = Files.probeContentType(Paths.get(fullPath));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
        } catch (IOException e) {
            response.setContentType("application/octet-stream");
        }

        // 파일 크기 설정
        response.setContentLengthLong(file.length());

        // 파일 스트림 처리 (try-with-resources 사용)
        try (FileInputStream filein = new FileInputStream(file);
                ServletOutputStream fileout = response.getOutputStream()) {
            FileCopyUtils.copy(filein, fileout);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
