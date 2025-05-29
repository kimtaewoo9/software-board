package my.board.comment.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageLimitCalculator {

	public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
		// page -> 현재 페이지, pageSize -> 한 페이지당 게시글 수 , movablePageCount -> 이동 가능한 페이지의 수
		return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
	}
}
