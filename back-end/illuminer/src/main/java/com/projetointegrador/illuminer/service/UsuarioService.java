package com.projetointegrador.illuminer.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetointegrador.illuminer.model.Usuario;
import com.projetointegrador.illuminer.model.UsuarioLogin;
import com.projetointegrador.illuminer.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Usuario cadastrar(Usuario usuario) throws IllegalArgumentException { 
		
		Optional<Usuario> usuarioEmail =  repository.findByEmail(usuario.getEmail());
		
		if(usuarioEmail.isPresent()) {
			throw new IllegalArgumentException("E-mail já cadastrado");
		}
		
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		usuario.setSenha(encoder.encode(usuario.getSenha()));
		usuario = repository.save(usuario);
		usuario.setPostagens(null);
		return usuario;
	}
	
	public Optional<UsuarioLogin> logar(Optional<UsuarioLogin> usuarioLogin){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuarioDB = repository.findByEmail(usuarioLogin.get().getEmail());
		if (usuarioDB.isPresent()) {
			if (encoder.matches(usuarioLogin.get().getSenha(), usuarioDB.get().getSenha())) {
				String auth = usuarioLogin.get().getEmail() + ":" + usuarioLogin.get().getSenha();
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				usuarioLogin.get().setToken("Basic " + new String(encodedAuth));
				usuarioLogin.get().setNome(usuarioDB.get().getNome());
				return usuarioLogin;
			}
		}
		return Optional.empty();
	}
}