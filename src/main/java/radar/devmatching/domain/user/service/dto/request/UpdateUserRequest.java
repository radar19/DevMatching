package radar.devmatching.domain.user.service.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Builder;

public class UpdateUserRequest {

	@NotBlank
	private final String schoolName;
	private final String githubUrl;
	private final String introduce;

	@Builder
	public UpdateUserRequest(String schoolName, String githubUrl, String introduce) {
		this.schoolName = schoolName;
		this.githubUrl = githubUrl;
		this.introduce = introduce;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public String getIntroduce() {
		return introduce;
	}
}
