package net.datasa.nanum.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.entity.BookMarkEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.domain.entity.ShareBoardEntity;
import net.datasa.nanum.repository.BookMarkRepository;
import net.datasa.nanum.repository.MemberRepository;
import net.datasa.nanum.repository.ShareBoardRepository;

/**
 * 북마크 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookMarkService {

        // 북마크 리퍼지토리에서 테이블 다룸
        private final BookMarkRepository bookMarkRepository;
        // 멤버 리퍼티조리 사용
        private final MemberRepository memberRepository;
        // 게시글 리퍼지토리 참조
        private final ShareBoardRepository shareBoardRepository;

        /**
         * 북마크 처리하는 서비스
         * 
         * @param shareNum  넘겨받은 게시글 번호
         * @param memberNum 로그인한 유저 넘버
         */
        public void bookmark(Integer shareNum, Integer memberNum) {
                // 게시글 존재유무 검색
                MemberEntity member = memberRepository.findById(memberNum)
                                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
                // 로그인 존재유무 검색
                ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
                // 북마크 entity 생성
                BookMarkEntity bookMarkEntity = BookMarkEntity.builder()
                                .member(member)
                                .shareBoard(shareBoard)
                                .build();

                // 북마크 엔티티를 리퍼지토리에 저장
                bookMarkRepository.save(bookMarkEntity);
        }

}
