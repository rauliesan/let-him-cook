package com.daw.mappers;

import com.daw.dtos.request.PostRequestDTO;
import com.daw.dtos.response.PostResponseDTO;
import com.daw.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    /* usuario y recetaVinculada se asignan manualmente en el servicio */
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "recetaVinculada", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    Post toEntity(PostRequestDTO dto);

    @Mapping(target = "usuarioId",              source = "usuario.id")
    @Mapping(target = "usuarioNombre",          source = "usuario.nombre")
    @Mapping(target = "usuarioFotoUrl",         source = "usuario.fotoUrl")
    @Mapping(target = "recetaVinculadaId",      source = "recetaVinculada.id")
    @Mapping(target = "recetaVinculadaNombre",  source = "recetaVinculada.nombre")
    PostResponseDTO toResponseDTO(Post entity);

    List<PostResponseDTO> toListDTO(List<Post> list);
}
