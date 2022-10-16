package mustodo.backend.enums.response;

public enum BasicResponseMsg implements ResponseMsg {
    ;

    private String errMsg;

    BasicResponseMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getResMsg() {
        return errMsg;
    }
}
