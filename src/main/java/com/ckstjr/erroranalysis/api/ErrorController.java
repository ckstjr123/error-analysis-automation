package com.ckstjr.erroranalysis.api;

import com.ckstjr.erroranalysis.service.ErrorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ex")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorService errorService;

    @GetMapping("/1")
    @Operation(summary = "런타임 예외 명시적 발생", description = "단순 RuntimeException을 의도적으로 발생시킵니다.")
    public String throwRuntimeException() {
        throw new RuntimeException("명시적으로 발생시킨 런타임 예외입니다.");
    }

    @GetMapping("/2")
    @Operation(summary = "잘못된 인자 예외 명시적 발생", description = "요청 파라미터나 상태가 잘못되었을 때 사용되는 IllegalArgumentException을 throw 합니다.")
    public void throwIllegalArgumentException() {
        throw new IllegalArgumentException("잘못된 인자가 전달되었습니다.");
    }

    @GetMapping("/3")
    @Operation(summary = "잘못된 상태 예외 명시적 발생", description = "메서드를 호출하기에 적합하지 않은 상태일 때 사용되는 IllegalStateException을 throw 합니다.")
    public void throwIllegalStateException() {
        throw new IllegalStateException("현재 시스템이 요청을 처리할 수 없는 상태입니다.");
    }

    @GetMapping("/4")
    @Operation(summary = "널 포인터 예외 코드 발생", description = "null 객체에 접근하여 NullPointerException을 유발합니다.")
    public String throwNullPointerException() {
        Object nullObject = null;
        return nullObject.toString();
    }

    @GetMapping("/5")
    @Operation(summary = "산술 연산 예외 코드 발생", description = "0으로 나누기를 시도하여 ArithmeticException을 유발합니다.")
    public String throwArithmeticException() {
        int num = 100 / 0;
        return "ok";
    }

    @GetMapping("/6")
    @Operation(summary = "인덱스 범위 초과 예외 발생", description = "비어있는 리스트에 접근해 IndexOutOfBoundsException을 발생시킵니다.")
    public String throwIndexOutOfBoundsException() {
        return errorService.triggerIndexOutOfBoundsException(0);
    }

    @GetMapping("/7")
    @Operation(summary = "지원하지 않는 연산 예외 발생", description = "ErrorService를 호출하여 지원하지 않는 기능임을 알리는 UnsupportedOperationException을 발생시킵니다.")
    public String throwUnsupportedOperationException() {
        return errorService.triggerUnsupportedOperationException();
    }

    @GetMapping("/8/{id}")
    @Operation(summary = "메서드 인자 타입 불일치 예외 발생", description = "경로 변수(PathVariable)에 숫자 대신 문자를 입력하면 MethodArgumentTypeMismatchException을 발생합니다. (예: /ex/7/abc)")
    public String throwMethodArgumentTypeMismatchException(@PathVariable Integer id) {
        return "요청 파라미터 id: " + id;
    }

}
