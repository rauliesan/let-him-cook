package com.daw.mappers;

import com.daw.dtos.request.PostComentarioRequestDTO;
import com.daw.dtos.response.PostComentarioResponseDTO;
import com.daw.entities.PostComentario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostComentarioMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "post",    ignore = true)
    PostComentario toEntity(PostComentarioRequestDTO dto);

    @Mapping(target = "usuarioId",          source = "usuario.id")
    @Mapping(target = "usuarioNombre",      source = "usuario.nombre")
    @Mapping(target = "usuarioFotoUrl",     source = "usuario.fotoUrl")
    @Mapping(target = "postId",             source = "post.id")
    @Mapping(target = "recetaVinculadaId",  source = "recetaVinculada.id")
    @Mapping(target = "recetaVinculadaNombre", source = "recetaVinculada.nombre")
    PostComentarioResponseDTO toResponseDTO(PostComentario entity);

    List<PostComentarioResponseDTO> toListDTO(List<PostComentario> list);
}
