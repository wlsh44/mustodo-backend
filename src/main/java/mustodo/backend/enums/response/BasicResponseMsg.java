package mustodo.backend.enums.response;

public enum BasicResponseMsg implements ResponseMsg {
    INVALID_ARGUMENT_ERROR("올바르지 않은 형식입니다.");

    private String errMsg;

    BasicResponseMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getResMsg() {
        return errMsg;
    }
}
