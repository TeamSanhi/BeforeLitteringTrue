package net.datasa.nanum.service;

import java.util.Optional;

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
         * 북마크 추가/삭제 처리
         * 
         * @param shareNum  게시글 번호
         * @param memberNum 회원 번호
         * @return 북마크 상태 (true: 북마크 추가됨, false: 북마크 삭제됨)
         */
        public boolean bookmark(Integer shareNum, Integer memberNum) {
                // 회원 정보와 게시글 정보 확인
                MemberEntity member = memberRepository.findById(memberNum)
                                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
                ShareBoardEntity shareBoard = shareBoardRepository.findById(shareNum)
                                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

                // 북마크가 존재하는지 확인
                Optional<BookMarkEntity> existingBookmark = bookMarkRepository.findByMemberAndShareBoard(member,
                                shareBoard);

                if (existingBookmark.isPresent()) {
                        // 북마크가 존재하면 삭제
                        bookMarkRepository.delete(existingBookmark.get());
                        return false; // 삭제된 경우
                } else {
                        // 북마크가 없으면 추가
                        BookMarkEntity newBookmark = BookMarkEntity.builder()
                                        .member(member)
                                        .shareBoard(shareBoard)
                                        .build();
                        bookMarkRepository.save(newBookmark);
                        return true; // 추가된 경우
                }
        }

}
