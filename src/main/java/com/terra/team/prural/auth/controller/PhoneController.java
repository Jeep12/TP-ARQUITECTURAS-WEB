package com.terra.team.prural.auth.controller;

import com.terra.team.prural.auth.dto.phone.AddPhoneRequest;
import com.terra.team.prural.auth.dto.phone.PhoneResponse;
import com.terra.team.prural.auth.dto.phone.DeletePhoneResponse;
import com.terra.team.prural.auth.service.PhoneService;
import com.terra.team.prural.auth.service.UserContextService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
public class PhoneController {
    
    @Autowired
    private PhoneService phoneService;
    
    @Autowired
    private UserContextService userContextService;
    
    @PostMapping
    public ResponseEntity<PhoneResponse> addPhone(@Valid @RequestBody AddPhoneRequest request) {
        Long userId = userContextService.getCurrentUserId();
        PhoneResponse response = phoneService.addPhone(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{phoneId}")
    public ResponseEntity<DeletePhoneResponse> deletePhone(@PathVariable Long phoneId) {
        Long userId = userContextService.getCurrentUserId();
        phoneService.deletePhone(userId, phoneId);
        DeletePhoneResponse response = new DeletePhoneResponse(
            "Teléfono eliminado exitosamente", 
            true, 
            phoneId
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<PhoneResponse>> getUserPhones() {
        Long userId = userContextService.getCurrentUserId();
        List<PhoneResponse> phones = phoneService.getUserPhones(userId);
        return ResponseEntity.ok(phones);
    }
    
    @GetMapping("/{phoneId}")
    public ResponseEntity<PhoneResponse> getPhone(@PathVariable Long phoneId) {
        Long userId = userContextService.getCurrentUserId();
        PhoneResponse phone = phoneService.getPhone(userId, phoneId);
        return ResponseEntity.ok(phone);
    }
}
