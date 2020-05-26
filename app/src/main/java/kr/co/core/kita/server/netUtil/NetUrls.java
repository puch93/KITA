package kr.co.core.kita.server.netUtil;

public class NetUrls {
    public static final String DOMAIN = "http://kita.adamstore.co.kr";

    //회원가입
    public static final String JOIN = DOMAIN + "/Member/regist_member";
    //로그인
    public static final String LOGIN = DOMAIN + "/Member/login";
    //내 정보 수정
    public static final String EDIT_PROFILE = DOMAIN + "/Member/setProfile";
    //home fragment -> 인기 리스트 가져오기
    public static final String LIST_HOME_POPULAR = DOMAIN + "/Member/main_popular_memberlist";
    //home fragment -> 지역 리스트 가져오기
    public static final String LIST_HOME_LOCATION = DOMAIN + "/Member/main_location_memberlist";
    //상대 정보 가져오기
    public static final String INFO_OTHER = DOMAIN + "/Member/getProfile";
    //내 정보 가져오기
    public static final String INFO_ME = DOMAIN + "/Member/getMyProfile";
    //회원 토크 리스트 가져오기
    public static final String LIST_TALK = DOMAIN + "/Board/talk_memlist";
    //회원 토크 리스트 가져오기
    public static final String LIST_TALK_ALL = DOMAIN + "/Board/talk_list";
    //토크 등록하기
    public static final String REGISTER_TALK = DOMAIN + "/Board/talk_regist_input";





    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
}
