package com.daw.mappers;

import com.daw.dtos.request.ComentarioRequestDTO;
import com.daw.dtos.response.ComentarioResponseDTO;
import com.daw.entities.Comentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComentarioMapper {

    @Mapping(target = "receta.id", source = "recetaId")
    Comentario toEntity(ComentarioRequestDTO dto);

    @Mapping(target = "recetaId", source = "receta.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "usuarioNombre", source = "usuario.nombre")
    @Mapping(target = "usuarioFotoUrl", source = "usuario.fotoUrl")
    ComentarioResponseDTO toResponseDTO(Comentario entity);
}