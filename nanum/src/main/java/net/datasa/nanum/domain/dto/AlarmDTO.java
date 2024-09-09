package net.datasa.nanum.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    private Integer alarmNum;
    private Integer memberNum;
    private Integer alarmDay;
    private String alarmContents;
}
