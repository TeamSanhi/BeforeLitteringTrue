package net.datasa.nanum.Controller.Message;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ReportEntity;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.ReportRepository;

@Slf4j
@RequestMapping("report")
@RestController
@RequiredArgsConstructor
public class ReportRestController {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @PostMapping("submit")
    public ResponseEntity<String> submitReport(@RequestParam("memberNum") int memberNum,
            @RequestParam("reporterNum") int reporterNum,
            @RequestParam("reportReason") String reportReason,
            @RequestParam("additionalReason") String additionalReason,
            @RequestParam("shareNum") int shareNum) {
        log.debug("신고 대상: {}", memberNum);
        log.debug("신고자: {}", reporterNum);
        log.debug("신고 사유: {}", reportReason);
        log.debug("기타 사유: {}", additionalReason);

        try {
            List<String> reasons = new ObjectMapper().readValue(reportReason, new TypeReference<List<String>>() {
            });

            MemberEntity member = memberRepository.findById(memberNum)
                    .orElseThrow(() -> new EntityNotFoundException("해당 신고 대상 정보를 찾을 수 없습니다."));

            MemberEntity reporter = memberRepository.findById(reporterNum)
                    .orElseThrow(() -> new EntityNotFoundException("신고자 정보를 찾을 수 없습니다."));

            // 회원이 신고한 유저 찾기
            Optional<ReportEntity> isReported = reportRepository.findByMemberAndReporter(member, reporter);

            // 현재 로그인한 회원이 해당 회원을 신고한 적이 없다면
            if (!isReported.isPresent()) {
                // 신고 내용을 DB에 저장
                ReportEntity report = ReportEntity.builder()
                        .member(member)
                        .reporter(reporter)
                        .reportReason(String.join(", ", reasons)
                                + (additionalReason.isEmpty() ? "" : ": " + additionalReason))
                        .build();

                reportRepository.save(report);

                return ResponseEntity.ok("신고가 접수되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 해당 유저를 신고하였습니다.");
            }
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 신고 사유 형식입니다.");
        }
    }
}
