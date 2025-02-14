package org.cheonyakplanet.be.presentation.exception;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getDetails()
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail", errorData
        );

        return ResponseEntity.ok(response);
    }

    // 2) 그 외 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleRuntimeException(RuntimeException e) {
        // 에러가 구체적인 CustomException이 아닌 경우, UNKNOWN_ERROR로 처리
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage()  // 혹은 별도의 default detail
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail",
                errorData
        );

        return ResponseEntity.ok(response);
    }

    // 3) 그 외 모든 예외(Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage() // 필요한 경우
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail",
                errorData
        );

        return ResponseEntity.ok(response);
    }
}
