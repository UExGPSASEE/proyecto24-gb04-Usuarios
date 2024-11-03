package org.openapitools.api;

import org.ASEE.DBAccess.UsersAccess;
import org.openapitools.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import org.springframework.ui.Model;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("${openapi.tubeFlixGestionDeUsuariosOpenAPI30.base-path:}")
public class UserApiController implements UserApi {

    private final NativeWebRequest request;

    public UserApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Procesa y muestra el JSON recibido en la consola
        System.out.println("Datos del usuario recibido:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + user.getPassword());

        // Aquí podrías agregar lógica adicional, como guardar el usuario en la base de datos
    	UsersAccess cliente = new UsersAccess();
    	System.out.println("Iniciando programa");
    	if(!cliente.dbConectar()) {
    		System.out.println("Conexion fallida");
    	}
    	
        //Guardar user
        cliente.dbAddUser(user);
        
    	
    	if(!cliente.dbDesconectar()) {
    		System.out.println("Desconexión fallida");
    	}

        // Retorna el usuario recibido como respuesta JSON
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
    
    @Override
    public String loginUser(@RequestBody @Valid LoginRequest loginRequest, HttpSession session) {
        
    	System.out.println("/////////////// LOGINUSER ///////////////");
    	
    	String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        UsersAccess cliente = new UsersAccess();
        System.out.println("Iniciando programa");
        if(!cliente.dbConectar()) {
            System.out.println("Conexion fallida");
        }

        // Buscar usuario
        User user = cliente.dbUserbyUsername(username);

        if(!cliente.dbDesconectar()) {
            System.out.println("Desconexión fallida");
        }

        System.out.println("Usuario obtenido: " + user.getUsername() + " Contraseña:" + user.getPassword());
        // Validar usuario y contraseña
        if (user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
        	// Almacenar el objeto `user` en la sesión
            session.setAttribute("user", user);
            System.out.println("Inicio de sesión correcto");
            return "redirect:/user/profile";
        } else {
            // Si las credenciales no son válidas, devuelve un error
            System.out.println("Inicio de sesión fallido");
            return "login";
        }
    }
    
    @GetMapping("/user/profile")
    public String userProfile(HttpSession session, Model model) {

        // Obtiene el objeto user desde la sesión
        User user = (User) session.getAttribute("user");
        if (user != null) {
            System.out.println("Username del modelo: " + user.getUsername());
            model.addAttribute("user", user); // Añadir al modelo para la vista
            
         // Aquí determinamos si el perfil es del usuario actual
            boolean esPropioPerfil = true; 
            model.addAttribute("propio", esPropioPerfil); // Agrega la variable 'propio' al modelo
            
        } else {
            System.out.println("El usuario no está en sesión.");
        }

        return "profile";  // Se refiere a un archivo HTML llamado `profile.html` en `src/main/resources/templates`
    }
    
    @Override
	public String updateUser(HttpSession session,@RequestBody @Valid User user, Model model) {
    	
    	System.out.println("////// updateUser: " + user.getUsername());
    	System.out.println("////// updateUser: " + user.getFirstName());
    	System.out.println("////// updateUser: " + user.getBirthdate());
    	System.out.println("////// updateUser: " + user.getBio());
    	UsersAccess cliente = new UsersAccess();
        System.out.println("Iniciando programa");
        if(!cliente.dbConectar()) {
            System.out.println("Conexion fallida");
        }

        // Actualizar usuario
        cliente.updateDbUser(user);

        if(!cliente.dbDesconectar()) {
            System.out.println("Desconexión fallida");
        }
        
        session.setAttribute("user", user);
        
     // Aquí determinamos si el perfil es del usuario actual
        boolean esPropioPerfil = true; 
        model.addAttribute("propio", esPropioPerfil); // Agrega la variable 'propio' al modelo
    	
    		return "profile";
        }
    
}