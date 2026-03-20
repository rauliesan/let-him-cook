package com.daw.mappers;

import com.daw.dtos.request.RecetaRequestDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.entities.IaModelo;
import com.daw.entities.Receta;
import com.daw.entities.TipoComida;
import com.daw.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T18:18:19+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class RecetaMapperImpl implements RecetaMapper {

    @Override
    public Receta toEntity(RecetaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Receta receta = new Receta();

        receta.setTipoComida( recetaRequestDTOToTipoComida( dto ) );
        receta.setIaModelo( recetaRequestDTOToIaModelo( dto ) );
        receta.setAlergenos( dto.getAlergenos() );
        receta.setCalorias( dto.getCalorias() );
        receta.setDescripcion( dto.getDescripcion() );
        receta.setDificultad( dto.getDificultad() );
        receta.setEsPublica( dto.getEsPublica() );
        receta.setImagenUrl( dto.getImagenUrl() );
        receta.setIngredientes( dto.getIngredientes() );
        receta.setNombre( dto.getNombre() );
        receta.setTiempoPreparacion( dto.getTiempoPreparacion() );

        return receta;
    }

    @Override
    public RecetaResponseDTO toResponseDTO(Receta entity) {
        if ( entity == null ) {
            return null;
        }

        RecetaResponseDTO recetaResponseDTO = new RecetaResponseDTO();

        recetaResponseDTO.setTipoComidaId( entityTipoComidaId( entity ) );
        recetaResponseDTO.setTipoComidaNombre( entityTipoComidaNombre( entity ) );
        recetaResponseDTO.setUsuarioCreadorId( entityUsuarioId( entity ) );
        recetaResponseDTO.setUsuarioCreadorNombre( entityUsuarioNombre( entity ) );
        recetaResponseDTO.setIaModeloId( entityIaModeloId( entity ) );
        recetaResponseDTO.setAlergenos( entity.getAlergenos() );
        recetaResponseDTO.setCalorias( entity.getCalorias() );
        recetaResponseDTO.setDescripcion( entity.getDescripcion() );
        recetaResponseDTO.setDificultad( entity.getDificultad() );
        recetaResponseDTO.setEsPublica( entity.getEsPublica() );
        recetaResponseDTO.setFechaCreacion( entity.getFechaCreacion() );
        recetaResponseDTO.setId( entity.getId() );
        recetaResponseDTO.setImagenUrl( entity.getImagenUrl() );
        recetaResponseDTO.setIngredientes( entity.getIngredientes() );
        recetaResponseDTO.setNombre( entity.getNombre() );
        recetaResponseDTO.setTiempoPreparacion( entity.getTiempoPreparacion() );

        return recetaResponseDTO;
    }

    @Override
    public List<RecetaResponseDTO> toListDTO(List<Receta> list) {
        if ( list == null ) {
            return null;
        }

        List<RecetaResponseDTO> list1 = new ArrayList<RecetaResponseDTO>( list.size() );
        for ( Receta receta : list ) {
            list1.add( toResponseDTO( receta ) );
        }

        return list1;
    }

    protected TipoComida recetaRequestDTOToTipoComida(RecetaRequestDTO recetaRequestDTO) {
        if ( recetaRequestDTO == null ) {
            return null;
        }

        TipoComida tipoComida = new TipoComida();

        tipoComida.setId( recetaRequestDTO.getTipoComidaId() );

        return tipoComida;
    }

    protected IaModelo recetaRequestDTOToIaModelo(RecetaRequestDTO recetaRequestDTO) {
        if ( recetaRequestDTO == null ) {
            return null;
        }

        IaModelo iaModelo = new IaModelo();

        iaModelo.setId( recetaRequestDTO.getIaModeloId() );

        return iaModelo;
    }

    private UUID entityTipoComidaId(Receta receta) {
        TipoComida tipoComida = receta.getTipoComida();
        if ( tipoComida == null ) {
            return null;
        }
        return tipoComida.getId();
    }

    private String entityTipoComidaNombre(Receta receta) {
        TipoComida tipoComida = receta.getTipoComida();
        if ( tipoComida == null ) {
            return null;
        }
        return tipoComida.getNombre();
    }

    private UUID entityUsuarioId(Receta receta) {
        Usuario usuario = receta.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombre(Receta receta) {
        Usuario usuario = receta.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombre();
    }

    private UUID entityIaModeloId(Receta receta) {
        IaModelo iaModelo = receta.getIaModelo();
        if ( iaModelo == null ) {
            return null;
        }
        return iaModelo.getId();
    }
}
