package postservice.exception;

import postservice.exception.common.BusinessException;
import postservice.exception.common.ErrorCode;

public class NotMatchPostException extends BusinessException {
    public NotMatchPostException() {
        super(ErrorCode.MATCH_POST_EXCEPTION);
    }
}
