package com.infodation.userservice.mapper;

import com.infodation.userservice.models.User;
import com.infodation.userservice.models.UserDTO;
import com.infodation.userservice.models.dto.user.CreateUserDTO;
import com.infodation.userservice.models.dto.user.UpdateUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = SexMapper.class)
public interface UserMapper {

    // Tạo instance của UserMapper
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "sex", target = "sex", qualifiedByName = "sexToEnum")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User userDTOToUser(UserDTO userDTO);
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "sex", target = "sex", qualifiedByName = "sexToEnum")
    User createUserDTOToUser(CreateUserDTO createUserDTO);
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "sex", target = "sex", qualifiedByName = "sexToEnum")
    void updateUserDTOToUser(UpdateUserDTO updateUserDTO, @MappingTarget User userToUpdate);
}

