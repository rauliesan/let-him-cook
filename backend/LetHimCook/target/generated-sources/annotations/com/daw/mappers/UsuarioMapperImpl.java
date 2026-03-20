package com.daw.mappers;

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.entities.IaModelo;
import com.daw.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T17:52:25+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
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
        usuario.setNombre( dto.getNombre() );
        usuario.setEmail( dto.getEmail() );
        usuario.setFotoUrl( dto.getFotoUrl() );

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
        usuarioResponseDTO.setId( entity.getId() );
        usuarioResponseDTO.setNombre( entity.getNombre() );
        usuarioResponseDTO.setEmail( entity.getEmail() );
        usuarioResponseDTO.setPuntos( entity.getPuntos() );
        usuarioResponseDTO.setNivel( entity.getNivel() );
        usuarioResponseDTO.setFechaInscripcion( entity.getFechaInscripcion() );
        usuarioResponseDTO.setFotoUrl( entity.getFotoUrl() );
        usuarioResponseDTO.setRol( entity.getRol() );

        return usuarioResponseDTO;
    }

    @Override
    public List<UsuarioResponseDTO> toListDTO(List<Usuario> list) {
        if ( list == null ) {
            return null;
        }

        List<UsuarioResponseDTO> list1 = new ArrayList<UsuarioResponseDTO>( list.size() );
        for ( Usuario usuario : list ) {
            list1.add( toResponseDTO( usuario ) );
        }

        return list1;
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
