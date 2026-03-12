package com.daw.mappers;

import com.daw.dtos.request.FavoritoSupermercadoRequestDTO;
import com.daw.dtos.response.FavoritoSupermercadoResponseDTO;
import com.daw.entities.FavoritoSupermercado;
import com.daw.entities.Supermercado;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T16:53:14+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FavoritoSupermercadoMapperImpl implements FavoritoSupermercadoMapper {

    @Override
    public FavoritoSupermercado toEntity(FavoritoSupermercadoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FavoritoSupermercado favoritoSupermercado = new FavoritoSupermercado();

        favoritoSupermercado.setSupermercado( favoritoSupermercadoRequestDTOToSupermercado( dto ) );

        return favoritoSupermercado;
    }

    @Override
    public FavoritoSupermercadoResponseDTO toResponseDTO(FavoritoSupermercado entity) {
        if ( entity == null ) {
            return null;
        }

        FavoritoSupermercadoResponseDTO favoritoSupermercadoResponseDTO = new FavoritoSupermercadoResponseDTO();

        favoritoSupermercadoResponseDTO.setSupermercadoId( entitySupermercadoId( entity ) );
        favoritoSupermercadoResponseDTO.setSupermercadoNombre( entitySupermercadoNombre( entity ) );
        favoritoSupermercadoResponseDTO.setFechaAgregada( entity.getFechaAgregada() );
        favoritoSupermercadoResponseDTO.setId( entity.getId() );

        return favoritoSupermercadoResponseDTO;
    }

    @Override
    public List<FavoritoSupermercadoResponseDTO> toListDTO(List<FavoritoSupermercado> list) {
        if ( list == null ) {
            return null;
        }

        List<FavoritoSupermercadoResponseDTO> list1 = new ArrayList<FavoritoSupermercadoResponseDTO>( list.size() );
        for ( FavoritoSupermercado favoritoSupermercado : list ) {
            list1.add( toResponseDTO( favoritoSupermercado ) );
        }

        return list1;
    }

    protected Supermercado favoritoSupermercadoRequestDTOToSupermercado(FavoritoSupermercadoRequestDTO favoritoSupermercadoRequestDTO) {
        if ( favoritoSupermercadoRequestDTO == null ) {
            return null;
        }

        Supermercado supermercado = new Supermercado();

        supermercado.setId( favoritoSupermercadoRequestDTO.getSupermercadoId() );

        return supermercado;
    }

    private UUID entitySupermercadoId(FavoritoSupermercado favoritoSupermercado) {
        Supermercado supermercado = favoritoSupermercado.getSupermercado();
        if ( supermercado == null ) {
            return null;
        }
        return supermercado.getId();
    }

    private String entitySupermercadoNombre(FavoritoSupermercado favoritoSupermercado) {
        Supermercado supermercado = favoritoSupermercado.getSupermercado();
        if ( supermercado == null ) {
            return null;
        }
        return supermercado.getNombre();
    }
}
