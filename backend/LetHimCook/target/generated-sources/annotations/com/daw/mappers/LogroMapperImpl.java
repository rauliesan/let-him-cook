package com.daw.mappers;

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.entities.UsuarioLogro;
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
public class LogroMapperImpl implements LogroMapper {

    @Override
    public Logro toEntity(LogroRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Logro logro = new Logro();

        logro.setDescripcion( dto.getDescripcion() );
        logro.setIconoUrl( dto.getIconoUrl() );
        logro.setNombre( dto.getNombre() );

        return logro;
    }

    @Override
    public LogroResponseDTO toResponseDTO(Logro entity) {
        if ( entity == null ) {
            return null;
        }

        LogroResponseDTO logroResponseDTO = new LogroResponseDTO();

        logroResponseDTO.setDescripcion( entity.getDescripcion() );
        logroResponseDTO.setIconoUrl( entity.getIconoUrl() );
        logroResponseDTO.setId( entity.getId() );
        logroResponseDTO.setNombre( entity.getNombre() );

        return logroResponseDTO;
    }

    @Override
    public LogroResponseDTO toResponseDTO(UsuarioLogro entity) {
        if ( entity == null ) {
            return null;
        }

        LogroResponseDTO logroResponseDTO = new LogroResponseDTO();

        logroResponseDTO.setId( entityLogroId( entity ) );
        logroResponseDTO.setNombre( entityLogroNombre( entity ) );
        logroResponseDTO.setDescripcion( entityLogroDescripcion( entity ) );
        logroResponseDTO.setIconoUrl( entityLogroIconoUrl( entity ) );
        logroResponseDTO.setFechaObtenido( entity.getFechaObtenido() );

        return logroResponseDTO;
    }

    @Override
    public List<LogroResponseDTO> toListDTO(List<Logro> list) {
        if ( list == null ) {
            return null;
        }

        List<LogroResponseDTO> list1 = new ArrayList<LogroResponseDTO>( list.size() );
        for ( Logro logro : list ) {
            list1.add( toResponseDTO( logro ) );
        }

        return list1;
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

    private String entityLogroDescripcion(UsuarioLogro usuarioLogro) {
        Logro logro = usuarioLogro.getLogro();
        if ( logro == null ) {
            return null;
        }
        return logro.getDescripcion();
    }

    private String entityLogroIconoUrl(UsuarioLogro usuarioLogro) {
        Logro logro = usuarioLogro.getLogro();
        if ( logro == null ) {
            return null;
        }
        return logro.getIconoUrl();
    }
}
