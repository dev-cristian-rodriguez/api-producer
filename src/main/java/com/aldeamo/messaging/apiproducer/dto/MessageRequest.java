package com.aldeamo.messaging.apiproducer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Message type is required")
    @Pattern(regexp = "TEXT|IMAGE|VIDEO|DOCUMENT", message = "Message type must be TEXT, IMAGE, VIDEO, or DOCUMENT")
    private String messageType;

    @NotBlank(message = "Content is required")
    private String content;
}
