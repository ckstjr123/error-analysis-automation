package com.ckstjr.erroranalysis.client;

import com.ckstjr.erroranalysis.dto.PetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "petStoreClient", url = "https://petstore3.swagger.io/api/v3")
public interface PetStoreClient {

    @GetMapping("/pet/{petId}")
    PetResponse findPet(@PathVariable("petId") Long petId);
}
