package com.daw.mappers;

import com.daw.dtos.request.SupermercadoRequestDTO;
import com.daw.dtos.response.SupermercadoResponseDTO;
import com.daw.entities.Supermercado;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupermercadoMapper {

    Supermercado toEntity(SupermercadoRequestDTO dto);

    SupermercadoResponseDTO toResponseDTO(Supermercado entity);

    List<SupermercadoResponseDTO> toListDTO(List<Supermercado> list);

    default Page<SupermercadoResponseDTO> toPageDTO(
            Page<Supermercado> page) {
        return page.map(this::toResponseDTO);
    }

}