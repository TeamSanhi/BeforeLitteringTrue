package net.datasa.nanum.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 북마크 서비스
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookMarkService {

    /**
     * 북마크 처리하는 서비스
     * 
     * @param shareNum 넘겨받은 게시글 번호
     * @param username 로그인한 유저아이디
     */
    public void bookmark(Integer shareNum, Integer memberNum) {
        // TODO Auto-generated method stub
    }

}
