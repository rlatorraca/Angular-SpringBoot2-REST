package com.rlsp.moneyapi.security;



import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rlsp.moneyapi.model.Usuario;
import com.rlsp.moneyapi.repository.UsuarioRepository;
/**
 * Busca um USUARIO no DB, trazendo seu USARIO, SENHA e suas ROLES
 * @author rlatorraca
 *
 */

@Service
public class AppUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository  usuarioRespository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Usuario> usuarioOptional = usuarioRespository.findByEmail(email);
		Usuario usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuario e/ senha nao encontrado"));
		System.out.println("-----------------" + email + ", " + usuario.getSenha() + ", " + getAuthorities(usuario));
		return new User(email, usuario.getSenha(), getAuthorities(usuario));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>(); // Set para as Permissoes
		usuario.getPermissoes()
			.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase()))); // Carrega as permissoes do USUARIO dentro do SET
		return authorities;
	}

}
