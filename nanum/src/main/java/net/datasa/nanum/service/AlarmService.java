package net.datasa.nanum.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.entity.AlarmEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.AlarmRepository;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;

    private static final String[] daysOfWeek = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

    private Integer getDayIndex(String alarmDay) {
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i].equals(alarmDay)) {
                return i;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 요일입니다: " + alarmDay);
    }

    /**
     * 알림 리스트 entity에서 dto로 변환
     * @param member 멤버 엔티티
     * @return 알림 리스트 반환
     */
    public List<AlarmDTO> getAlarmList(MemberEntity member) {
        List<AlarmEntity> alarmEntities = alarmRepository.listAlarmByMemberNum(member);
        List<AlarmDTO> alarmDTOS = new ArrayList<>();

        for (AlarmEntity alarmEntity : alarmEntities) {
            AlarmDTO alarmDTO = new AlarmDTO();
            alarmDTO.setAlarmNum(alarmEntity.getAlarmNum());
            alarmDTO.setMemberNum(alarmEntity.getMemberNum().getMemberNum());
            alarmDTO.setAlarmDay(alarmEntity.getAlarmDay());
            alarmDTO.setAlarmContents(alarmEntity.getAlarmContents());
            alarmDTOS.add(alarmDTO);
        }
        return alarmDTOS;
    }

    public Boolean isAlarmExists(MemberEntity member, String alarmDay) {
        Integer alarmDayInt = getDayIndex(alarmDay);

        if(alarmRepository.findByMemberNumAndAlarmDay(member, alarmDayInt)!=null)
            return true;
        return false;
    }

    public Boolean alarmAdd (MemberEntity member, String alarmDay, String alarmContents) {
        Integer alarmDayInt = getDayIndex(alarmDay);

        AlarmEntity alarmEntity = new AlarmEntity();

        alarmEntity.setMemberNum(member);
        alarmEntity.setAlarmDay(alarmDayInt);
        alarmEntity.setAlarmContents(alarmContents);

        alarmRepository.save(alarmEntity);

        return true;
    }

    public Boolean alarmEdit (MemberEntity memberNum, String alarmDay, String alarmContents) {
        Integer alarmDayInt = getDayIndex(alarmDay);

        // 기존 알람 조회
        AlarmEntity alarmEntity = alarmRepository.findByMemberNumAndAlarmDay(memberNum, alarmDayInt);

        if (alarmEntity != null)
            alarmEntity.setAlarmContents(alarmContents);

        return true;
    }

    // 기존의 알람 수정
    public Map<String, Object> alarmChange(AlarmDTO alarmDTO) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 알람 정보를 가져옵니다.
            AlarmEntity alarmEntity = alarmRepository.findById(alarmDTO.getAlarmNum())
                .orElseThrow(() -> new EntityNotFoundException("알람 정보가 존재하지 않습니다."));
    
            log.debug("지금 가져온 알람: {}", alarmEntity);
            // 알람 정보를 수정합니다.
            alarmEntity.setAlarmDay(alarmDTO.getAlarmDay());
            alarmEntity.setAlarmContents(alarmDTO.getAlarmContents());
            alarmRepository.save(alarmEntity);  // 수정된 알람 저장
    
            // 성공 응답 설정
            response.put("alarmChange", true);
        } catch (EntityNotFoundException e) {
            // 알람이 존재하지 않는 경우
            response.put("alarmChange", false);
            response.put("message", e.getMessage());  // 에러 메시지 추가
        } catch (Exception e) {
            // 기타 예외 처리
            response.put("alarmChange", false);
            response.put("message", "알람 수정에 실패했습니다.");  // 기본 에러 메시지
        }
        
        return response;
    }

}
