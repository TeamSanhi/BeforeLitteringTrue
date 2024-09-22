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

    /**
     * 알람 추가
     * @param memberNum 멤버 엔티티
     * @param alarmDay 알람 요일
     * @param alarmContents 알람 내용
     * @return 알람 추가 여부
     */
    public Boolean alarmEdit(MemberEntity memberNum, Integer alarmDay, String alarmContents) {
        Boolean result = false;

        AlarmEntity alarmEntity = new AlarmEntity();

        alarmEntity.setMemberNum(memberNum);
        alarmEntity.setAlarmDay(alarmDay);
        alarmEntity.setAlarmContents(alarmContents);

        alarmRepository.save(alarmEntity);

        result = true;

        return result;
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
