package com.ckstjr.erroranalysis.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ErrorService {

    public String triggerIndexOutOfBoundsException(int idx) {
        List<String> list = new ArrayList<>();
        return list.get(idx);
    }

    public String triggerUnsupportedOperationException() {
        // 지원하지 않는 연산 예외를 의도적으로 발생시킵니다.
        throw new UnsupportedOperationException("이 기능은 아직 지원되지 않습니다.");
    }
}
