package radar.devmatching.common.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.BusinessException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.common.security.JwtProperties;
import radar.devmatching.common.security.jwt.exception.ExpiredAccessTokenException;
import radar.devmatching.common.security.jwt.exception.ExpiredRefreshTokenException;
import radar.devmatching.common.security.jwt.exception.InvalidTokenException;
import radar.devmatching.domain.user.entity.UserRole;

@Slf4j
@Component
public class JwtTokenProvider {

	private final long ACCESS_TOKEN_EXPIRE_TIME;
	private final long REFRESH_TOKEN_EXPIRE_TIME;

	private final Key key;

	private final UserDetailsService userDetailsService;

	public JwtTokenProvider(@Value("${jwt.access-token-expire-time}") long accessTime,
		@Value("${jwt.refresh-token-expire-time}") long refreshTime,
		@Value("${jwt.secret}") String secretKey,
		UserDetailsService userDetailsService) {
		this.ACCESS_TOKEN_EXPIRE_TIME = accessTime;
		this.REFRESH_TOKEN_EXPIRE_TIME = refreshTime;
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.userDetailsService = userDetailsService;
	}

	protected String createToken(String username, UserRole userRole, long tokenValid) {
		Claims claims = Jwts.claims().setSubject(username);

		claims.put(JwtProperties.ROLE, userRole);

		Date date = new Date();

		// TODO : accessToken, refreshToken ???????????? ?????? ????????????.
		return Jwts.builder()
			.setClaims(claims) // ?????? ?????? ?????? ??????
			.setIssuedAt(date) // ?????? ?????? ??????
			.setExpiration(new Date(date.getTime() + tokenValid)) // ?????? ?????? ??????
			.signWith(SignatureAlgorithm.HS512, key)
			.compact();// ??????????????? ??? ??????
	}

	public String createAccessToken(String username, UserRole userRole) {
		return createToken(username, userRole, ACCESS_TOKEN_EXPIRE_TIME);
	}

	public String createRefreshToken(String username, UserRole userRole) {
		return createToken(username, userRole, REFRESH_TOKEN_EXPIRE_TIME);
	}

	public String createNewAccessTokenFromRefreshToken(String refreshToken) {
		Claims claims = parseClaims(refreshToken);
		String username = claims.getSubject();
		UserRole role = UserRole.valueOf((String)claims.get(JwtProperties.ROLE));
		return createAccessToken(username, role);
	}

	/**
	 * ?????? maxAge??? ????????? ???????????? 1000?????? ??????????????? ??????
	 * ?????? ????????? 1????????? ??????????????????
	 */
	public long getExpireTime() {
		return REFRESH_TOKEN_EXPIRE_TIME / 1000;
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);
		log.info("user role={}", claims.get(JwtProperties.ROLE).toString());
		if (claims.get(JwtProperties.ROLE) == null || !StringUtils.hasText(claims.get(JwtProperties.ROLE).toString())) {
			throw new BusinessException(ErrorMessage.AUTHORITY_NOT_FOUND); //??????????????????
		}

		log.info("access claims : username={}, authority={}", claims.getSubject(), claims.get(JwtProperties.ROLE));

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(JwtProperties.ROLE).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// TODO : principal ??? ???????????? ??????

		return new JwtAuthenticationToken(claims.getSubject(), "", authorities);
	}

	public void validAccessToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredAccessTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException(e);
		}
	}

	public void validRefreshToken(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredRefreshTokenException();
		} catch (Exception e) {
			throw new InvalidTokenException(e);
		}
	}

	public Claims parseClaims(String accessToken) {
		return Jwts.parser()
			.setSigningKey(key)
			.parseClaimsJws(accessToken)
			.getBody();
	}

}
