package net.datasa.nanum.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer messageNum;
    private Integer giverNum;
    private Integer receiverNum;
    private Integer shareNum;
    private String messageContents;
    private LocalDateTime deliverDate;
}
