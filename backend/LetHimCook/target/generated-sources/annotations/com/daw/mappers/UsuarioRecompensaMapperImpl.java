package com.daw.mappers;

import com.daw.dtos.request.UsuarioRecompensaRequestDTO;
import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.entities.UsuarioRecompensa;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T17:20:36+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UsuarioRecompensaMapperImpl implements UsuarioRecompensaMapper {

    @Autowired
    private RecompensaMapper recompensaMapper;

    @Override
    public UsuarioRecompensa toEntity(UsuarioRecompensaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UsuarioRecompensa usuarioRecompensa = new UsuarioRecompensa();

        return usuarioRecompensa;
    }

    @Override
    public UsuarioRecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioRecompensaResponseDTO usuarioRecompensaResponseDTO = new UsuarioRecompensaResponseDTO();

        usuarioRecompensaResponseDTO.setFechaObtenida( entity.getFechaObtenida() );
        usuarioRecompensaResponseDTO.setId( entity.getId() );
        usuarioRecompensaResponseDTO.setRecompensa( recompensaMapper.toResponseDTO( entity.getRecompensa() ) );

        return usuarioRecompensaResponseDTO;
    }

    @Override
    public List<UsuarioRecompensaResponseDTO> toListDTO(List<UsuarioRecompensa> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioRecompensaResponseDTO> list = new ArrayList<UsuarioRecompensaResponseDTO>( entities.size() );
        for ( UsuarioRecompensa usuarioRecompensa : entities ) {
            list.add( toResponseDTO( usuarioRecompensa ) );
        }

        return list;
    }
}
