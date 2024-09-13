package net.datasa.nanum.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.MemberEntity;
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
    public void reportBoard(Integer shareNum, Integer memberNum) {

        // 신고한 게시글 찾기
        ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
        // 신고한 회원 찾기
        MemberEntity member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

    }

}
