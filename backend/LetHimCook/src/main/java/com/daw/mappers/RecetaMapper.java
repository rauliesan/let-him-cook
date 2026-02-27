package com.daw.mappers;

import com.daw.dtos.request.RecetaRequestDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.entities.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecetaMapper {

    @Mapping(target = "tipoComida.id", source = "tipoComidaId")
    Receta toEntity(RecetaRequestDTO dto);

    @Mapping(target = "tipoComidaId", source = "tipoComida.id")
    @Mapping(target = "tipoComidaNombre", source = "tipoComida.nombre")
    @Mapping(target = "usuarioCreadorId", source = "usuario.id")
    @Mapping(target = "usuarioCreadorNombre", source = "usuario.nombre")
    RecetaResponseDTO toResponseDTO(Receta entity);
}