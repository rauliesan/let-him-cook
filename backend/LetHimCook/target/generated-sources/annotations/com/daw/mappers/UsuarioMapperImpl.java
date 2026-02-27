package com.daw.mappers;

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.entities.IaModelo;
import com.daw.entities.Usuario;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:21:51+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setIaModeloSeleccionado( usuarioRequestDTOToIaModelo( dto ) );
        usuario.setEmail( dto.getEmail() );
        usuario.setFotoUrl( dto.getFotoUrl() );
        usuario.setNombre( dto.getNombre() );

        return usuario;
    }

    @Override
    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();

        usuarioResponseDTO.setIaModeloSeleccionadoId( entityIaModeloSeleccionadoId( entity ) );
        usuarioResponseDTO.setIaModeloSeleccionadoNombre( entityIaModeloSeleccionadoNombreModelo( entity ) );
        usuarioResponseDTO.setEmail( entity.getEmail() );
        usuarioResponseDTO.setFechaInscripcion( entity.getFechaInscripcion() );
        usuarioResponseDTO.setFotoUrl( entity.getFotoUrl() );
        usuarioResponseDTO.setId( entity.getId() );
        usuarioResponseDTO.setNivel( entity.getNivel() );
        usuarioResponseDTO.setNombre( entity.getNombre() );
        usuarioResponseDTO.setPuntos( entity.getPuntos() );

        return usuarioResponseDTO;
    }

    protected IaModelo usuarioRequestDTOToIaModelo(UsuarioRequestDTO usuarioRequestDTO) {
        if ( usuarioRequestDTO == null ) {
            return null;
        }

        IaModelo iaModelo = new IaModelo();

        iaModelo.setId( usuarioRequestDTO.getIaModeloSeleccionadoId() );

        return iaModelo;
    }

    private UUID entityIaModeloSeleccionadoId(Usuario usuario) {
        IaModelo iaModeloSeleccionado = usuario.getIaModeloSeleccionado();
        if ( iaModeloSeleccionado == null ) {
            return null;
        }
        return iaModeloSeleccionado.getId();
    }

    private String entityIaModeloSeleccionadoNombreModelo(Usuario usuario) {
        IaModelo iaModeloSeleccionado = usuario.getIaModeloSeleccionado();
        if ( iaModeloSeleccionado == null ) {
            return null;
        }
        return iaModeloSeleccionado.getNombreModelo();
    }
}
