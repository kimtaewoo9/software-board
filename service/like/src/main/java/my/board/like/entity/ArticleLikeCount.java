package my.board.like.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_like_count")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLikeCount {

	@Id
	private Long articleId;
	private Long likeCount;

	@Version
	private Long version; // 낙관적 락을 위한 version

	// 처음에는 데이터가 없어서 init 을 해줘야함 .
	public static ArticleLikeCount init(Long articleId, Long likeCount) {
		ArticleLikeCount articleLikeCount = new ArticleLikeCount();
		articleLikeCount.articleId = articleId;
		articleLikeCount.likeCount = likeCount;
		articleLikeCount.version = 0L;

		return articleLikeCount;
	}

	public void increase() {
		this.likeCount++;
	}

	public void decrease() {
		this.likeCount--;
	}
}
