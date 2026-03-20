package com.daw.mappers;

import com.daw.dtos.request.SupermercadoRequestDTO;
import com.daw.dtos.response.SupermercadoResponseDTO;
import com.daw.entities.Supermercado;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:10:23+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class SupermercadoMapperImpl implements SupermercadoMapper {

    @Override
    public Supermercado toEntity(SupermercadoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Supermercado supermercado = new Supermercado();

        supermercado.setDescripcion( dto.getDescripcion() );
        supermercado.setDireccion( dto.getDireccion() );
        supermercado.setFotoUrl( dto.getFotoUrl() );
        supermercado.setHorario( dto.getHorario() );
        supermercado.setNombre( dto.getNombre() );
        supermercado.setValoracion( dto.getValoracion() );

        return supermercado;
    }

    @Override
    public SupermercadoResponseDTO toResponseDTO(Supermercado entity) {
        if ( entity == null ) {
            return null;
        }

        SupermercadoResponseDTO supermercadoResponseDTO = new SupermercadoResponseDTO();

        supermercadoResponseDTO.setDescripcion( entity.getDescripcion() );
        supermercadoResponseDTO.setDireccion( entity.getDireccion() );
        supermercadoResponseDTO.setFotoUrl( entity.getFotoUrl() );
        supermercadoResponseDTO.setHorario( entity.getHorario() );
        supermercadoResponseDTO.setId( entity.getId() );
        supermercadoResponseDTO.setNombre( entity.getNombre() );
        supermercadoResponseDTO.setValoracion( entity.getValoracion() );

        return supermercadoResponseDTO;
    }

    @Override
    public List<SupermercadoResponseDTO> toListDTO(List<Supermercado> list) {
        if ( list == null ) {
            return null;
        }

        List<SupermercadoResponseDTO> list1 = new ArrayList<SupermercadoResponseDTO>( list.size() );
        for ( Supermercado supermercado : list ) {
            list1.add( toResponseDTO( supermercado ) );
        }

        return list1;
    }
}
