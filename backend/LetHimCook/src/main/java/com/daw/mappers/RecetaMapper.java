package com.daw.mappers;

import com.daw.dtos.request.RecetaRequestDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.entities.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecetaMapper {

    /* tipoComida2/3 y usuario se asignan manualmente en el servicio.
       tipoComida principal se puede mapear desde tipoComidaId si se proporciona. */
    @Mapping(target = "tipoComida.id", source = "tipoComidaId")
    @Mapping(target = "tipoComida2", ignore = true)
    @Mapping(target = "tipoComida3", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "iaModelo.id", source = "iaModeloId")
    Receta toEntity(RecetaRequestDTO dto);

    @Mapping(target = "tipoComidaId",         source = "tipoComida.id")
    @Mapping(target = "tipoComidaNombre",      source = "tipoComida.nombre")
    @Mapping(target = "tipoComida2Nombre",     source = "tipoComida2.nombre")
    @Mapping(target = "tipoComida3Nombre",     source = "tipoComida3.nombre")
    @Mapping(target = "usuarioCreadorId",      source = "usuario.id")
    @Mapping(target = "usuarioCreadorNombre",  source = "usuario.nombre")
    @Mapping(target = "iaModeloId",            source = "iaModelo.id")
    RecetaResponseDTO toResponseDTO(Receta entity);

    List<RecetaResponseDTO> toListDTO(List<Receta> list);

    default Page<RecetaResponseDTO> toPageDTO(Page<Receta> page) {
        return page.map(this::toResponseDTO);
    }
}
