package com.terra.team.prural.auth.service;

import com.terra.team.prural.auth.dto.phone.AddPhoneRequest;
import com.terra.team.prural.auth.dto.phone.PhoneResponse;
import com.terra.team.prural.auth.entity.PhoneType;
import com.terra.team.prural.auth.entity.User;
import com.terra.team.prural.auth.entity.UserPhone;
import com.terra.team.prural.auth.repository.UserPhoneRepository;
import com.terra.team.prural.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PhoneServiceImpl implements PhoneService {
    
    @Autowired
    private UserPhoneRepository userPhoneRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public PhoneResponse addPhone(Long userId, AddPhoneRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        
        // Validar límite de teléfonos
        if (!user.canAddPhone()) {
            throw new RuntimeException("No se puede agregar más de 3 números de teléfono por usuario. Límite alcanzado.");
        }
        
        // Debug: Verificar si el request indica que será primario
        System.out.println("🔍 [DEBUG] Request isPrimary: " + request.getIsPrimary());
        
        // Crear nuevo teléfono
        UserPhone phone = new UserPhone();
        phone.setPhoneNumber(request.getPhoneNumber());
        phone.setPhoneType(request.getPhoneType());
        phone.setPrimary(request.getIsPrimary());
        phone.setUser(user);
        
        System.out.println("🔍 [DEBUG] Nuevo teléfono creado con isPrimary: " + phone.isPrimary());
        
        // Agregar teléfono al usuario
        user.addPhone(phone);
        
        // Guardar en base de datos
        UserPhone savedPhone = userPhoneRepository.save(phone);
        
        System.out.println("🔍 [DEBUG] Teléfono guardado con isPrimary: " + savedPhone.isPrimary());
        
        return new PhoneResponse(
                savedPhone.getId(),
                savedPhone.getPhoneNumber(),
                savedPhone.getPhoneType(),
                savedPhone.isPrimary(),
                savedPhone.getCreatedAt()
        );
    }
    
    @Override
    public boolean deletePhone(Long userId, Long phoneId) {
        UserPhone phone = userPhoneRepository.findByIdAndUserId(phoneId, userId)
                .orElseThrow(() -> new RuntimeException("No se encontró el teléfono"));
        
        User user = phone.getUser();
        user.removePhone(phone);
        
        userPhoneRepository.delete(phone);
        return true;
    }
    
    @Override
    public List<PhoneResponse> getUserPhones(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        
        List<UserPhone> phones = userPhoneRepository.findByUserId(userId);
        
        return phones.stream()
                .map(phone -> new PhoneResponse(
                        phone.getId(),
                        phone.getPhoneNumber(),
                        phone.getPhoneType(),
                        phone.isPrimary(),
                        phone.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public PhoneResponse getPhone(Long userId, Long phoneId) {
        UserPhone phone = userPhoneRepository.findByIdAndUserId(phoneId, userId)
                .orElseThrow(() -> new RuntimeException("No se encontró el teléfono"));
        
        return new PhoneResponse(
                phone.getId(),
                phone.getPhoneNumber(),
                phone.getPhoneType(),
                phone.isPrimary(),
                phone.getCreatedAt()
        );
    }
}
