package com.daw.mappers;

import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.entities.UsuarioRecompensa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioRecompensaMapper {
	
    @Mapping(target = "recompensaId", source = "recompensa.id")
    @Mapping(target = "recompensaNombre", source = "recompensa.nombre")
    UsuarioRecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity);
    
}