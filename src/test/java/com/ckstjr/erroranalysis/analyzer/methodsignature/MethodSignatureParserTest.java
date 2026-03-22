package com.ckstjr.erroranalysis.analyzer.methodsignature;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static com.ckstjr.erroranalysis.analyzer.methodsignature.MethodSignatureParser.ARG;
import static com.ckstjr.erroranalysis.analyzer.methodsignature.MethodSignatureParser.UNKNOWN_LINE_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

class MethodSignatureParserTest {

    private final MethodSignatureParser parser = new MethodSignatureParser();

    // 1. 인스턴스 메서드 테스트를 위한 더미 클래스
    private static class DummyInstanceClass {
        public Long instanceMethod(Long id, String text) {
            return id;
        }
    }

    // 2. 정적(static) 메서드 테스트를 위한 더미 클래스
    private static class DummyStaticClass {
        public static Object staticMethod(Object object, boolean flag) {
            return object;
        }
    }

    // 3. 로컬 변수 테이블이 없는 상황 테스트를 위한 더미 인터페이스
    private interface DummyInterface {
        void method(String str, int count);
    }

    @Test
    @DisplayName("스택 트레이스 요소 리스트를 전달하면 해당 라인의 인스턴스 메서드 시그니처 목록 추출")
    void parse_WithStackTraceElements_ReturnsMethodSignatures() {
        String className = DummyInstanceClass.class.getName();

        for (Method method : getDeclaredMethods(DummyInstanceClass.class)) {
            String methodName = method.getName();
            int lineNumber = getFirstLineNumberOfMethod(method);
            ParameterInfo[] parameterInfos = createParameterInfos(method);
            String returnType = method.getReturnType().getName();

            StackTraceElement stackTraceElement = new StackTraceElement(
                    className,
                    methodName,
                    "Test.java",
                    lineNumber
            );

            // when
            List<MethodSignature> signatures = parser.parse(List.of(stackTraceElement));

            // then
            assertThat(signatures)
                    .singleElement()
                    .satisfies(signature -> {
                        assertThat(signature.getClassName()).isEqualTo(className);
                        assertThat(signature.getReturnType()).isEqualTo(returnType);

                        assertThat(signature.getParameters())
                                .containsExactly(parameterInfos);
                    });
        }
    }

    @Test
    @DisplayName("정적(Static) 메서드 시그니처 추출 시 this를 건너뛰지 않고 첫 파라미터부터 정확히 추출해야 함")
    void parse_WithStaticMethod_ReturnsCorrectSignature() {
        String className = DummyStaticClass.class.getName();

        for (Method method : getDeclaredMethods(DummyStaticClass.class)) {
            String methodName = method.getName();
            int lineNumber = getFirstLineNumberOfMethod(method);
            ParameterInfo[] parameterInfos = createParameterInfos(method);
            String returnType = method.getReturnType().getName();

            StackTraceElement stackTraceElement = new StackTraceElement(
                    className,
                    methodName,
                    "Test.java",
                    lineNumber
            );

            // when
            List<MethodSignature> signatures = parser.parse(List.of(stackTraceElement));

            // then
            assertThat(signatures)
                    .singleElement()
                    .satisfies(signature -> {
                        assertThat(signature.getReturnType()).isEqualTo(returnType);

                        assertThat(signature.getParameters())
                                .containsExactly(parameterInfos);
                    });
        }
    }

    @Test
    @DisplayName("존재하지 않는 클래스 이름이 주어지면 Null Object 반환")
    void parse_WithInvalidClassName_ReturnsEmptySignature() {
        // given
        String invalidClassName = "com.test.NonExistentClass";

        // when
        MethodSignature signature = parser.parse(invalidClassName, 10);

        // then
        // MethodSignatureParser 내부에 정의된 EMPTY 객체와 일치하는 상태인지 검증
        assertThat(signature.getClassName()).isEmpty();
        assertThat(signature.getLineNumber()).isEqualTo(UNKNOWN_LINE_NUMBER);
        assertThat(signature.getParameters()).isEmpty();
        assertThat(signature.getReturnType()).isEmpty();
    }

    @Test
    @DisplayName("클래스는 존재하지만 주어진 라인 번호가 메서드 범위 밖인 경우 Null Object 반환")
    void parse_WithLineNumberOutsideMethodScope_ReturnsEmptySignature() {
        // given
        String className = DummyInstanceClass.class.getName();
        int invalidLineNumber = 999999; // 존재할 수 없는 라인 번호

        // when
        MethodSignature signature = parser.parse(className, invalidLineNumber);

        // then
        assertThat(signature.getClassName()).isEmpty();
        assertThat(signature.getLineNumber()).isEqualTo(UNKNOWN_LINE_NUMBER);
        assertThat(signature.getParameters()).isEmpty();
    }

    @Test
    @DisplayName("로컬 변수 테이블이 없는 경우 파라미터 이름이 arg0, arg1과 같은 형태로 대체")
    void parse_WithoutLocalVariableTable_FallbacksToArgPrefix() {
        String className = DummyInterface.class.getName();

        for (Method method : getDeclaredMethods(DummyInterface.class)) {

            Parameter[] parameters = method.getParameters();
            ParameterInfo[] parameterInfos = new ParameterInfo[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterInfos[i] = new ParameterInfo(parameters[i].getType().getName(), ARG + i);
            }

            // when
            MethodSignature signature = parser.extractMethodSignature(className, UNKNOWN_LINE_NUMBER, parseMethodNode(method));

            // then
            // 로컬 변수 테이블이 없으므로 작성한 Fallback 로직에 의해 arg0, arg1이 부여되어야 함
            // Reflection으로 알아낸 파라미터 사이즈 및 타입 정보를 바탕으로 검증
            assertThat(signature.getParameters())
                    .containsExactly(parameterInfos);
        }
    }


    /**
     * 테스트 대상 메서드 목록을 가져오는 헬퍼 메서드 (Synthetic 메서드 제외)
     */
    private List<Method> getDeclaredMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .toList();
    }

    /**
     * 리플렉션 Method 객체를 통해 ASM의 MethodNode를 추출하는 헬퍼 메서드
     */
    private MethodNode parseMethodNode(Method method) {
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        InputStream inputStream = MethodSignatureParser.class.getClassLoader()
                .getResourceAsStream(className.replace('.', '/') + ".class");
        ClassNode classNode = new ClassNode();
        try {
            new ClassReader(inputStream).accept(classNode, 0);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return classNode.methods.stream()
                .filter(methodNode -> methodNode.name.equals(methodName))
                .findFirst()
                .orElseThrow();
    }

    /**
     * 리플렉션 Method 객체를 통해 첫 번째 라인 번호를 ASM으로 찾아내는 헬퍼 메서드
     */
    private int getFirstLineNumberOfMethod(Method method) {
        MethodNode methodNode = parseMethodNode(method);
        if (methodNode != null) {
            for (var instruction : methodNode.instructions) {
                if (instruction instanceof LineNumberNode lineNumberNode) {
                    return lineNumberNode.line;
                }
            }
        }
        return UNKNOWN_LINE_NUMBER;
    }

    /**
     * 리플렉션 Method 객체를 이용하여 ParameterInfo 배열을 생성하여 반환하는 공통 헬퍼 메서드
     */
    private ParameterInfo[] createParameterInfos(Method method) {
        Parameter[] parameters = method.getParameters();
        ParameterInfo[] parameterInfos = new ParameterInfo[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterInfos[i] = new ParameterInfo(parameters[i].getType().getName(), parameters[i].getName());
        }
        return parameterInfos;
    }

}
