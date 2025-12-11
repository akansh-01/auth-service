package com.project.auth.mapper;

import com.project.auth.dto.CreateUserDto;
import com.project.auth.entity.Audit;
import com.project.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationEntityMapper {

    private final String SYSTEM_USER = "SYSTEM";

    public User createUserEntity(CreateUserDto createUserDto) {

        User user = new User();
        user.setFirstname(createUserDto.getFirstname());
        user.setLastname(createUserDto.getLastname());
        user.setPassword(createUserDto.getPassword());
        user.setPhone(createUserDto.getPhone());
        user.setEmail(createUserDto.getEmail());
        user.setRole(createUserDto.getRole());
        setAuditFields(user,createUserDto.getFirstname()+" "+createUserDto.getLastname());
        return user;
    }


    public void setAuditFields(Object entity, String username) {
        LocalDateTime now = LocalDateTime.now();
        String modifiedBy =  username==null ? SYSTEM_USER : username;

        ((Audit) entity).setCreatedAt(now);
        ((Audit) entity).setUpdatedAt(now);
        ((Audit) entity).setCreatedBy(modifiedBy);
        ((Audit) entity).setUpdatedBy(modifiedBy);
    }

}
