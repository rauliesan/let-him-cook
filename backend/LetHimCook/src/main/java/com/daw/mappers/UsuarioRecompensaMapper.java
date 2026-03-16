package com.daw.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import com.daw.dtos.request.UsuarioRecompensaRequestDTO;
import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.entities.UsuarioRecompensa;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { RecompensaMapper.class })
public interface UsuarioRecompensaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "recompensa", ignore = true)
    @Mapping(target = "fechaObtenida", ignore = true)
    UsuarioRecompensa toEntity(UsuarioRecompensaRequestDTO dto);

    UsuarioRecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity);

    List<UsuarioRecompensaResponseDTO> toListDTO(List<UsuarioRecompensa> entities);

    default Page<UsuarioRecompensaResponseDTO> toPageDTO(Page<UsuarioRecompensa> page) {
        return page.map(this::toResponseDTO);
    }
}
