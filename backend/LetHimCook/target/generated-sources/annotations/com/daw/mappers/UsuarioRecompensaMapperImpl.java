package com.daw.mappers;

import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.entities.Recompensa;
import com.daw.entities.UsuarioRecompensa;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:25:58+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UsuarioRecompensaMapperImpl implements UsuarioRecompensaMapper {

    @Override
    public UsuarioRecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioRecompensaResponseDTO usuarioRecompensaResponseDTO = new UsuarioRecompensaResponseDTO();

        usuarioRecompensaResponseDTO.setRecompensaId( entityRecompensaId( entity ) );
        usuarioRecompensaResponseDTO.setRecompensaNombre( entityRecompensaNombre( entity ) );
        usuarioRecompensaResponseDTO.setFechaObtenida( entity.getFechaObtenida() );
        usuarioRecompensaResponseDTO.setId( entity.getId() );

        return usuarioRecompensaResponseDTO;
    }

    private UUID entityRecompensaId(UsuarioRecompensa usuarioRecompensa) {
        Recompensa recompensa = usuarioRecompensa.getRecompensa();
        if ( recompensa == null ) {
            return null;
        }
        return recompensa.getId();
    }

    private String entityRecompensaNombre(UsuarioRecompensa usuarioRecompensa) {
        Recompensa recompensa = usuarioRecompensa.getRecompensa();
        if ( recompensa == null ) {
            return null;
        }
        return recompensa.getNombre();
    }
}
