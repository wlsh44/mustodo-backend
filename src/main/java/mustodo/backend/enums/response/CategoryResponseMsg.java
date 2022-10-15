package mustodo.backend.enums.response;

public enum CategoryResponseMsg implements ResponseMsg {
    CREATE_CATEGORY_FAILED("카테고리 생성에 실패했습니다."),
    CREATE_CATEGORY_SUCCESS("카테고리 생성에 성공했습니다.");

    private String resMsg;

    CategoryResponseMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    @Override
    public String getResMsg() {
        return resMsg;
    }
}
