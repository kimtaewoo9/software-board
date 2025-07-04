package my.board.article.repository;

import my.board.article.document.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDocumentRepository extends ElasticsearchRepository<ArticleDocument, Long> {

}
