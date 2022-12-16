package com.microservice.authservice.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiError implements Serializable {

    private int statusCode;

    private HttpStatus status;

    private LocalDateTime timestamp;

    private String message;

    List<String> errorDetails;

}
