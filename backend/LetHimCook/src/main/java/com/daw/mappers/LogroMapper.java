package com.daw.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.entities.UsuarioLogro;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogroMapper {

    // 1. Para crear logros (Admin)
    Logro toEntity(LogroRequestDTO dto);

    // Para listar logros en general
    LogroResponseDTO toResponseDTO(Logro entity);

    // 2. Para listar "Mis Logros"
    @Mapping(target = "id", source = "logro.id")
    @Mapping(target = "nombre", source = "logro.nombre")
    @Mapping(target = "descripcion", source = "logro.descripcion")
    @Mapping(target = "iconoUrl", source = "logro.iconoUrl")
    @Mapping(target = "fechaObtenido", source = "fechaObtenido")
    LogroResponseDTO toResponseDTO(UsuarioLogro entity);

    List<LogroResponseDTO> toListDTO(List<Logro> list);

    default Page<LogroResponseDTO> toPageDTO(Page<Logro> page) {
        return page.map(this::toResponseDTO);
    }

}