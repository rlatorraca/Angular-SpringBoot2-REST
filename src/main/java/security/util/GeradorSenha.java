package security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorSenha {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
		System.out.println("m0b1l30 : " + encoder.encode("m0b1l30"));
		System.out.println("@ngul@r : " + encoder.encode("@ngul@r"));
		System.out.println("admin : " + encoder.encode("admin"));
		System.out.println("rodrigo : " + encoder.encode("rodrigo"));
	}
	
}
