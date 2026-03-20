package com.daw.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import com.daw.dtos.request.UsuarioLogroRequestDTO;
import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.UsuarioLogro;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { LogroMapper.class })
public interface UsuarioLogroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "logro", ignore = true)
    @Mapping(target = "fechaObtenido", ignore = true)
    UsuarioLogro toEntity(UsuarioLogroRequestDTO dto);

    UsuarioLogroResponseDTO toResponseDTO(UsuarioLogro entity);

    List<UsuarioLogroResponseDTO> toListDTO(List<UsuarioLogro> entities);

    default Page<UsuarioLogroResponseDTO> toPageDTO(Page<UsuarioLogro> page) {
        return page.map(this::toResponseDTO);
    }
}
