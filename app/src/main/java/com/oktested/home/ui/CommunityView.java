package com.oktested.home.ui;

import com.oktested.community.model.PollSubmitResponse;
import com.oktested.core.ui.MasterView;
import com.oktested.home.model.CommunityPostResponse;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;

public interface CommunityView extends MasterView {
    void setCommunityPostResponse(CommunityPostResponse communityPostResponse);

    void setPostReactionResponse(PostReactionResponse postReactionResponse);

    void setPostShareResponse(PostShareResponse postShareResponse);

    void setPollSubmitResponse(PollSubmitResponse pollSubmitResponse);
}