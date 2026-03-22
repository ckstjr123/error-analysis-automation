package com.ckstjr.erroranalysis.analyzer.methodsignature;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ASM 라이브러리를 이용하여 런타임에 바이트코드를 분석하고,
 * 메서드의 파라미터 이름, 타입, 반환 타입을 추출하는 역할을 담당합니다.
 */
@Slf4j
@Component
public class MethodSignatureParser {

    protected static final int UNKNOWN_LINE_NUMBER = -1;
    protected static final String ARG = "arg";
    private static final MethodSignature EMPTY = new MethodSignature("", UNKNOWN_LINE_NUMBER, new ArrayList<>(), "");

    /**
     * 필터링된 스택 트레이스 요소 목록을 받아 각각의 메서드 시그니처 정보를 추출합니다.
     */
    public List<MethodSignature> parse(List<StackTraceElement> stackTraceElements) {
        return stackTraceElements.stream()
                .map(stackTrace ->
                    parse(stackTrace.getClassName(), stackTrace.getLineNumber())
                )
                .filter(methodSignature ->
                        Objects.nonNull(methodSignature) && methodSignature != EMPTY)
                .collect(Collectors.toList());
    }

    /**
     * 특정 클래스와 라인 번호를 기반으로 해당 라인이 포함된 메서드의 시그니처를 추출합니다.
     */
    public MethodSignature parse(String className, int lineToFind) {
        InputStream classInputStream = MethodSignatureParser.class.getClassLoader()
                .getResourceAsStream(className.replace('.', '/') + ".class");

        ClassNode classNode = new ClassNode();
        try {
            ClassReader classReader = new ClassReader(classInputStream);
            classReader.accept(classNode, 0);
        } catch (IOException ex) {
            log.error("클래스 파일의 스트림을 읽고 분석하는 중 예외가 발생했습니다.", ex);
            return EMPTY;
        }

        for (MethodNode method : classNode.methods) {
            List<Integer> lineNumbers = new ArrayList<>();

            for (var instruction : method.instructions) {
                if (instruction instanceof LineNumberNode lineNumberNode) {
                    lineNumbers.add(lineNumberNode.line);
                }
            }

            if (lineNumbers.contains(lineToFind)) {
                return extractMethodSignature(className, lineToFind, method);
            }
        }

        return EMPTY;
    }

    /**
     * 찾은 MethodNode에서 파라미터 이름/타입과 반환 타입을 추출합니다.
     * (접근 제어자를 package-private으로 두어 같은 패키지의 테스트 코드에서 직접 검증할 수 있도록 합니다)
     */
    MethodSignature extractMethodSignature(String className, int lineNumber, MethodNode method) {
        Type[] argumentTypes = Type.getArgumentTypes(method.desc);

        // 메서드가 static인지 확인합니다. 인스턴스 메서드인 경우 첫번째 로컬 변수가 항상 this 객체이므로 1개를 건너뛰고(dropCount = 1),
        // static 메서드인 경우 this가 없으므로 건너뛰지 않습니다(dropCount = 0).
        boolean isStatic = (method.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0;
        int dropCount = isStatic ? 0 : 1;

        List<String> parameterNames = method.localVariables == null 
                ? List.of()
                : method.localVariables.stream()
                    .skip(dropCount)
                    .limit(argumentTypes.length)
                    .map(LocalVariableNode -> LocalVariableNode.name)
                    .toList();

        List<ParameterInfo> parameters = new ArrayList<>();
        for (int i = 0; i < argumentTypes.length; i++) {
            String name = (i < parameterNames.size()) ? parameterNames.get(i) : ARG + i;
            parameters.add(new ParameterInfo(argumentTypes[i].getClassName(), name));
        }

        return new MethodSignature(
                className,
                lineNumber,
                parameters,
                Type.getReturnType(method.desc).getClassName()
        );
    }

}