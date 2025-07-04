package my.board.article.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import my.board.article.document.ArticleDocument;
import my.board.article.repository.ArticleDocumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSearchService {

	private final ArticleDocumentRepository articleDocumentRepository;
	private final ElasticsearchOperations elasticsearchOperations;

	public List<ArticleDocument> searchArticles(
		String query,
		Long boardId,
		int page,
		int pageSize
	) {
		// must
		Query multiMatchQuery = MultiMatchQuery.of(m -> m
			.query(query)
			.fields("title^3", "content^1")
			.fuzziness("AUTO") // 오타 허용
		)._toQuery();

		// filter
		Query boardFilter = TermQuery.of(t -> t
			.field("boardId")
			.value(boardId)
		)._toQuery();

		// must filter 로 bool query
		Query boolQuery = BoolQuery.of(b -> b
			.must(multiMatchQuery)
			.filter(boardFilter)
		)._toQuery();

		// highlight 만들고 .
		HighlightParameters highlightParameters = HighlightParameters.builder()
			.withPostTags("<mark>")
			.withPostTags("</mark>")
			.build();
		Highlight highlight = new Highlight(highlightParameters,
			List.of(new HighlightField("title"), new HighlightField("content")));
		HighlightQuery highlightQuery = new HighlightQuery(highlight, ArticleDocument.class);

		// bool query + highlight query + pageable
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(boolQuery)
			.withHighlightQuery(highlightQuery)
			.withPageable(PageRequest.of(page - 1, pageSize))
			.build();

		SearchHits<ArticleDocument> searchHits =
			elasticsearchOperations.search(nativeQuery, ArticleDocument.class);

		return searchHits.getSearchHits().stream()
			.map(hit -> {
				ArticleDocument articleDocument = hit.getContent();
				String highlightedTitle = hit.getHighlightField("title").getFirst();
				String highlightedContent = hit.getHighlightField("content").getFirst();

				articleDocument.setTitle(highlightedTitle);
				articleDocument.setContent(highlightedContent);

				return articleDocument;
			}).toList();
	}
}
