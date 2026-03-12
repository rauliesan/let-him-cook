package com.daw.mappers;

import com.daw.dtos.request.TipoComidaRequestDTO;
import com.daw.dtos.response.TipoComidaResponseDTO;
import com.daw.entities.TipoComida;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T16:53:14+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class TipoComidaMapperImpl implements TipoComidaMapper {

    @Override
    public TipoComida toEntity(TipoComidaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TipoComida tipoComida = new TipoComida();

        tipoComida.setDescripcion( dto.getDescripcion() );
        tipoComida.setIconoUrl( dto.getIconoUrl() );
        tipoComida.setNombre( dto.getNombre() );

        return tipoComida;
    }

    @Override
    public TipoComidaResponseDTO toResponseDTO(TipoComida entity) {
        if ( entity == null ) {
            return null;
        }

        TipoComidaResponseDTO tipoComidaResponseDTO = new TipoComidaResponseDTO();

        tipoComidaResponseDTO.setDescripcion( entity.getDescripcion() );
        tipoComidaResponseDTO.setIconoUrl( entity.getIconoUrl() );
        tipoComidaResponseDTO.setId( entity.getId() );
        tipoComidaResponseDTO.setNombre( entity.getNombre() );

        return tipoComidaResponseDTO;
    }

    @Override
    public List<TipoComidaResponseDTO> toListDTO(List<TipoComida> list) {
        if ( list == null ) {
            return null;
        }

        List<TipoComidaResponseDTO> list1 = new ArrayList<TipoComidaResponseDTO>( list.size() );
        for ( TipoComida tipoComida : list ) {
            list1.add( toResponseDTO( tipoComida ) );
        }

        return list1;
    }
}
