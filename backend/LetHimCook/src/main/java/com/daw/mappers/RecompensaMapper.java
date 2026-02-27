package com.daw.mappers;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.entities.Recompensa;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecompensaMapper {
	
    Recompensa toEntity(RecompensaRequestDTO dto);
    
    RecompensaResponseDTO toResponseDTO(Recompensa entity);
    
}