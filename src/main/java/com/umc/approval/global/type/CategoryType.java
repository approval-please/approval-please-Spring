package com.umc.approval.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    DIGITAL(0, "디지털기기"),
    HOUSE_APPLIANCE(1, "생활가전"),
    HOUSE_GOODS(2, "생활용품"),
    FURNITURE_INTERIOR(3, "가구/인테리어"),
    KITCHEN_HEALTH(4, "주방/건강"),
    DELIVERY_CHILD(5, "출산/유아동"),
    FASHION_ACCESSORY(6, "패션의류/잡화"),
    BEAUTY(7, "뷰티/미용"),
    SPORTS(8, "스포츠/헬스/레저"),
    HOBBY(9, "취미/게임/완구"),
    OFFICE(10, "문구/오피스"),
    BOOK_MUSIC(11, "도서/음악"),
    TICKET(12, "티켓/교환권"),
    FOOD(13, "식품"),
    ANIMAL_PLANT(14, "동물/식물"),
    MOVIE_SHOW(15, "영화/공연"),
    CAR_TOOL(16, "자동차/공구"),
    OTHER(17, "기타 물품");

    private final int value;
    private final String category;
}
