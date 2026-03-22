package com.ckstjr.erroranalysis.analyzer.methodsignature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ParameterInfo {
    private final String type;
    private final String name;
}
