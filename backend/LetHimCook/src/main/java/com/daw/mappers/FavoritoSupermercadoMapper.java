package com.daw.mappers;

import com.daw.dtos.request.FavoritoSupermercadoRequestDTO;
import com.daw.dtos.response.FavoritoSupermercadoResponseDTO;
import com.daw.entities.FavoritoSupermercado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FavoritoSupermercadoMapper {

    @Mapping(target = "supermercado.id", source = "supermercadoId")
    FavoritoSupermercado toEntity(FavoritoSupermercadoRequestDTO dto);

    @Mapping(target = "supermercadoId", source = "supermercado.id")
    @Mapping(target = "supermercadoNombre", source = "supermercado.nombre")
    FavoritoSupermercadoResponseDTO toResponseDTO(FavoritoSupermercado entity);

    List<FavoritoSupermercadoResponseDTO> toListDTO(List<FavoritoSupermercado> list);

    default Page<FavoritoSupermercadoResponseDTO> toPageDTO(Page<FavoritoSupermercado> page) {
        return page.map(this::toResponseDTO);
    }
}