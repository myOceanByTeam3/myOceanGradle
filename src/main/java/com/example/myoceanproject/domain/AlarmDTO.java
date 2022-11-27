package com.example.myoceanproject.domain;

import com.example.myoceanproject.entity.Alarm;
import com.example.myoceanproject.entity.Period;
import com.example.myoceanproject.entity.User;
import com.example.myoceanproject.type.ReadStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
public class AlarmDTO {

    private User user; //FK
    private String alarmContent;
    private LocalDateTime alarmDate;

//  ReadStatus는 UNREAD가 디폴트 값으로 저장되고, 이후 READ로 업데이트가 되기 때문에 DTO에서 값을 저장하지 않는다.
    public Alarm toEntity(){
        return Alarm.builder()
                .user(user)
                .alarmContent(alarmContent)
                .alarmDate(alarmDate)
                .build();
    }

}
