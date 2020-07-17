package kr.co.core.kita.server.netUtil;

public class NetUrls {
    public static final String DOMAIN = "https://kita.adamstore.co.kr";

    //회원가입
    public static final String JOIN = DOMAIN + "/Member/regist_member"; //
    //로그인
    public static final String LOGIN = DOMAIN + "/Member/login"; //
    //내 정보 수정
    public static final String EDIT_PROFILE = DOMAIN + "/Member/setProfile"; //
    //home fragment -> 인기 리스트 가져오기
    public static final String LIST_HOME_POPULAR = DOMAIN + "/Member/main_popular_memberlist"; //
    //home fragment -> 지역 리스트 가져오기
    public static final String LIST_HOME_LOCATION = DOMAIN + "/Member/main_location_memberlist"; //
    //상대 정보 가져오기
    public static final String INFO_OTHER = DOMAIN + "/Member/getProfile"; //
    //내 정보 가져오기
    public static final String INFO_ME = DOMAIN + "/Member/getMyProfile"; //
    //회원 토크 리스트 가져오기
    public static final String LIST_TALK = DOMAIN + "/Board/talk_memlist"; //
    //토크 리스트 가져오기
    public static final String LIST_TALK_ALL = DOMAIN + "/Board/talk_list"; //
    //토크 상세 정보 가져오기
    public static final String TALK_DETAIL = DOMAIN + "/Board/talk_detail"; //
    //토크 댓글 등록하기
    public static final String REGISTER_TALK_COMMENT = DOMAIN + "/Board/talk_comment_input"; //
    //토크 등록하기
    public static final String REGISTER_TALK = DOMAIN + "/Board/talk_regist_input"; //
    //토크 삭제하기
    public static final String TALK_DELETE = DOMAIN + "/Board/talk_del"; //
    //하트/하트취소 하기
    public static final String HEART = DOMAIN + "/Member/set_heart"; //
    //선물한 기록 가져오기
    public static final String GIFT_HISTORY = DOMAIN + "/Member/get_gift"; //
    //선물하기
    public static final String GIFT = DOMAIN + "/Member/set_gift"; //
    //오프라인 상태 적용하기
    public static final String SET_OFFLINE = DOMAIN + "/Member/setloginYN"; //
    //약관 가져오기
    public static final String TERMS = DOMAIN + "/Main/app_info"; //
    //신고하기
    public static final String REPORT = DOMAIN + "/Member/declaremember"; //


    /* 결제 */
    //결제결과 전송
    public static final String PAY_RESULT = DOMAIN + "/Member/sell_item"; //
    //채팅 이용권 구매
    public static final String PAY_TICKET = DOMAIN + "/Member/sell_ticket"; // 안씀 (구독으로 변경되어 쓰지않음)


    /* 채팅 */
    //채팅방 만들기
    public static final String CREATE_ROOM = DOMAIN + "/Chat/createRoom"; //
    //채팅방 나가기
    public static final String LEAVE_ROOM = DOMAIN + "/Chat/leaveChat"; // 안씀
    //채팅 가능여부 확인
    public static final String CHAT_POSSIBLE = DOMAIN + "/Chat/chkchatpossible"; //
    //채팅 이용권 구매했는지 여부 확인
    public static final String CHAT_TICKET_PURCHASED = DOMAIN + "/Chat/chkchatresponsepoint"; //
    //채팅 보내기
    public static final String SEND_CHAT = DOMAIN + "/Chat/fcmMsg"; //
    //채팅 이미지 업로드
    public static final String SEND_IMAGE = DOMAIN + "/Chat/addmsgimg"; //
    //채팅 리스트 가져오기
    public static final String LIST_CHAT = DOMAIN + "/Chat/listChats"; //

    /* 영상통화 */
    //전화걸기
    public static final String CALL = DOMAIN + "/Chat/reqMovieCall"; //
    //전화끊기
    public static final String CALL_DISCONNECT = DOMAIN + "/Chat/disconnectMovieCall"; //
    //페소차감
    public static final String DEDUCT_PESO = DOMAIN + "/Chat/setMovieCallPoint"; //
    //영상통화 기록 리스트 가져오기
    public static final String LIST_CALL_HISTORY = DOMAIN + "/Chat/getMovieCallList"; //


    /* 추가 */
    public static final String LIST_NEWEST = DOMAIN + "/Member/memberlist_new"; //
    public static final String FIRST_MSG = DOMAIN + "/Chat/chkmyfirstchat"; //
    public static final String CHECK_AUTOPAY = DOMAIN + "/Member/autopay_chatchk";
    public static final String CANCEL_AUTOPAY = DOMAIN + "/Member/autopay_cancel";

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
}
