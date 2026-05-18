package com.daw.services;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.UsuarioLogroRequestDTO;
import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.entities.Usuario;
import com.daw.entities.UsuarioLogro;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.UsuarioLogroMapper;
import com.daw.repositories.FavoritoRecetaRepository;
import com.daw.repositories.LogroRepository;
import com.daw.repositories.PostComentarioRepository;
import com.daw.repositories.PostRepository;
import com.daw.repositories.UsuarioLogroRepository;
import com.daw.repositories.UsuarioRepository;
import com.daw.repositories.UsuarioRecompensaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioLogroService {

    private final UsuarioLogroRepository usuarioLogroRepository;
    private final LogroRepository logroRepository;
    private final UsuarioLogroMapper usuarioLogroMapper;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PostRepository postRepository;
    private final PostComentarioRepository postComentarioRepository;
    private final FavoritoRecetaRepository favoritoRecetaRepository;
    private final UsuarioRecompensaRepository usuarioRecompensaRepository;

    public Page<UsuarioLogroResponseDTO> buscarMisLogros(UUID usuarioId, Pageable pageable) {
        Page<UsuarioLogro> page = usuarioLogroRepository.findByUsuarioId(usuarioId, pageable);
        return usuarioLogroMapper.toPageDTO(page);
    }

    /**
     * Verifica todas las condiciones de logros para el usuario y concede
     * los que aún no tenga. Se llama tras cualquier acción relevante.
     */
    public void verificarLogros(UUID usuarioId) {
        int recetas  = usuarioRepository.countRecetasCompletadas(usuarioId);
        long posts   = postRepository.countByUsuarioId(usuarioId);
        long comts   = postComentarioRepository.countByUsuarioId(usuarioId);
        long likes   = favoritoRecetaRepository.countByUsuarioId(usuarioId);
        long premios = usuarioRecompensaRepository.countByUsuarioId(usuarioId);
        int amigos   = usuarioRepository.countAmigos(usuarioId);

        intentarConceder("Primera receta",        recetas >= 1,  usuarioId);
        intentarConceder("Cocinero en prácticas", recetas >= 5,  usuarioId);
        intentarConceder("Chef experimentado",    recetas >= 10, usuarioId);
        intentarConceder("Escritor",              posts   >= 1,  usuarioId);
        intentarConceder("Primer comentario",     comts   >= 1,  usuarioId);
        intentarConceder("Foodie",                likes   >= 1,  usuarioId);
        intentarConceder("Primera tirada",        premios >= 1,  usuarioId);
        intentarConceder("Coleccionista",         premios >= 5,  usuarioId);
        intentarConceder("Sociable",              amigos  >= 1,  usuarioId);
    }

    private void intentarConceder(String nombreLogro, boolean condicion, UUID usuarioId) {
        if (!condicion) return;
        Optional<Logro> logro = logroRepository.findByNombre(nombreLogro);
        if (logro.isEmpty()) return;
        if (usuarioLogroRepository.existsByUsuarioIdAndLogroId(usuarioId, logro.get().getId())) return;

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);
        UsuarioLogro ul = new UsuarioLogro();
        ul.setUsuario(usuario);
        ul.setLogro(logro.get());
        usuarioLogroRepository.save(ul);
        log.info("Logro '{}' concedido a usuario {}", nombreLogro, usuarioId);
    }

    public UsuarioLogroResponseDTO concederLogro(UsuarioLogroRequestDTO dto, UUID usuarioId) {
        if (usuarioLogroRepository.existsByUsuarioIdAndLogroId(usuarioId, dto.getLogroId())) {
            throw new RecursoDuplicadoException("El usuario ya posee este logro en su vitrina.");
        }

        Logro logro = logroRepository.findById(dto.getLogroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Logro no encontrado con ID: " + dto.getLogroId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        UsuarioLogro usuarioLogro = new UsuarioLogro();
        usuarioLogro.setUsuario(usuario);
        usuarioLogro.setLogro(logro);

        usuarioLogro = usuarioLogroRepository.save(usuarioLogro);
        return usuarioLogroMapper.toResponseDTO(usuarioLogro);
    }
}
