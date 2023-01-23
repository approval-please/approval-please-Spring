package com.umc.approval.global.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;

    public static String convert(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        long curTime = now.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        long regTime = date.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        long diffTime = (curTime - regTime) / 1000;

        // 작성일이 지났을 때
        if (!now.toLocalDate().isEqual(date.toLocalDate())) {
            return DateTimeFormatter.ofPattern("yy/MM/dd HH:mm").format(date);
        }

        String msg = null;

        if (diffTime < SEC) {
            msg = "1분 전";
        } else if ((diffTime /= SEC) < MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            msg = diffTime + "시간 전";
        }

        return msg;
    }
}
