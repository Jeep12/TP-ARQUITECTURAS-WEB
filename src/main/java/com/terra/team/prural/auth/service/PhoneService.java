package com.terra.team.prural.auth.service;

import com.terra.team.prural.auth.dto.phone.AddPhoneRequest;
import com.terra.team.prural.auth.dto.phone.PhoneResponse;
import java.util.List;

public interface PhoneService {
    
    PhoneResponse addPhone(Long userId, AddPhoneRequest request);
    
    boolean deletePhone(Long userId, Long phoneId);
    
    List<PhoneResponse> getUserPhones(Long userId);
    
    PhoneResponse getPhone(Long userId, Long phoneId);
}
