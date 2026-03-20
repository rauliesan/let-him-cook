package com.daw.mappers;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.entities.FavoritoReceta;
import com.daw.entities.Receta;
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
public class FavoritoRecetaMapperImpl implements FavoritoRecetaMapper {

    @Override
    public FavoritoReceta toEntity(FavoritoRecetaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FavoritoReceta favoritoReceta = new FavoritoReceta();

        favoritoReceta.setReceta( favoritoRecetaRequestDTOToReceta( dto ) );

        return favoritoReceta;
    }

    @Override
    public FavoritoRecetaResponseDTO toResponseDTO(FavoritoReceta entity) {
        if ( entity == null ) {
            return null;
        }

        FavoritoRecetaResponseDTO favoritoRecetaResponseDTO = new FavoritoRecetaResponseDTO();

        favoritoRecetaResponseDTO.setRecetaId( entityRecetaId( entity ) );
        favoritoRecetaResponseDTO.setRecetaNombre( entityRecetaNombre( entity ) );
        favoritoRecetaResponseDTO.setFechaAgregada( entity.getFechaAgregada() );
        favoritoRecetaResponseDTO.setId( entity.getId() );

        return favoritoRecetaResponseDTO;
    }

    @Override
    public List<FavoritoRecetaResponseDTO> toListDTO(List<FavoritoReceta> list) {
        if ( list == null ) {
            return null;
        }

        List<FavoritoRecetaResponseDTO> list1 = new ArrayList<FavoritoRecetaResponseDTO>( list.size() );
        for ( FavoritoReceta favoritoReceta : list ) {
            list1.add( toResponseDTO( favoritoReceta ) );
        }

        return list1;
    }

    protected Receta favoritoRecetaRequestDTOToReceta(FavoritoRecetaRequestDTO favoritoRecetaRequestDTO) {
        if ( favoritoRecetaRequestDTO == null ) {
            return null;
        }

        Receta receta = new Receta();

        receta.setId( favoritoRecetaRequestDTO.getRecetaId() );

        return receta;
    }

    private UUID entityRecetaId(FavoritoReceta favoritoReceta) {
        Receta receta = favoritoReceta.getReceta();
        if ( receta == null ) {
            return null;
        }
        return receta.getId();
    }

    private String entityRecetaNombre(FavoritoReceta favoritoReceta) {
        Receta receta = favoritoReceta.getReceta();
        if ( receta == null ) {
            return null;
        }
        return receta.getNombre();
    }
}
