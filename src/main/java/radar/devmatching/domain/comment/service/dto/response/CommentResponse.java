package radar.devmatching.domain.comment.service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import radar.devmatching.domain.comment.entity.Comment;
import radar.devmatching.domain.user.service.dto.response.SimpleUserResponse;

@Builder(access = AccessLevel.PRIVATE)
public class CommentResponse {

	private final Long commentId;

	private final SimpleUserResponse simpleUserResponse;

	private final String content;

	public static CommentResponse of(Comment comment, Long commentId) {
		return CommentResponse.builder()
			.commentId(commentId)
			.simpleUserResponse(SimpleUserResponse.of(comment.getUser()))
			.content(comment.getContent())
			.build();
	}

	public Long getCommentId() {
		return commentId;
	}

	public SimpleUserResponse getSimpleUserResponse() {
		return simpleUserResponse;
	}

	public String getContent() {
		return content;
	}
}
