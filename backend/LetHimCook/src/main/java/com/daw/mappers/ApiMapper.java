package com.daw.mappers;

import com.daw.dtos.request.ApiRequestDTO;
import com.daw.dtos.response.ApiResponseDTO;
import com.daw.entities.Api;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiMapper {
	
    Api toEntity(ApiRequestDTO dto);
    
    ApiResponseDTO toResponseDTO(Api entity);
    
}