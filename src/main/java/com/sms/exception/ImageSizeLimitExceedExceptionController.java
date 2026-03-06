package com.sms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ImageSizeLimitExceedExceptionController {
    @ExceptionHandler(ImageSizeLimitExceededException.class)
    public ResponseEntity<?> handleImageSizeLimitExceededException(ImageSizeLimitExceededException exe){
        ImageErrorResponse errorResponse = new ImageErrorResponse("FILE_SIZE_LIMIT_EXCEEDED", exe.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
