package kig.dashboard.member.exception;

import kig.dashboard.global.exception.BaseException;
import kig.dashboard.global.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
