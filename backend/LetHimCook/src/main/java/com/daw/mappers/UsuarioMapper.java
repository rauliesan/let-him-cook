package com.daw.mappers;

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    @Mapping(target = "iaModeloSeleccionado.id", source = "iaModeloSeleccionadoId")
    @Mapping(target = "passwordHash", ignore = true)
    Usuario toEntity(UsuarioRequestDTO dto);

    @Mapping(target = "iaModeloSeleccionadoId", source = "iaModeloSeleccionado.id")
    @Mapping(target = "iaModeloSeleccionadoNombre", source = "iaModeloSeleccionado.nombreModelo")
    UsuarioResponseDTO toResponseDTO(Usuario entity);

    List<UsuarioResponseDTO> toListDTO(List<Usuario> list);

    default Page<UsuarioResponseDTO> toPageDTO(Page<Usuario> page) {
        return page.map(this::toResponseDTO);
    }
}