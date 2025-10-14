package com.example.board;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @GetMapping
    public BoardListResponse getBoards(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        // TODO: 실제 구현
        return new BoardListResponse();
    }
}
