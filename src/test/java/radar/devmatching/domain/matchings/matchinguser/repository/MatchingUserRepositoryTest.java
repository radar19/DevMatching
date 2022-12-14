package radar.devmatching.domain.matchings.matchinguser.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.matchings.matchinguser.entity.MatchingUser;
import radar.devmatching.domain.post.full.entity.FullPost;
import radar.devmatching.domain.post.simple.entity.PostCategory;
import radar.devmatching.domain.post.simple.entity.Region;
import radar.devmatching.domain.post.simple.entity.SimplePost;
import radar.devmatching.domain.post.simple.repository.SimplePostRepository;
import radar.devmatching.domain.user.entity.User;
import radar.devmatching.domain.user.repository.UserRepository;

@DataJpaTest
// @SpringBootTest
@DisplayName("MatchingUserRepository의")
class MatchingUserRepositoryTest {

	@Autowired
	private MatchingUserRepository matchingUserRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SimplePostRepository simplePostRepository;

	private User createUser() {
		return User.builder()
			.username("username")
			.password("password")
			.nickName("nickName")
			.schoolName("schoolName")
			.githubUrl("githubUrl")
			.introduce("introduce")
			.build();
	}

	private SimplePost createSimplePost(User user, FullPost fullPost, Matching matching) {
		return SimplePost.builder()
			.title("게시글 제목")
			.category(PostCategory.PROJECT)
			.region(Region.BUSAN)
			.userNum(1)
			.leader(user)
			.fullPost(fullPost)
			.matching(matching)
			.build();
	}

	@Test
	@DisplayName("existsByMatchingIdAndUserId 메서드는 matchingId와 userId를 가지는 matchingUser 있는지 확인한다.")
	void existsByMatchingIdAndUserIdMethod() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();

		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		MatchingUser matchingUser = MatchingUser.builder().user(user).matching(matching).build();
		matchingUserRepository.save(matchingUser);
		//when
		boolean check = matchingUserRepository.existsByMatchingIdAndUserId(matching.getId(), user.getId());
		//then
		assertThat(check).isTrue();
	}

	@Test
	@DisplayName("deleteByMatchingIdAndUserId 메서드는 MatchingId와 userId를 가지는 matchingUser를 삭제한다.")
	void deleteByMatchingIdAndUserIdMethod() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		MatchingUser matchingUser = MatchingUser.builder().user(user).matching(matching).build();
		matchingUserRepository.save(matchingUser);

		//when
		matchingUserRepository.deleteByMatchingIdAndUserId(matching.getId(), user.getId());
		//then
		assertThat(matchingUserRepository.findById(matchingUser.getId()).isEmpty()).isTrue();
	}

	@Test
	@DisplayName("findMatchingUserList 메서드는 userId에 해당하는 MatchingUser리스트를 반환한다.")
	void findMatchingUserList() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		MatchingUser matchingUser = MatchingUser.builder().user(user).matching(matching).build();
		matchingUserRepository.save(matchingUser);

		//when
		List<MatchingUser> list = matchingUserRepository.findMatchingUserList(user.getId());
		//then
		assertThat(list.get(0)).usingRecursiveComparison().isEqualTo(matchingUser);
	}

	@Test
	@DisplayName("findByMatchingIdAndUserId 메서드는 matchingId와 userId를 포함하는 MatchingUser를 반환한다.")
	void findByMatchingIdAndUserId() {
		//given
		User user = createUser();
		userRepository.save(user);
		Matching matching = Matching.builder().build();
		SimplePost simplePost = createSimplePost(user, FullPost.builder().content("test").build(), matching);
		simplePostRepository.save(simplePost);
		MatchingUser matchingUser = MatchingUser.builder().user(user).matching(matching).build();
		matchingUserRepository.save(matchingUser);

		//when
		MatchingUser findMatchingUser = matchingUserRepository.findByMatchingIdAndUserId(
			matching.getId(), user.getId()).get();
		//then
		assertThat(findMatchingUser).usingRecursiveComparison().isEqualTo(matchingUser);
	}

}