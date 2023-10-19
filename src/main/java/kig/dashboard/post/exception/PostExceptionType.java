package kig.dashboard.post.exception;

import kig.dashboard.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PostExceptionType implements BaseExceptionType {

    POST_NOT_FOUND(700, HttpStatus.NOT_FOUND, "찾으시는 게시글이 없습니다"),
    NOT_AUTHORITY_UPDATE_POST(701, HttpStatus.FORBIDDEN, "게시글을 업데이트할 권한이 없습니다"),
    NOT_AUTHORITY_DELETE_POST(702, HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다");


    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    PostExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
