package com.oktested.core.network;

import com.google.gson.JsonObject;
import com.oktested.community.model.PollSubmitResponse;
import com.oktested.community.model.PostDetailResponse;
import com.oktested.community.model.PostLogResponse;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.EventTrackingResponse;
import com.oktested.entity.EventTrackingUpdateResponse;
import com.oktested.helpSupport.model.HelpResponse;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.CommunityPostResponse;
import com.oktested.home.model.ContactFileLinkResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.EditorPickResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.HomeStructureResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.NotifyInviteFriendResponse;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.home.model.QuizStructureResponse;
import com.oktested.home.model.RejectFriendResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.SearchUserContactListResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.joinRoom.model.JoinRoomResponse;
import com.oktested.joinRoom.model.RejectInvitationResponse;
import com.oktested.login.model.CreateUserResponse;
import com.oktested.login.model.SocialLoginResponse;
import com.oktested.menu.model.NotificationResponse;
import com.oktested.notification.model.NotificationsResponse;
import com.oktested.pickQuiz.model.SelectQuizResponse;
import com.oktested.pickQuiz.model.SuggestQuizResponse;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.quizProfile.model.UserStatsResponse;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.quizResult.model.QuizResultResponse;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.search.model.SearchHistoryResponse;
import com.oktested.termsPrivacy.model.CommunityGuidelineResponse;
import com.oktested.termsPrivacy.model.PrivacyPolicyResponse;
import com.oktested.termsPrivacy.model.TermsConditionResponse;
import com.oktested.updateMobile.model.UpdateMobileNumberResponse;
import com.oktested.updateMobile.model.VerifyMobileNumberResponse;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.verifyotp.model.ResendOtpResponse;
import com.oktested.verifyotp.model.VerifyOtpResponse;
import com.oktested.videoCall.model.AcceptSuggestedQuizResponse;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.LeaveRoomResponse;
import com.oktested.videoCall.model.QuestionWiseResultResponse;
import com.oktested.videoCall.model.RecommendedQuizResponse;
import com.oktested.videoCall.model.RemoveParticipantResponse;
import com.oktested.videoPlayer.model.CommentDeleteResponse;
import com.oktested.videoPlayer.model.CommentReactionResponse;
import com.oktested.videoPlayer.model.CommentReplyPostResponse;
import com.oktested.videoPlayer.model.CommentReplyResponse;
import com.oktested.videoPlayer.model.CommentReportResponse;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.ReactionResponse;
import com.oktested.videoPlayer.model.ReplyDeleteResponse;
import com.oktested.videoPlayer.model.ReplyReactionResponse;
import com.oktested.videoPlayer.model.ReplyReportResponse;
import com.oktested.videoPlayer.model.ReportIssueResponse;
import com.oktested.videoPlayer.model.SingleCommentResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;
import com.oktested.videoPlayer.model.VideoCommentPostResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {

    // Used for signup process
    @POST("/users")
    Call<CreateUserResponse> createUser(@Body JsonObject jsonObject);

    // For socially login in app
    @POST("/sociallogin")
    Call<SocialLoginResponse> socialLogin(@Body JsonObject jsonObject);

    // For verify the otp
    @POST("/verifyotp")
    Call<VerifyOtpResponse> verifyOtp(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For resend otp
    @POST("/resendotp")
    Call<ResendOtpResponse> resendOtp(@Body JsonObject jsonObject);

    // Used in multiple screens to get the user data like follow, favourite etc.
    @GET("/users/{uid}")
    Call<GetUserResponse> getUserData(@HeaderMap HashMap<String, String> var1, @Path("uid") String uid);

    // For update the user information on my profile screen and notification token on home screen
    @PUT("/users/{uid}")
    Call<UpdateUserResponse> updateUserData(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject, @Path("uid") String uid);

    // For get the dynamic home structure
    @GET("new_dynamichome/?type=androidapp")
    Call<HomeStructureResponse> getHomeStructure();

    // For get the editor pick video on top of home
    @GET("ed-picks/latest/?type=androidapp")
    Call<EditorPickResponse> getEditorPickVideo();

    // For App Exclusive Video list
    @GET("videos/?filter_type=app_only&type=androidapp")
    Call<AppExclusiveResponse> getAppExclusiveVideoList(@Query("offset") String offset);

    // For Trending Video list
    @GET("videos/?filter_type=trending&type=androidapp")
    Call<TrendingVideoResponse> getTrendingVideoList(@Query("offset") String offset);

    // For Most Viewed Video list
    @GET("videos/?filter_type=most_viewed&type=androidapp")
    Call<MostViewedVideoResponse> getMostViewedVideoList(@Query("offset") String offset);

    // To get the recently added videos list
    @GET("videos/?type=androidapp")
    Call<VideoListResponse> getRecentlyAddedVideoList();

    // To get the recently added videos list scroll
    @GET("videos/?type=androidapp")
    Call<VideoListResponse> getRecentlyAddedPaginationList(@Query("offset") String next);

    // For get the list of shows
    @GET("video_app/ok_tested/get_all_channel_shows/?type=androidapp")
    Call<ShowsResponse> getShowsList(@Query("offset") String offset);

    // For get the list of videos for particular show
    @GET("videos/?filter_type=show&type=androidapp")
    Call<ShowsVideoResponse> getShowsVideoList(@Query("filter_slug") String slug);

    // For get the list of videos for particular show on scrolling
    @GET("videos/?filter_type=show&type=androidapp")
    Call<ShowsVideoResponse> getShowsVideoPagingList(@Query("filter_slug") String filter_slug, @Query("offset") String offset);

    // For get the list of scoop whoop videos
    @GET("videos/?filter_type=sw_more&type=androidapp")
    Call<ScoopWhoopVideoResponse> getScoopWhoopVideoList(@Query("offset") String offset);

    // For get the list of scoop whoop shows
    @GET("originals/ok_tested_app/sw_more_shows/?type=androidapp")
    Call<ScoopWhoopShowsResponse> getScoopWhoopShowsList(@Query("offset") String offset);

    // For get the list of scoop whoop shows videos
    @GET("videos/?filter_type=show_sw_more&type=androidapp")
    Call<ScoopWhoopShowVideoResponse> getScoopWhoopShowVideoList(@Query("filter_slug") String filter_slug, @Query("offset") String offset);

    // For get the list of actors on actor listing page
    @GET("video_app/get_all_castcrew/?show_faces=true&type=androidapp")
    Call<AnchorsResponse> getAnchorsList();

    // For get the videos of particular actor on actor profile page
    @GET("videos/?filter_type=castcrew&type=androidapp")
    Call<VideoListResponse> getActorsVideoList(@Query("filter_slug") String string);

    // For get the videos of particular actor on scrolling on actor profile page
    @GET("videos/?filter_type=castcrew&type=androidapp")
    Call<VideoListResponse> getActorsVideoPagingList(@Query("filter_slug") String filter_slug, @Query("offset") String offset);

    // For get list of favourite videos on my profile page
    @GET("videos/?type=androidapp")
    Call<VideoListResponse> getFavouriteVideoList(@Query("data") String string);

    // For get list of favourite shows on my profile page
    @GET("ok_tested/get_all_channel_shows/?type=androidapp")
    Call<ShowsResponse> getFavouriteShowsList(@Query("data") String string);

    // For get the list of favourite actors on my profile
    @GET("get_all_castcrew/?type=androidapp")
    Call<AnchorsResponse> getFavouriteAnchorList(@Query("data") String string);

    // For get the user search data (history)
    @GET("/searchdata?")
    Call<SearchHistoryResponse> getUserHistoryData(@HeaderMap HashMap<String, String> var1, @Query("userid") String userid);

    // For get the user search result
    @GET("search_videos/{search}/?type=androidapp")
    Call<VideoListResponse> getUserSearchResult(@HeaderMap HashMap<String, String> var1, @Path("search") String search);

    // For get the user search result on scroll
    @GET("search_videos/{search}/?type=androidapp")
    Call<VideoListResponse> getUserSearchPagingResult(@HeaderMap HashMap<String, String> var1, @Path("search") String search, @Query("offset") String offset);

    // For store the user search string on server
    @POST("/searchdata")
    Call<BaseResponse> storeSearchData(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For get the video report issues
    @GET("/report/static/")
    Call<ReportIssueResponse> getVideoReportIssue(@HeaderMap HashMap<String, String> var1);

    // For get the new video comment data
    @GET("/comments?")
    Call<VideoCommentResponse> getVideoCommentData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id);

    // For get the new video comment data on scroll
    @GET("/comments?")
    Call<VideoCommentResponse> getVideoCommentPagingData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id, @Query("page") String page);

    // For get the top video comment data
    @GET("/comments?")
    Call<VideoCommentResponse> getTopVideoCommentData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id, @Query("type") String type);

    // For get the top video comment data on scroll
    @GET("/comments?")
    Call<VideoCommentResponse> getTopVideoCommentPagingData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id, @Query("type") String type, @Query("page") String page);

    // For send the comment
    @POST("/comments")
    Call<VideoCommentPostResponse> sendUserComment(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For send the comment reaction
    @POST("/reactions")
    Call<CommentReactionResponse> sendCommentReaction(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For delete the comment
    @PUT("/comment/remove")
    Call<CommentDeleteResponse> deleteComment(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For report the comment
    @POST("/comment/report")
    Call<CommentReportResponse> reportComment(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For send the comment reply
    @POST("/comments")
    Call<CommentReplyPostResponse> sendCommentReply(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For get the comment reply data
    @GET("/comment/replies?")
    Call<CommentReplyResponse> getCommentReplyData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id, @Query("parent_id") String parent_id);

    // For get the comment reply data on scroll
    @GET("/comment/replies?")
    Call<CommentReplyResponse> getCommentReplyPagingData(@HeaderMap HashMap<String, String> var1, @Query("post_id") String post_id, @Query("parent_id") String parent_id, @Query("page") String page);

    // For send the reply reaction
    @POST("/reactions")
    Call<ReplyReactionResponse> sendReplyReaction(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For delete the reply
    @PUT("/comment/remove")
    Call<ReplyDeleteResponse> deleteReply(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For report the comment reply
    @POST("/comment/report")
    Call<ReplyReportResponse> reportReply(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Used for subscribe notifications in settings screen
    @POST("/subscribe")
    Call<NotificationResponse> subscribeNotification(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Used for unsubscribe notifications in settings screen
    @POST("/unsubscribe")
    Call<NotificationResponse> unsubscribeNotification(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Use for click favourite video on video playing screen and for select favourite show
    @POST("/favourite")
    Call<FavouriteResponse> favourite(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Use for click unfavoured video on video playing screen and for select unfavoured show
    @POST("/unfavourite")
    Call<UnFavouriteResponse> unfavourite(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Used for follow actors on video playing screen and actors screen
    @POST("/follow")
    Call<FollowResponse> follow(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Used for unfollow actors on video playing screen and actors screen
    @POST("/unfollow")
    Call<UnFollowResponse> unfollow(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Used for select emoticon reaction on video playing screen
    @POST("/like")
    Call<ReactionResponse> postReaction(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Use for privacy policy
    @GET("/page/privacy-policy")
    Call<PrivacyPolicyResponse> getPrivacyPolicy();

    // Use for terms and condition
    @GET("/page/terms-of-use")
    Call<TermsConditionResponse> getTermsCondition();

    // Use for community guidelines
    @GET("/page/community-guidelines")
    Call<CommunityGuidelineResponse> getCommunityGuideline();

    // For get the help questions answers on settings screen
    @GET("/help?status=1")
    Call<HelpResponse> getHelpQuestions(@HeaderMap HashMap<String, String> var1);

    // For report problem on settings screen
    @POST("/report")
    Call<ReportProblemResponse> reportProblem(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // Video Tracking api for 1st time play video
    @POST("/events")
    Call<EventTrackingResponse> callVideoTrackingApi(@Body JsonObject jsonObject);

    // Video tracking api when video is playing in handler
    @PUT("/events")
    Call<EventTrackingUpdateResponse> callVideoTrackingUpdateApi(@Body JsonObject jsonObject);

    // For get the latest post of community
    @GET("/post?")
    Call<CommunityPostResponse> getLatestCommunityPost(@HeaderMap HashMap<String, String> var1, @Query("page") String page, @Query("sort") String sort);

    // For get the post detail data via deep link and notification
    @GET("/post/{post_id}")
    Call<PostDetailResponse> getSinglePostDetailData(@HeaderMap HashMap<String, String> var1, @Path("post_id") String post_id);

    // For send the community post reaction
    @POST("/reactions")
    Call<PostReactionResponse> sendPostReaction(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For send the community post share count
    @POST("/share")
    Call<PostShareResponse> sendPostShareCount(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For submit poll selected option
    @POST("/poll")
    Call<PollSubmitResponse> sendPollSelectedOption(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For send the post log when open post detail page
    @POST("/post/visit")
    Call<PostLogResponse> callPostLogApi(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For get the particular comment data when user click on notification for go to video and post detail page
    @GET("/comments/{post_id}/{comment_id}/")
    Call<SingleCommentResponse> getSingleCommentData(@HeaderMap HashMap<String, String> var1, @Path("post_id") String post_id, @Path("comment_id") String comment_id);

    // For get the dynamic home structure of quiz section
    @GET("quiz/dynamic_home/")
    Call<QuizStructureResponse> getQuizHomeStructure();

    //For get the trending quiz
    @GET("quiz/posts/")
    Call<TrendingQuizResponse> getTrendingQuiz(@Query("type") String type, @Query("offset") String offset);

    //For get the latest quiz
    @GET("quiz/posts/")
    Call<LatestQuizResponse> getLatestQuiz(@Query("offset") String offset);

    //For get the detail data of particular quiz
    @GET("quiz/posts/{post_id}")
    Call<QuizDetailResponse> getQuizDetail(@Path("post_id") String post_id);

    //For get the detail data of particular quiz based on channel id
    @GET("quiz/posts/{post_id}")
    Call<QuizDetailResponse> getQuizDetailForChannel(@Path("post_id") String post_id, @Query("channel_id") String channel_id);

    //For get the categories of quiz
    @GET("quiz/categories/")
    Call<CategoryQuizResponse> getQuizCategory(@Query("offset") String offset);

    //For get the list of quiz for particular quiz category
    @GET("quiz/category_posts/{category_slug}")
    Call<CategoryDetailQuizResponse> getQuizCategoryDetail(@Path("category_slug") String category_slug, @Query("offset") String offset);

    //For search the quiz
    @GET("quiz/search/")
    Call<SearchQuizResponse> getSearchedQuiz(@Query("q") String q, @Query("page") String page);

    //For upload contacts File
    @Multipart
    @POST("user_contacts")
    Call<ContactFileUploadResponse> uploadContacts(@HeaderMap HashMap<String, String> header, @Part MultipartBody.Part filePart, @Part("firebase_token") RequestBody firebase_token);

    //For get the contacts list
    @GET("user_contacts")
    Call<UserContactListResponse> getContactList(@HeaderMap HashMap<String, String> header, @Query("type") String type, @Query("offset") String offset, @Query("limit") String limit);

    // For sending  Friend request
    @POST("send_friend_request")
    Call<SendFriendRequestResponse> sendFriendRequest(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For accept  Friend request
    @PUT("friend_request/accepted")
    Call<AcceptFriendResponse> acceptFriendRequest(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For reject  Friend request
    @PUT("friend_request/rejected")
    Call<RejectFriendResponse> rejectFriendRequest(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For generate invite link
    @GET("get_invite_link")
    Call<GenerateInviteLinkResponse> getInviteLink(@HeaderMap HashMap<String, String> header, @Query("contact") String contact, @Query("connect_type") String connect_type);

    //For sending invite link and notify invite user installed app
    @POST("notify_invite_link ")
    Call<NotifyInviteFriendResponse> notifyInviteLinkRequest(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For getting all notification of user
    @GET("user_notify")
    Call<NotificationsResponse> getUserNotification(@HeaderMap HashMap<String, String> header, @Query("page") int page, @Query("count") int count);

    // For send event for solo play quiz
    @POST("/start_play_quiz/solo")
    Call<QuizStartResponse> callPlayQuizEvent(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For send event for group play quiz
    @POST("/start_play_quiz/group")
    Call<QuizStartResponse> callGroupPlayQuizEvent(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For insert each and every question data while playing solo quiz
    @POST("quiz/insert_ques_data")
    Call<InsertQuizQuestionResponse> insertPlaySoloQuizData(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For get the result of solo quiz
    @GET("quiz/result/solo?")
    Call<QuizResultResponse> getSoloQuizResult(@HeaderMap HashMap<String, String> var1, @Query("quiz_id") String quiz_id);

    // For invite friend in the room
    @POST("channel/invite_user")
    Call<InviteFriendToRoomResponse> inviteFriendToRoom(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For joining room via invite
    @PUT("channel/manage_user")
    Call<JoinRoomResponse> joinRoom(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For reject invitation
    @PUT("channel/reject_invite")
    Call<RejectInvitationResponse> rejectInvitation(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For leave room
    @PUT("channel/manage_user")
    Call<LeaveRoomResponse> leaveRoom(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For get the question wise result
    @GET("quiz-question/result/group?")
    Call<QuestionWiseResultResponse> getQuestionWiseResult(@HeaderMap HashMap<String, String> var1, @Query("channel_id") String channel_id, @Query("quiz_id") String quiz_id, @Query("question_id") String question_id);

    //For get the result of group quiz
    @GET("quiz/result/group?")
    Call<GroupQuizResultResponse> getGroupQuizResult(@HeaderMap HashMap<String, String> var1, @Query("quiz_id") String quiz_id, @Query("channel_id") String channel_id);

    //For getting Friend List
    @GET("friends")
    Call<FriendListResponse> getFriendList(@HeaderMap HashMap<String, String> header, @Query("type") String type, @Query("next_page") int next_page);

    //For change user status online to offline
    @POST("user-manage/offline")
    Call<ResponseBody> userOffline(@HeaderMap HashMap<String, String> header);

    //For change user status offline to online
    @POST("user-manage/online")
    Call<ResponseBody> userOnline(@HeaderMap HashMap<String, String> header);

    //For change user status offline to online for first time
    @POST("user-manage/online/yes")
    Call<ResponseBody> userOnlineFirstTime(@HeaderMap HashMap<String, String> header);

    //For Recently played Quizzes
    @GET("recently_played_quizzes")
    Call<RecentPlayedQuizResponse> getRecentPlayedQuiz(@HeaderMap HashMap<String, String> header, @Query("offset") String offset);

    // For select quiz from pick quiz dialog in room via admin
    @PUT("channel/select_quiz/")
    Call<SelectQuizResponse> selectQuiz(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For suggest quiz from pick quiz dialog in room via participant
    @PUT("channel/suggest_quiz/")
    Call<SuggestQuizResponse> suggestQuiz(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For accept the suggested quiz via participant in room
    @PUT("channel/accept_suggested_quiz/")
    Call<AcceptSuggestedQuizResponse> acceptSuggestedQuiz(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For get the detail channel data in room
    @GET("/get_channel/{channel_id}")
    Call<ChannelDetailResponse> getDetailChannelData(@HeaderMap HashMap<String, String> var1, @Path("channel_id") String channel_id);

    // For remove the participant from room
    @PUT("channel/remove_participant/")
    Call<RemoveParticipantResponse> removeParticipant(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For set the status ready/unready in room
    @PUT("channel/manage_ready_status/")
    Call<ChannelDetailResponse> readyToPlay(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For get the list of recommended quiz
    @GET("quiz/reco/?")
    Call<RecommendedQuizResponse> getRecommendedQuiz(@Query("quiz_id") String quiz_id, @Query("offset") String offset);

    //For search  the contacts list
    @GET("user_contact_search")
    Call<SearchUserContactListResponse> getSearchContactList(@HeaderMap HashMap<String, String> header, @Query("q") String searched, @Query("page") String page, @Query("limit") String limit);

    //For  user stats
    @GET("user_stats")
    Call<UserStatsResponse> getUserStats(@HeaderMap HashMap<String, String> header);

    // For update mobile number when login via social login
    @POST("/add_mobile_number")
    Call<UpdateMobileNumberResponse> updateMobileNumber(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For verify mobile number via otp when login via social login
    @POST("/verify_mobile_number")
    Call<VerifyMobileNumberResponse> verifyMobileNumber(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    //For Download ContactList
    @GET("{path}")
    Call<List<ContactModel>> downloadContact(@Path("path") String path,@Query("val") long l);

    //For contact file link
    @GET("user_contacts")
    Call<ContactFileLinkResponse> getContactFileLink(@HeaderMap HashMap<String, String> headerMap);

    // For unfriend
    @POST("unfriend")
    Call<UnFriendResponse> unFriendApi(@HeaderMap HashMap<String, String> header, @Body JsonObject jsonObject);

    // For change the status of camera enable/disable
    @PUT("channel/camera_status/{action}")
    Call<CameraStatusResponse> changeCameraStatus(@HeaderMap HashMap<String, String> header, @Path("action") String action, @Body JsonObject jsonObject);
}