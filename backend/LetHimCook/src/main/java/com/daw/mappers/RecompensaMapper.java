package com.daw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.entities.Recompensa;
import com.daw.entities.UsuarioRecompensa;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecompensaMapper {

    // 1. Para crear recompensas
    Recompensa toEntity(RecompensaRequestDTO dto);

    // Para listar recompensas en general
    RecompensaResponseDTO toResponseDTO(Recompensa entity);

    // 2. Para listar "Mis Recompensas"
    @Mapping(target = "id", source = "recompensa.id")
    @Mapping(target = "nombre", source = "recompensa.nombre")
    @Mapping(target = "descripcion", source = "recompensa.descripcion")
    @Mapping(target = "probabilidad", source = "recompensa.probabilidad")
    @Mapping(target = "fechaObtenida", source = "fechaObtenida")
    RecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity);

    List<RecompensaResponseDTO> toListDTO(List<Recompensa> list);

    default Page<RecompensaResponseDTO> toPageDTO(Page<Recompensa> page) {
        return page.map(this::toResponseDTO);
    }

}