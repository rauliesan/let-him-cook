package com.daw.mappers;

import com.daw.dtos.request.TipoComidaRequestDTO;
import com.daw.dtos.response.TipoComidaResponseDTO;
import com.daw.entities.TipoComida;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoComidaMapper {

    TipoComida toEntity(TipoComidaRequestDTO dto);

    TipoComidaResponseDTO toResponseDTO(TipoComida entity);

    List<TipoComidaResponseDTO> toListDTO(List<TipoComida> list);

    default Page<TipoComidaResponseDTO> toPageDTO(Page<TipoComida> page) {
        return page.map(this::toResponseDTO);
    }

}