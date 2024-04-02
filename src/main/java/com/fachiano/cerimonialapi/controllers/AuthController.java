package com.fachiano.cerimonialapi.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fachiano.cerimonialapi.dto.LoginRequestDTO;
import com.fachiano.cerimonialapi.dto.RegisterRequestDTO;
import com.fachiano.cerimonialapi.dto.ResponseDTO;
import com.fachiano.cerimonialapi.infra.security.TokenService;
import com.fachiano.cerimonialapi.models.Usuario;
import com.fachiano.cerimonialapi.repositories.UsuarioRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	TokenService tokenService;
	
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body) {
		
		Usuario usuario = this.usuarioRepository.findByEmail(body.email()).orElseThrow(()->new RuntimeException("Usuário não encontrado"));
		
		if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
			String token = this.tokenService.generateToken(usuario);
			return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token));
		}
		return ResponseEntity.badRequest().build();
	}
	
	 @PostMapping("/register")
	    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO body){
	        Optional<Usuario> user = this.usuarioRepository.findByEmail(body.email());

	        if(user.isEmpty()) {
	            Usuario novo = new Usuario();
	            novo.setSenha(passwordEncoder.encode(body.senha()));
	            novo.setEmail(body.email());
	            novo.setNome(body.nome());
	            this.usuarioRepository.save(novo);

	            String token = this.tokenService.generateToken(novo);
	            return ResponseEntity.ok(new ResponseDTO(novo.getNome(), token));
	        }
	        return ResponseEntity.badRequest().build();
	    }
	 
	 @GetMapping("/teste")
	 public String teste() {
		 return "ok";
	 }
	
	
}
