package com.daw.mappers;

import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.UsuarioLogro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioLogroMapper {
	
    @Mapping(target = "logroId", source = "logro.id")
    @Mapping(target = "logroNombre", source = "logro.nombre")
    @Mapping(target = "logroIcono", source = "logro.icono")
    UsuarioLogroResponseDTO toResponseDTO(UsuarioLogro entity);
    
}