package com.tugestor.gestortareas.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {
	@Value("${jwt.secret}")
	private String secretKey;
	private static final long EXPIRATION_TIME_MILLISECONDS = 24*60*60*1000;	// 24 horas
//	private static final long EXPIRATION_TIME_MILLISECONDS = 60*1000;	// 1 minuto para pruebas
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	
	public String generateToken(UserDetails userDetails) {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + EXPIRATION_TIME_MILLISECONDS);
		return Jwts.builder()
				.setSubject(userDetails.getUsername())	// Identificador del usuario
				.setIssuedAt(issuedAt)					// Cuando se ha creado
				.setExpiration(expiration)				// Cuando expira
				.signWith(SIGNATURE_ALGORITHM, secretKey)//Firmo el token con HS256 y mi clave secreta
				.compact();								// Creo el String dinal
	}
	
	public String extractUsername(String token) {
		return Jwts.parser()				// Inicia la lectura
				.setSigningKey(secretKey)	// Indica la variable que valida la firma
				.parseClaimsJws(token)		// Parsea el token y valida la firma
				.getBody()					// Extrae los datos
				.getSubject();				// Extrae el identificador del usuario
	}
	
	public Date extractExpiration(String token) {
		return Jwts.parser()	// Extraigo la fecha de expiracion parseada
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody()
				.getExpiration();
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}


}
