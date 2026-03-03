package com.daw.mappers;

import com.daw.dtos.request.IaModeloRequestDTO;
import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.entities.Api;
import com.daw.entities.IaModelo;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T13:23:50+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class IaModeloMapperImpl implements IaModeloMapper {

    @Override
    public IaModelo toEntity(IaModeloRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        IaModelo iaModelo = new IaModelo();

        iaModelo.setApi( iaModeloRequestDTOToApi( dto ) );
        iaModelo.setNombreModelo( dto.getNombreModelo() );

        return iaModelo;
    }

    @Override
    public IaModeloResponseDTO toResponseDTO(IaModelo entity) {
        if ( entity == null ) {
            return null;
        }

        IaModeloResponseDTO iaModeloResponseDTO = new IaModeloResponseDTO();

        iaModeloResponseDTO.setApiId( entityApiId( entity ) );
        iaModeloResponseDTO.setApiNombreServicio( entityApiNombreServicio( entity ) );
        iaModeloResponseDTO.setApiEndpointUrl( entityApiEndpointUrl( entity ) );
        iaModeloResponseDTO.setApiKey( entityApiApiKey( entity ) );
        iaModeloResponseDTO.setId( entity.getId() );
        iaModeloResponseDTO.setNombreModelo( entity.getNombreModelo() );

        return iaModeloResponseDTO;
    }

    protected Api iaModeloRequestDTOToApi(IaModeloRequestDTO iaModeloRequestDTO) {
        if ( iaModeloRequestDTO == null ) {
            return null;
        }

        Api api = new Api();

        api.setId( iaModeloRequestDTO.getApiId() );

        return api;
    }

    private UUID entityApiId(IaModelo iaModelo) {
        Api api = iaModelo.getApi();
        if ( api == null ) {
            return null;
        }
        return api.getId();
    }

    private String entityApiNombreServicio(IaModelo iaModelo) {
        Api api = iaModelo.getApi();
        if ( api == null ) {
            return null;
        }
        return api.getNombreServicio();
    }

    private String entityApiEndpointUrl(IaModelo iaModelo) {
        Api api = iaModelo.getApi();
        if ( api == null ) {
            return null;
        }
        return api.getEndpointUrl();
    }

    private String entityApiApiKey(IaModelo iaModelo) {
        Api api = iaModelo.getApi();
        if ( api == null ) {
            return null;
        }
        return api.getApiKey();
    }
}
