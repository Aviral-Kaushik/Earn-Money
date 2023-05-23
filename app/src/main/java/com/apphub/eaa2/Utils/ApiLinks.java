package com.apphub.eaa2.Utils;

public class ApiLinks {

    private static final String BASE_URL = "https://earnifytech.in/earnpay/v1/";

    public static String CHECK_EMAIL = BASE_URL + "apps/check_email.php";
    public static final String FETCH_DATA = BASE_URL + "apps/fetchData.php";
    public static final String CHECK_CODE = BASE_URL + "apps/check_ReferralCode.php";
    public static final String REGISTER = BASE_URL + "apps/register.php";
    public static final String LOGIN = BASE_URL + "apps/login.php";
    public static final String UPDATE_USER_TOKEN = BASE_URL + "token/update_user_token.php";
    public static String GET_GAMES = BASE_URL + "apps/getRandomGame.php";

    public static final String GET_USER_COINS = BASE_URL + "apps/get_user_balance.php";
    public static final String UPDATE_USER_BALANCE = BASE_URL + "apps/update_user_balance.php";
    public static final String GET_HIDES = BASE_URL + "get_hides.php";
    public static final String GET_API_KEYS = BASE_URL + "get_api_keys.php";
    public static final String PP = BASE_URL + "privacyPolicy.html";
    public static final String TC = BASE_URL + "tc.html";

    public static String WITHDRAW_REQUEST = BASE_URL + "withdraw/v2/add_withdraw.php";

    public static String GET_LINKS = BASE_URL + "/Links/getLinks.php";


}
