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

    @Mapping(target = "tipoComida.id", source = "tipoComidaId")
    @Mapping(target = "iaModelo.id", source = "iaModeloId")
    Receta toEntity(RecetaRequestDTO dto);

    @Mapping(target = "tipoComidaId", source = "tipoComida.id")
    @Mapping(target = "tipoComidaNombre", source = "tipoComida.nombre")
    @Mapping(target = "usuarioCreadorId", source = "usuario.id")
    @Mapping(target = "usuarioCreadorNombre", source = "usuario.nombre")
    @Mapping(target = "iaModeloId", source = "iaModelo.id")
    RecetaResponseDTO toResponseDTO(Receta entity);

    List<RecetaResponseDTO> toListDTO(List<Receta> list);

    default Page<RecetaResponseDTO> toPageDTO(
            Page<Receta> page) {
        return page.map(this::toResponseDTO);
    }
}