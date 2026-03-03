package com.daw.mappers;

import com.daw.dtos.request.TipoComidaRequestDTO;
import com.daw.dtos.response.TipoComidaResponseDTO;
import com.daw.entities.TipoComida;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoComidaMapper {
	
    TipoComida toEntity(TipoComidaRequestDTO dto);
    
    TipoComidaResponseDTO toResponseDTO(TipoComida entity);
    
}