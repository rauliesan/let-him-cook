package com.daw.mappers;

import com.daw.dtos.request.ComentarioRequestDTO;
import com.daw.dtos.response.ComentarioResponseDTO;
import com.daw.entities.Comentario;
import com.daw.entities.Receta;
import com.daw.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:10:23+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ComentarioMapperImpl implements ComentarioMapper {

    @Override
    public Comentario toEntity(ComentarioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Comentario comentario = new Comentario();

        comentario.setReceta( comentarioRequestDTOToReceta( dto ) );
        comentario.setComentario( dto.getComentario() );
        comentario.setValoracion( dto.getValoracion() );

        return comentario;
    }

    @Override
    public ComentarioResponseDTO toResponseDTO(Comentario entity) {
        if ( entity == null ) {
            return null;
        }

        ComentarioResponseDTO comentarioResponseDTO = new ComentarioResponseDTO();

        comentarioResponseDTO.setRecetaId( entityRecetaId( entity ) );
        comentarioResponseDTO.setUsuarioId( entityUsuarioId( entity ) );
        comentarioResponseDTO.setUsuarioNombre( entityUsuarioNombre( entity ) );
        comentarioResponseDTO.setUsuarioFotoUrl( entityUsuarioFotoUrl( entity ) );
        comentarioResponseDTO.setComentario( entity.getComentario() );
        comentarioResponseDTO.setFechaCreacion( entity.getFechaCreacion() );
        comentarioResponseDTO.setId( entity.getId() );
        comentarioResponseDTO.setValoracion( entity.getValoracion() );

        return comentarioResponseDTO;
    }

    @Override
    public List<ComentarioResponseDTO> toListDTO(List<Comentario> list) {
        if ( list == null ) {
            return null;
        }

        List<ComentarioResponseDTO> list1 = new ArrayList<ComentarioResponseDTO>( list.size() );
        for ( Comentario comentario : list ) {
            list1.add( toResponseDTO( comentario ) );
        }

        return list1;
    }

    protected Receta comentarioRequestDTOToReceta(ComentarioRequestDTO comentarioRequestDTO) {
        if ( comentarioRequestDTO == null ) {
            return null;
        }

        Receta receta = new Receta();

        receta.setId( comentarioRequestDTO.getRecetaId() );

        return receta;
    }

    private UUID entityRecetaId(Comentario comentario) {
        Receta receta = comentario.getReceta();
        if ( receta == null ) {
            return null;
        }
        return receta.getId();
    }

    private UUID entityUsuarioId(Comentario comentario) {
        Usuario usuario = comentario.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombre(Comentario comentario) {
        Usuario usuario = comentario.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombre();
    }

    private String entityUsuarioFotoUrl(Comentario comentario) {
        Usuario usuario = comentario.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getFotoUrl();
    }
}
