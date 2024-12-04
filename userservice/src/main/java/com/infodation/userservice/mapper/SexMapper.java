package com.infodation.userservice.mapper;

import com.infodation.userservice.models.Sex;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface SexMapper {

    @Named("sexToEnum")
    default Sex mapSexToEnum(String sex) {
        return Sex.valueOf(sex.toUpperCase()); // Convert String to Enum by converting the input to uppercase
    }

    @Named("sexToString")
    default String mapSexToString(Sex sex) {
        return sex != null ? sex.name() : null; // Convert enum to String
    }

}
