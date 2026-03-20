package com.daw.mappers;

import com.daw.dtos.request.UsuarioLogroRequestDTO;
import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.UsuarioLogro;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:10:23+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UsuarioLogroMapperImpl implements UsuarioLogroMapper {

    @Autowired
    private LogroMapper logroMapper;

    @Override
    public UsuarioLogro toEntity(UsuarioLogroRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UsuarioLogro usuarioLogro = new UsuarioLogro();

        return usuarioLogro;
    }

    @Override
    public UsuarioLogroResponseDTO toResponseDTO(UsuarioLogro entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioLogroResponseDTO usuarioLogroResponseDTO = new UsuarioLogroResponseDTO();

        usuarioLogroResponseDTO.setFechaObtenido( entity.getFechaObtenido() );
        usuarioLogroResponseDTO.setId( entity.getId() );
        usuarioLogroResponseDTO.setLogro( logroMapper.toResponseDTO( entity.getLogro() ) );

        return usuarioLogroResponseDTO;
    }

    @Override
    public List<UsuarioLogroResponseDTO> toListDTO(List<UsuarioLogro> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioLogroResponseDTO> list = new ArrayList<UsuarioLogroResponseDTO>( entities.size() );
        for ( UsuarioLogro usuarioLogro : entities ) {
            list.add( toResponseDTO( usuarioLogro ) );
        }

        return list;
    }
}
