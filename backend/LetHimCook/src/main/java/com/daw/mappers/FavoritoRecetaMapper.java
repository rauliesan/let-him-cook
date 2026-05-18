package com.daw.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.entities.FavoritoReceta;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoritoRecetaMapper {

    /* receta y usuario se asignan manualmente en el servicio
       para evitar objetos transitorios huérfanos en Hibernate */
    @Mapping(target = "receta", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    FavoritoReceta toEntity(FavoritoRecetaRequestDTO dto);

    @Mapping(target = "recetaId",     source = "receta.id")
    @Mapping(target = "recetaNombre", source = "receta.nombre")
    FavoritoRecetaResponseDTO toResponseDTO(FavoritoReceta entity);

    List<FavoritoRecetaResponseDTO> toListDTO(List<FavoritoReceta> list);
    
    default Page<FavoritoRecetaResponseDTO> toPageDTO(Page<FavoritoReceta> page) {
        return page.map(this::toResponseDTO);
    }
}
