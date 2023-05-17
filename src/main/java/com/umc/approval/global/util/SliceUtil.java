package com.umc.approval.global.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class SliceUtil {

    public static <T> Slice<T> slice(List<T> contents, Pageable pageable) {
        boolean next = hasNext(contents, pageable);
        return new SliceImpl<>(next ? getOriginalList(contents, pageable) : contents, pageable, next);
    }

    private static <T> boolean hasNext(List<T> contents, Pageable pageable) {
        return pageable.isPaged() && contents.size() > pageable.getPageSize();
    }

    private static <T> List<T> getOriginalList(List<T> contents, Pageable pageable) {
        return contents.subList(0, pageable.getPageSize());
    }
}
