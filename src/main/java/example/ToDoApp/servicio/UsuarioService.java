package example.ToDoApp.servicio;

import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.model.Usuario;
import example.ToDoApp.repositorio.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {
        LOGIN_OK, USER_NOT_FOUND, USER_BLOCKED, ERROR_PASSWORD
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (Boolean.TRUE.equals(usuario.get().getBloqueado())) {
            return LoginStatus.USER_BLOCKED;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuarioRepository.existsByEmail(usuario.getEmail()))
            throw new UsuarioServiceException("El usuario registrado con ese correo ya existe");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else if (usuario.getEsAdministrador() && usuarioRepository.existsByEsAdministradorTrue()) {
            throw new UsuarioServiceException("Ya existe un usuario con es administrador");
        } else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
            usuarioNuevo.setApellido(usuario.getApellido());
            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null)
            return null;
        UsuarioData dto = modelMapper.map(usuario, UsuarioData.class);
        logger.info("findByEmail: usuario {} esAdministrador: {}", email, dto.getEsAdministrador());
        System.out.println("usuario " + usuario.getEmail() + " esAdministrador: " + dto.getEsAdministrador());
        return dto;
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void Bloqueo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("Usuario no encontrado"));
        usuario.setBloqueado(!Boolean.TRUE.equals(usuario.getBloqueado()));
        usuarioRepository.save(usuario);
    }

}
