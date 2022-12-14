package radar.devmatching.domain.user.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.JwtCookieProvider;
import radar.devmatching.common.security.jwt.JwtTokenCookieInfo;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.service.AuthService;
import radar.devmatching.domain.user.service.dto.request.SignInRequest;
import radar.devmatching.domain.user.service.dto.response.SignInResponse;
import radar.devmatching.domain.user.service.dto.response.SignOutResponse;

/**
 *  TODO : RestController -> Controller 변경
 */
@Slf4j
@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/signIn")
	public String signInPage(Model model) {
		model.addAttribute("signInRequest", SignInRequest.of());
		return "user/signIn";
	}

	@PostMapping("/signIn")
	public String signIn(@Valid @ModelAttribute SignInRequest signInRequest,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user/signIn";
		}
		SignInResponse signInResponse = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());

		JwtTokenCookieInfo accessToken = signInResponse.getAccessToken();
		JwtTokenCookieInfo refreshToken = signInResponse.getRefreshToken();

		ResponseCookie accessTokenCookie = JwtCookieProvider.createCookie(accessToken.getHeader(),
			accessToken.getToken(),
			accessToken.getExpireTime());
		ResponseCookie refreshTokenCookie = JwtCookieProvider.createCookie(refreshToken.getHeader(),
			refreshToken.getToken(),
			refreshToken.getExpireTime());

		JwtCookieProvider.setCookie(accessTokenCookie, refreshTokenCookie);
		log.info("signIn execute");
		return "redirect:/";
	}

	@GetMapping("/signOut")
	public String signOut(@AuthUser JwtTokenInfo tokenInfo) {
		SignOutResponse signOutResponse = authService.signOut();
		log.info("access User={}", tokenInfo);
		log.info("logout process execute");
		JwtCookieProvider.deleteCookieFromRequest(signOutResponse.getAccessTokenHeader(),
			signOutResponse.getRefreshTokenHeader());

		return "redirect:/api/users/signIn";
	}

}
