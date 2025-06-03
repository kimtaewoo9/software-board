package my.board.hotarticle.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import my.board.hotarticle.service.HotArticleService;
import my.board.hotarticle.service.response.HotArticleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HotArticleController {

	private final HotArticleService hotArticleService;

	@GetMapping("/v1/hot-articles/articles/date/{dateStr}")
	public List<HotArticleResponse> readAll(
		@PathVariable("dateStr") String dateStr
	) {
		return hotArticleService.readAll(dateStr);
	}
}
