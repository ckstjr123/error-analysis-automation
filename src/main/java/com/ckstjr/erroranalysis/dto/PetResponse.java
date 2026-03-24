package com.ckstjr.erroranalysis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PetResponse {
    private final Long id;
    private final String name;
    private final String status;
}
