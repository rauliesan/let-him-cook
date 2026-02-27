package com.daw.mappers;

import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.entities.UsuarioLogro;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:25:52+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UsuarioLogroMapperImpl implements UsuarioLogroMapper {

    @Override
    public UsuarioLogroResponseDTO toResponseDTO(UsuarioLogro entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioLogroResponseDTO usuarioLogroResponseDTO = new UsuarioLogroResponseDTO();

        usuarioLogroResponseDTO.setLogroId( entityLogroId( entity ) );
        usuarioLogroResponseDTO.setLogroNombre( entityLogroNombre( entity ) );
        usuarioLogroResponseDTO.setLogroIcono( entityLogroIcono( entity ) );
        usuarioLogroResponseDTO.setFechaObtenido( entity.getFechaObtenido() );
        usuarioLogroResponseDTO.setId( entity.getId() );

        return usuarioLogroResponseDTO;
    }

    private UUID entityLogroId(UsuarioLogro usuarioLogro) {
        Logro logro = usuarioLogro.getLogro();
        if ( logro == null ) {
            return null;
        }
        return logro.getId();
    }

    private String entityLogroNombre(UsuarioLogro usuarioLogro) {
        Logro logro = usuarioLogro.getLogro();
        if ( logro == null ) {
            return null;
        }
        return logro.getNombre();
    }

    private String entityLogroIcono(UsuarioLogro usuarioLogro) {
        Logro logro = usuarioLogro.getLogro();
        if ( logro == null ) {
            return null;
        }
        return logro.getIcono();
    }
}
