package com.daw.mappers;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.entities.Recompensa;
import com.daw.entities.UsuarioRecompensa;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T17:36:03+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class RecompensaMapperImpl implements RecompensaMapper {

    @Override
    public Recompensa toEntity(RecompensaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Recompensa recompensa = new Recompensa();

        recompensa.setDescripcion( dto.getDescripcion() );
        recompensa.setNombre( dto.getNombre() );
        recompensa.setProbabilidad( dto.getProbabilidad() );

        return recompensa;
    }

    @Override
    public RecompensaResponseDTO toResponseDTO(Recompensa entity) {
        if ( entity == null ) {
            return null;
        }

        RecompensaResponseDTO recompensaResponseDTO = new RecompensaResponseDTO();

        recompensaResponseDTO.setDescripcion( entity.getDescripcion() );
        recompensaResponseDTO.setId( entity.getId() );
        recompensaResponseDTO.setNombre( entity.getNombre() );
        recompensaResponseDTO.setProbabilidad( entity.getProbabilidad() );

        return recompensaResponseDTO;
    }

    @Override
    public RecompensaResponseDTO toResponseDTO(UsuarioRecompensa entity) {
        if ( entity == null ) {
            return null;
        }

        RecompensaResponseDTO recompensaResponseDTO = new RecompensaResponseDTO();

        recompensaResponseDTO.setId( entityRecompensaId( entity ) );
        recompensaResponseDTO.setNombre( entityRecompensaNombre( entity ) );
        recompensaResponseDTO.setDescripcion( entityRecompensaDescripcion( entity ) );
        recompensaResponseDTO.setProbabilidad( entityRecompensaProbabilidad( entity ) );
        recompensaResponseDTO.setFechaObtenida( entity.getFechaObtenida() );

        return recompensaResponseDTO;
    }

    @Override
    public List<RecompensaResponseDTO> toListDTO(List<Recompensa> list) {
        if ( list == null ) {
            return null;
        }

        List<RecompensaResponseDTO> list1 = new ArrayList<RecompensaResponseDTO>( list.size() );
        for ( Recompensa recompensa : list ) {
            list1.add( toResponseDTO( recompensa ) );
        }

        return list1;
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

    private String entityRecompensaDescripcion(UsuarioRecompensa usuarioRecompensa) {
        Recompensa recompensa = usuarioRecompensa.getRecompensa();
        if ( recompensa == null ) {
            return null;
        }
        return recompensa.getDescripcion();
    }

    private Double entityRecompensaProbabilidad(UsuarioRecompensa usuarioRecompensa) {
        Recompensa recompensa = usuarioRecompensa.getRecompensa();
        if ( recompensa == null ) {
            return null;
        }
        return recompensa.getProbabilidad();
    }
}
