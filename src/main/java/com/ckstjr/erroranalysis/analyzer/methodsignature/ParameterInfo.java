package com.ckstjr.erroranalysis.analyzer.methodsignature;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ParameterInfo {
    private final String name;
    private final String type;
}
