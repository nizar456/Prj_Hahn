package Back_End.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration}")
  private long expirationMs;

  private Key key() {
    // Use UTF-8 bytes directly; secret length (>=32 chars) ensures HS256 key strength
    //Transforme ces bytes en clé cryptographique de type Key, compatible avec HS256.
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String subject, Map<String, Object> claims) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, io.jsonwebtoken.Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<io.jsonwebtoken.Claims, T> claimsResolver) {
    var claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    return claimsResolver.apply(claims);
  }

  public boolean isTokenValid(String token, String username) {
    String subject = extractUsername(token);
    Date expiration = extractClaim(token, io.jsonwebtoken.Claims::getExpiration);
    return subject.equals(username) && expiration.after(new Date());
  }
}
