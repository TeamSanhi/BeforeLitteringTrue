package net.datasa.nanum.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ReportBoardEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.ReportBoardRepository;
import net.datasa.nanum.repository.ShareBoardRepository;

/**
 * 게시글 신고기능을 처리하는 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReportBoardService {

    // 멤버 리퍼지토리 사용
    private final MemberRepository memberRepository;
    // 게시글 리퍼지토리 사용
    private final ShareBoardRepository shareBoardRepository;
    // 게시글 신고 리퍼지토리 사용
    private final ReportBoardRepository reportBoardRepository;

    /**
     * 게시글 신고 함수
     * 
     * @param shareNum
     */
    public Integer reportBoard(Integer shareNum, Integer memberNum) {

        // 신고한 게시글 찾기
        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        // 신고한 회원 찾기
        MemberEntity member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 회원이 신고한 게시글 찾기
        Optional<ReportBoardEntity> existingReport = reportBoardRepository.findByMemberAndShareBoard(member,
                shareBoard);

        // 신고테이블에 회원이 신고한 게시글이 없을 시 shareBoard 신고카운트 + 1
        if (!existingReport.isPresent()) {
            // 게시글이 없으면 shareBoard의 reportCount + 1
            shareBoard.setReportCount(shareBoard.getReportCount() + 1);
            // 신고당한 게시글을 reportBoard 에 entity 를 만들어 넣어준다.
            ReportBoardEntity reportBoard = ReportBoardEntity.builder()
                    .member(member)
                    .shareBoard(shareBoard)
                    .build();
            // 신고당한 글 reportEntity를 저장
            reportBoardRepository.save(reportBoard);
        } else {
            // 게시글 중복신고 불가 메시지
            return 0;
        }

        // 신고카운트 수가 3 달성하면 게시글 삭제
        if (shareBoard.getReportCount() >= 3) {
            // 게시글을 삭제
            shareBoardRepository.delete(shareBoard);
            // true 값을 리턴
            return 1;
        } else {
            // false 값을 리턴
            return 2;
        }

    }

}
