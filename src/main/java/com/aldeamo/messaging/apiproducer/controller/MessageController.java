package com.aldeamo.messaging.apiproducer.controller;

import com.aldeamo.messaging.apiproducer.dto.ApiResponse;
import com.aldeamo.messaging.apiproducer.dto.MessageRequest;
import com.aldeamo.messaging.apiproducer.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendMessage(@Valid @RequestBody MessageRequest request) {
        log.debug("Received POST /messages request: {}", request);
        messageService.validateAndPublish(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Message accepted and queued for processing"));
    }
}
