package com.oktested.core.network;

public class NetworkManager {

    private static final String BASE_URL_API_STAGING = "https://stageoktapi.scoopwhoop.com";
    private static final String BASE_URL_API_LIVE = "https://oktapi.scoopwhoop.com";

    private static final String BASE_URL_CONTACT_API_STAGING = "https://oktapi-stage.scoopwhoop.com";
    private static final String BASE_URL_CONTACT_API_LIVE = "https://oktapi-prod.scoopwhoop.com";

    private static final String OK_TESTED_QUIZ_API_STAGING = "https://stageoktapi.scoopwhoop.com/api/v1/";
    private static final String OK_TESTED_QUIZ_API_LIVE = "https://oktapi.scoopwhoop.com/api/v1/";

    private static final String SCOOPWHOOP_SHOWS_API = "https://www.scoopwhoop.com/api/v4/";
    private static final String SCOOPWHOOP_ACTOR_URL_API = "https://www.scoopwhoop.com/api/v2/video_app/";
    private static final String SCOOPWHOOP_URL_API = "https://www.scoopwhoop.com/api/v2/";
    private static final String SCOOPWHOOP_URL_API_NEW = "https://www.scoopwhoop.com/api/v2/video_app/extPlateform/ok_tested_app/";
    private static final String SCOOPWHOOP_URL_API_STAGING = "https://stagingsw.scoopwhoop.com/api/v2/video_app/extPlateform/ok_tested_app/";

    private static final String VIDEO_TRACKING_STAGE_URL = "https://stage.scwp.in/";
    private static final String VIDEO_TRACKING_LIVE_URL = "https://videoapi.scwp.in/";

    private static final String OK_TESTED_DYNAMIC_HOME = "https://www.scoopwhoop.com/api/oktestedapp/";
    private static final String OK_TESTED_CONTACT_DOWNLOAD_URL = "https://okt-storage.scoopwhoop.com/oktested/static/contact/mobile/";

    public static ApiServices getApi() {
        return RetrofitClient.getClient(BASE_URL_API_LIVE)
                .create(ApiServices.class);
    }

    public static ApiServices getContactApi() {
        return RetrofitClient.getClient(BASE_URL_CONTACT_API_LIVE)
                .create(ApiServices.class);
    }

    public static ApiServices getUserContacts() {
        return RetrofitClient.getClient(OK_TESTED_CONTACT_DOWNLOAD_URL)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopStagingApi() {
        return RetrofitClient.getClient(SCOOPWHOOP_URL_API_STAGING)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopApi() {
        return RetrofitClient.getClient(SCOOPWHOOP_URL_API_NEW)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopOldApi() {
        return RetrofitClient.getClient(SCOOPWHOOP_URL_API)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopActorApi() {
        return RetrofitClient.getClient(SCOOPWHOOP_ACTOR_URL_API)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopVideoTrackingApi() {
        return RetrofitClient.getClient(VIDEO_TRACKING_LIVE_URL)
                .create(ApiServices.class);
    }

    public static ApiServices getDynamicHomeApi() {
        return RetrofitClient.getClient(OK_TESTED_DYNAMIC_HOME)
                .create(ApiServices.class);
    }

    public static ApiServices getScoopWhoopShowsApi() {
        return RetrofitClient.getClient(SCOOPWHOOP_SHOWS_API)
                .create(ApiServices.class);
    }
    public static ApiServices getOkTestedQuizApi() {
        return RetrofitClient.getClient(OK_TESTED_QUIZ_API_LIVE)
                .create(ApiServices.class);
    }
}