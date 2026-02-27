package com.daw.mappers;

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.entities.Logro;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:25:30+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
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
        logro.setIcono( dto.getIcono() );
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
        logroResponseDTO.setIcono( entity.getIcono() );
        logroResponseDTO.setId( entity.getId() );
        logroResponseDTO.setNombre( entity.getNombre() );

        return logroResponseDTO;
    }
}
