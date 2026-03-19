package com.ckstjr.erroranalysis.analyzer.methodsignature;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@RequiredArgsConstructor
public class MethodSignature {
    private final String className;
    private final int lineNumber;
    private final List<ParameterInfo> parameters;
    private final String returnType;
}
