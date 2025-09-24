package com.ajayaraj.cloudbox.mapper;

import com.ajayaraj.cloudbox.dto.RegisterRequest;
import com.ajayaraj.cloudbox.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    RegisterRequest toDto(User user);
}
