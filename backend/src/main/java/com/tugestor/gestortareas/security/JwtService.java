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
	@Value("${jwt.access-token-expiration-ms:900000}")
	private long accessTokenExpirationMilliseconds;
	@Value("${jwt.refresh-token-expiration-ms:604800000}")
	private long refreshTokenExpirationMilliseconds;
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	private static final String TOKEN_TYPE_CLAIM = "type";
	private static final String ACCESS_TOKEN_TYPE = "access";
	private static final String REFRESH_TOKEN_TYPE = "refresh";

	public String generateToken(UserDetails userDetails) {
		return generateAccessToken(userDetails);
	}

	public String generateAccessToken(UserDetails userDetails) {
		return generateToken(userDetails, ACCESS_TOKEN_TYPE, accessTokenExpirationMilliseconds);
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return generateToken(userDetails, REFRESH_TOKEN_TYPE, refreshTokenExpirationMilliseconds);
	}

	private String generateToken(UserDetails userDetails, String tokenType, long expirationMilliseconds) {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + expirationMilliseconds);
		return Jwts.builder()
				.setSubject(userDetails.getUsername())	// Identificador del usuario
				.claim(TOKEN_TYPE_CLAIM, tokenType)
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

	public String extractTokenType(String token) {
		return Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody()
				.get(TOKEN_TYPE_CLAIM, String.class);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public boolean isAccessToken(String token) {
		return ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
	}

	public boolean isRefreshToken(String token) {
		return REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
	}

	public boolean isAccessTokenValid(String token, UserDetails userDetails) {
		return isTokenValid(token, userDetails) && isAccessToken(token);
	}

	public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
		return isTokenValid(token, userDetails) && isRefreshToken(token);
	}


}
