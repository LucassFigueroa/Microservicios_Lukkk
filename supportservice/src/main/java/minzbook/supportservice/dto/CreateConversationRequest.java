package minzbook.supportservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String asunto;

    @NotBlank
    private String mensajeInicial;

    // getters y setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensajeInicial() { return mensajeInicial; }
    public void setMensajeInicial(String mensajeInicial) { this.mensajeInicial = mensajeInicial; }
}
