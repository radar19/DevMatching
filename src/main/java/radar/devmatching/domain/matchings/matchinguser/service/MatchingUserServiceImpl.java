package radar.devmatching.domain.matchings.matchinguser.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import radar.devmatching.common.exception.EntityNotFoundException;
import radar.devmatching.common.exception.error.ErrorMessage;
import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUserRole;
import radar.devmatching.domain.matchings.matchinguser.exception.AlreadyJoinMatchingUserException;
import radar.devmatching.domain.matchings.matchinguser.repository.MatchingUserRepository;
import radar.devmatching.domain.matchings.matchinguser.service.dto.response.MatchingUserResponse;
import radar.devmatching.domain.user.entity.User;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingUserServiceImpl implements MatchingUserService {

	private final MatchingUserRepository matchingUserRepository;

	@Override
	@Transactional
	public MatchingUser createMatchingUser(Matching matching, User user) {

		if (matchingUserRepository.existsByMatchingIdAndUserId(matching.getId(), user.getId())) {
			throw new AlreadyJoinMatchingUserException(ErrorMessage.ALREADY_JOIN);
		}
		MatchingUser matchingUser = new MatchingUser(MatchingUserRole.USER, user, matching);
		matchingUserRepository.save(matchingUser);
		return matchingUser;
	}

	@Override
	public List<MatchingUserResponse> getMatchingUserList(Long userId) {
		return matchingUserRepository.findMatchingUserList(userId).stream()
			.map(MatchingUserResponse::of)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteMatchingUser(Long matchingId, Long userId) {
		matchingUserRepository.deleteByMatchingIdAndUserId(matchingId, userId);
	}

	public MatchingUser findByMatchingIdAndUserId(Long matchingId, Long userId) {
		return matchingUserRepository.findByMatchingIdAndUserId(matchingId, userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.MATCHING_USER_NOT_FOUND));
	}

}
