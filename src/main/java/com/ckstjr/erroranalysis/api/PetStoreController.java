package com.ckstjr.erroranalysis.api;

import com.ckstjr.erroranalysis.client.PetStoreClient;
import com.ckstjr.erroranalysis.dto.PetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Petstore API", description = "Swagger Petstore API를 이용함")
@RestController
@RequestMapping("/petstore")
@RequiredArgsConstructor
public class PetStoreController {

    private final PetStoreClient petStoreClient;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Petstore API 호출", description = "petId에 해당하는 펫을 조회합니다")
    public PetResponse findPet(@PathVariable Long petId) {
        return petStoreClient.findPet(petId);
    }

}

