package com.daw.mappers;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.entities.Recompensa;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:25:38+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
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
}
