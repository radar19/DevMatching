package radar.devmatching.domain.matchings.matching.service;

import radar.devmatching.domain.matchings.matching.entity.Matching;
import radar.devmatching.domain.post.entity.SimplePost;
import radar.devmatching.domain.user.entity.User;

public interface MatchingService {

	Matching createMatching(User authUser, SimplePost simplePost);
}
