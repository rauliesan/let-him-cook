package com.daw.mappers;

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.entities.Logro;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogroMapper {
	
    Logro toEntity(LogroRequestDTO dto);
    
    LogroResponseDTO toResponseDTO(Logro entity);
    
}