package radar.devmatching.domain.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;
import radar.devmatching.domain.user.service.dto.request.CreateUserRequest;
import radar.devmatching.domain.user.service.dto.request.UpdateUserRequest;
import radar.devmatching.domain.user.service.dto.response.UserResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserResponse createUser(CreateUserRequest request) {
		checkDuplicateUsername(request.getUsername());
		checkDuplicateNickName(request.getNickName(), null);

		String encodePassword = passwordEncoder.encode(request.getPassword());
		request.setPassword(encodePassword);
		User signUpUser = CreateUserRequest.toEntity(request);

		userRepository.save(signUpUser);
		log.info("create user={}", signUpUser);

		return UserResponse.of(signUpUser);
	}

	@Override
	public UserResponse getUser(Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);
		return UserResponse.of(authUser);
	}

	@Override
	@Transactional
	public UserResponse updateUser(UpdateUserRequest request, Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);

		changeNickName(request.getNickName(), authUser);
		authUser.changeSchoolName(request.getSchoolName());
		authUser.changeGithubUrl(request.getGithubUrl());
		authUser.changeIntroduce(request.getIntroduce());

		return UserResponse.of(authUser);
	}

	@Override
	@Transactional
	public void deleteUser(Long requestUserId, User authUser) {
		validatePermission(requestUserId, authUser);
		userRepository.delete(authUser);
	}

	private void changeNickName(String nickName, User user) {
		if (user.getNickName().equals(nickName)) {
			return;
		}
		checkDuplicateNickName(nickName, user.getId());
		user.changeNickName(nickName);
	}

	/**
	 * 아이디 중복 확인, 회원가입시에만 사용
	 * BusinessException 정의되면 예외 변경 예정
	 */
	private void checkDuplicateUsername(String username) {
		userRepository.findByUsername(username).ifPresent(user -> {
			throw new RuntimeException("Username already exist");
		});
	}

	/**
	 * 닉네임 중복 확인
	 * BusinessException 정의되면 예외 변경 예정
	 */
	private void checkDuplicateNickName(String nickName, Long userId) {
		userRepository.findByNickName(nickName).ifPresent(user -> {
			if (!Objects.equals(user.getId(), userId) || userId == null) {
				throw new RuntimeException("nickName already exist");
			}
		});
	}

	/**
	 * 사용자가 접근하는 userId가 사용자 아이디와 일치하는지 감사
	 * @param requestUserId
	 * @param authUser
	 */
	private void validatePermission(Long requestUserId, User authUser) {
		if (!Objects.equals(requestUserId, authUser.getId())) {
			throw new RuntimeException("request userId is inaccessible Id");
		}
	}

}
