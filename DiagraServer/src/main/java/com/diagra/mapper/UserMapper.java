package com.diagra.mapper;

import com.diagra.dao.model.UserEntity;
import com.diagra.dao.model.UserRoles;
import com.diagra.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper implements EntityMapper<UserDto, UserEntity> {

    @Override
    @Mappings({
            @Mapping(target = "password", ignore = true)
    })
    public abstract UserDto toDto(UserEntity entity);

    @Override
    @Mappings({
            @Mapping(target = "roles", expression = "java(roles())")
    })
    public abstract UserEntity fromDto(UserDto dto);

    protected static List<UserRoles> roles() {
        return new ArrayList<UserRoles>() {{
            add(UserRoles.USER);
        }};
    }

}