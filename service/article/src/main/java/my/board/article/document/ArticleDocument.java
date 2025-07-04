package my.board.article.document;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "articles")
@Setting(settingPath = "/elasticsearch/article-settings.json")
@Getter
@Setter
@ToString
public class ArticleDocument {

	@Id
	private Long articleId;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "articles_analyzer"),
		otherFields = {
			@InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
		}
	)
	private String title;

	@Field(type = FieldType.Text, analyzer = "articles_analyzer")
	private String content;

	@Field(type = FieldType.Long)
	private Long boardId;

	@Field(type = FieldType.Long)
	private Long writerId;

	@Field(type = FieldType.Date)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Date)
	private LocalDateTime updatedAt;

	public static ArticleDocument create(Long articleId, String title, String content,
		Long boardId, Long writerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
		ArticleDocument articleDocument = new ArticleDocument();
		articleDocument.articleId = articleId;
		articleDocument.title = title;
		articleDocument.content = content;
		articleDocument.boardId = boardId;
		articleDocument.writerId = writerId;
		articleDocument.createdAt = createdAt;
		articleDocument.updatedAt = updatedAt;
		return articleDocument;
	}
}
