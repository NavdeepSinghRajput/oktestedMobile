package com.oktested.joinRoom.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.joinRoom.model.JoinRoomResponse;
import com.oktested.joinRoom.model.RejectInvitationResponse;
import com.oktested.videoCall.model.CameraStatusResponse;

public interface JoinRoomView extends MasterView {

    void setJoinRoomResponse(JoinRoomResponse joinRoomResponse);

    void setRejectInvitationResponse(RejectInvitationResponse rejectInvitationResponse);

    void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse);
}