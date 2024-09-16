package net.datasa.nanum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.nanum.domain.dto.AlarmDTO;
import net.datasa.nanum.domain.entity.AlarmEntity;
import net.datasa.nanum.domain.entity.MemberEntity;
import net.datasa.nanum.repository.AlarmRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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

}
