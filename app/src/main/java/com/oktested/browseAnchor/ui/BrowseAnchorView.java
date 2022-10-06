package com.oktested.browseAnchor.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AnchorsResponse;

public interface BrowseAnchorView extends MasterView {
    void setAnchorsResponse(AnchorsResponse anchorsResponse);
}