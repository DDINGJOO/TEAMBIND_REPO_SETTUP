package com.example.board;

import java.util.List;

public class BoardListResponse {
    private List<BoardItem> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;

    // Getters and Setters
}

class BoardItem {
    private Long id;
    private String title;
    private String author;
    private String createdAt;

    // Getters and Setters
}
