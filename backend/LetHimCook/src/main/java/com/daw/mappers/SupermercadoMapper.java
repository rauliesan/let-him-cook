package com.daw.mappers;

import com.daw.dtos.request.SupermercadoRequestDTO;
import com.daw.dtos.response.SupermercadoResponseDTO;
import com.daw.entities.Supermercado;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupermercadoMapper {
	
    Supermercado toEntity(SupermercadoRequestDTO dto);
    
    SupermercadoResponseDTO toResponseDTO(Supermercado entity);
    
}