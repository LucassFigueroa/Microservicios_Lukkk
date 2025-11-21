package minzbook.orderservice.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull
    private Long userId;


    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
