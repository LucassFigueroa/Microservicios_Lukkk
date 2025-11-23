package minzbook.catalogservice.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id;
    private String content;
    private int rating;
    private Long bookId;
    private String username;
}