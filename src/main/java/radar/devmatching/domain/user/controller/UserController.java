package radar.devmatching.domain.user.controller;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.security.jwt.JwtTokenInfo;
import radar.devmatching.common.security.resolver.AuthUser;
import radar.devmatching.domain.user.service.UserService;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Slf4j
@Controller
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@SessionAttributes("createUserRequest")
public class UserController {

	private static final String CREAT_USER_REQUEST = "createUserRequest";
	private static final String USER_INFO = "userInfo";
	private static final String SIMPLE_USER_INFO = "simpleUserInfo";

	private final UserService userService;

	@GetMapping(value = "/signUp")
	public String signUp(Model model) {
		model.addAttribute(CREAT_USER_REQUEST, CreateUserRequest.of());
		return "redirect:/api/users/signUp/page";
	}

	@GetMapping(value = "/signUp/page")
	public String signUpPage(@ModelAttribute(CREAT_USER_REQUEST) CreateUserRequest request, Model model) {
		model.addAttribute(CREAT_USER_REQUEST, request);
		return "user/signUp";
	}

	@PostMapping(value = "/signUp/page")
	public String signUpRequest(@Valid @ModelAttribute(CREAT_USER_REQUEST) CreateUserRequest request,
		BindingResult bindingResult, Model model, SessionStatus sessionStatus) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(CREAT_USER_REQUEST, request);
			return "user/signUp";
		}
		userService.createUser(request);

		sessionStatus.setComplete();
		return "redirect:/api/users/signIn";
	}

	/**
	 * ?????? username ????????????
	 * ?????? ????????? : ??????????????? ??????????????????.
	 * ?????? ?????? : ???????????? ????????? ?????????.
	 * TODO : ????????? ????????????
	 */
	@PostMapping("/duplicate/username")
	public String checkDuplicateUsername(@ModelAttribute(CREAT_USER_REQUEST) CreateUserRequest request, Model model,
		RedirectAttributes redirectAttributes) {
		userService.checkDuplicateUsername(request);
		redirectAttributes.addFlashAttribute("msg", request.getUsername() + ": ??????????????? ????????? ?????????.");
		model.addAttribute(CREAT_USER_REQUEST, request);
		return "redirect:/api/users/signUp/page";
	}

	/**
	 * ?????? nickName ????????????
	 * ?????? ????????? : ??????????????? ???????????????.
	 * ?????? ?????? : ???????????? ???????????????.
	 * TODO : ????????? ????????????
	 */
	@PostMapping("/duplicate/nickName")
	public String checkDuplicateNickName(@ModelAttribute(CREAT_USER_REQUEST) CreateUserRequest request, Model model,
		RedirectAttributes redirectAttributes) {
		userService.checkDuplicateNickName(request);
		redirectAttributes.addFlashAttribute("msg", request.getNickName() + ": ??????????????? ?????? ?????????.");
		model.addAttribute(CREAT_USER_REQUEST, request);
		return "redirect:/api/users/signUp/page";
	}

	@GetMapping
	public String getUser(@AuthUser JwtTokenInfo tokenInfo, Model model) {
		UserResponse user = userService.getUser(tokenInfo.getUserId());
		model.addAttribute(USER_INFO, user);
		return "user/userInfo";
	}

	@GetMapping("/{userId}")
	public String getSimpleUser(@PathVariable Long userId, @AuthUser JwtTokenInfo jwtTokenInfo, Model model) {
		if (Objects.equals(userId, jwtTokenInfo.getUserId())) {
			return "redirect:/api/users";
		}
		model.addAttribute(SIMPLE_USER_INFO, userService.getSimpleUser(userId));
		return "user/simpleUserInfo";
	}

	@GetMapping("/update")
	public String updateUser(@AuthUser JwtTokenInfo tokenInfo, Model model) {
		UserResponse user = userService.getUser(tokenInfo.getUserId());
		model.addAttribute(USER_INFO, user);
		return "user/userUpdate";
	}

	/**
	 * TODO : schoolname ???????????? ???????????? valid ??????????????? exception ?????? ??????????????? ?????????
	 */
	@PostMapping("/update")
	public String updateUser(@Valid @ModelAttribute UpdateUserRequest request, BindingResult bindingResult,
		@AuthUser JwtTokenInfo tokenInfo) {
		if (bindingResult.hasErrors()) {
			return "redirect:/api/users/update";
		}
		userService.updateUser(request, tokenInfo.getUserId());
		return "redirect:/api/users";
	}

	/**
	 * batch ???????????? ???????????? ???????????? ?????? ???????????? ??????
	 * ex: user??? ?????? ?????? ??????????????? ???????????? ??????
	 */
	@GetMapping("/delete")
	public String deleteUser(@AuthUser JwtTokenInfo jwtTokenInfo) {
		userService.deleteUser(jwtTokenInfo.getUserId());
		return "redirect:/api/users/signIn";
	}

	// TODO : ???????????? ??????, ????????? ?????? ????????? ?????????. ???????????? ??????,
}
