package com.oktested.helpSupport.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.helpSupport.model.HelpResponse;

public interface HelpSupportView extends MasterView {
    void setHelpResponse(HelpResponse helpResponse);
}