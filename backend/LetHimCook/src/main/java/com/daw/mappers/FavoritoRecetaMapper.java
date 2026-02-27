package com.daw.mappers;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.entities.FavoritoReceta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoritoRecetaMapper {

    @Mapping(target = "receta.id", source = "recetaId")
    FavoritoReceta toEntity(FavoritoRecetaRequestDTO dto);

    @Mapping(target = "recetaId", source = "receta.id")
    @Mapping(target = "recetaNombre", source = "receta.nombre")
    FavoritoRecetaResponseDTO toResponseDTO(FavoritoReceta entity);
}