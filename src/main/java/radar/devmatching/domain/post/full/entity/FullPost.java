package radar.devmatching.domain.post.full.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import radar.devmatching.common.entity.BaseEntity;
import radar.devmatching.common.util.ExcludeJacocoGenerated;
import radar.devmatching.domain.comment.entity.MainComment;
import radar.devmatching.domain.post.simple.entity.SimplePost;

@Table(name = "FULL_POST")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FullPost extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "full_post_id")
	private Long id;

	private String content;

	@OneToOne(mappedBy = "fullPost", fetch = FetchType.LAZY)
	private SimplePost simplePost;

	@OneToMany(mappedBy = "fullPost", orphanRemoval = true)
	private List<MainComment> mainComments;

	@Builder
	public FullPost(String content) {
		this.content = content;
		this.mainComments = new ArrayList<>();
	}

	public void setSimplePost(SimplePost simplePost) {
		this.simplePost = simplePost;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	@ExcludeJacocoGenerated
	@Override
	public String toString() {
		return "FullPost{" +
			"id=" + id +
			", content='" + content + '\'' +
			", simplePost=" + simplePost.getClass() +
			", mainComments=" + mainComments +
			'}';
	}
}
