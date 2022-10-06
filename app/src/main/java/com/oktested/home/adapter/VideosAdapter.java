package com.oktested.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.oktested.R;
import com.oktested.activities.WebViewActivity;
import com.oktested.allVideo.ui.AllVideoActivity;
import com.oktested.browseAnchor.ui.BrowseAnchorActivity;
import com.oktested.browseShow.ui.BrowseShowActivity;
import com.oktested.entity.DataItem;
import com.oktested.home.holder.VideosViewHolder;
import com.oktested.home.model.AnchorsListModel;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.HomeStructureListModel;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.showDetail.ui.ShowDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;
import com.oktested.utils.MyBannerAd;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.Map;

public class VideosAdapter extends RecyclerView.Adapter<VideosViewHolder> {

    private Context context;
    private ArrayList<HomeStructureListModel> structureListModel;
    private Map<Integer, Object> sectionMap;
    private ArrayList<Integer> moreShowsCountAL;
    private ArrayList<Integer> swShowsVideoCountAL;
    private ArrayList<Integer> bannerCountAL;
    private ArrayList<Integer> bannerAdCountAL;
    private int count = 0;
    private int swVideoCount = 0;
    private int bannerCount = 0;
    private int bannerAdCount = 0;

    public VideosAdapter(Context context, ArrayList<HomeStructureListModel> structureListModel, Map<Integer, Object> sectionMap, ArrayList<Integer> moreShowsCountAL, ArrayList<Integer> swShowsVideoCountAL, ArrayList<Integer> bannerCountAL, ArrayList<Integer> bannerAdCountAL) {
        this.context = context;
        this.structureListModel = structureListModel;
        this.sectionMap = sectionMap;
        this.moreShowsCountAL = moreShowsCountAL;
        this.swShowsVideoCountAL = swShowsVideoCountAL;
        this.bannerCountAL = bannerCountAL;
        this.bannerAdCountAL = bannerAdCountAL;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AppConstants.SECTION_EDITOR_PICK:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_pick, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_APP_EXCLUSIVE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_exclusive, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_RECENTLY_ADDED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_added, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_TRENDING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_MOST_VIEWED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_viewed, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_SHOWS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shows, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_ANCHORS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anchors, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_MORE_SHOWS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_shows, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_SW_VIDEOS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scoop_whoop_video, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_SW_SHOWS_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scoop_whoop_show_video, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_SW_SHOWS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scoop_whoop_shows, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_BANNER_AD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad, parent, false);
                return new VideosViewHolder(view, viewType);

            case AppConstants.SECTION_UNKNOWN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unknown_layout, parent, false);
                return new VideosViewHolder(view, viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        HomeStructureListModel listModel = structureListModel.get(position);
        if (listModel != null) {
            switch (listModel.section_type) {
                case AppConstants.SECTION_EDITOR_PICK_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_EDITOR_PICK)) {
                        loadEditorPickSection(holder, (DataItem) sectionMap.get(AppConstants.SECTION_EDITOR_PICK));
                    } else {
                        holder.editorPickRL.setVisibility(View.GONE);
                        holder.editorPickRL.getLayoutParams().height = 0;
                        holder.editorPickRL.requestLayout();
                    }
                    break;

                case AppConstants.SECTION_BANNER_LAYOUT:
                    if (bannerCountAL != null && bannerCountAL.size() > 0) {
                        for (int i = bannerCount; i < bannerCountAL.size(); i++) {
                            if (sectionMap.containsKey(bannerCountAL.get(i))) {
                                loadBannerSection(holder, (HomeStructureListModel.HomeSectionValue) sectionMap.get(bannerCountAL.get(i)));
                                bannerCount++;
                                break;
                            }
                        }
                    }
                    break;

                case AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_APP_EXCLUSIVE)) {
                        holder.appExclusiveRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadAppExclusiveSection(holder, (AppExclusiveResponse) sectionMap.get(AppConstants.SECTION_APP_EXCLUSIVE), structureListModel.get(position).value.section_title);
                    } else {
                        holder.appExclusiveRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_RECENTLY_ADDED_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_RECENTLY_ADDED)) {
                        holder.recentlyAddedRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadRecentlyAddedSection(holder, (VideoListResponse) sectionMap.get(AppConstants.SECTION_RECENTLY_ADDED), structureListModel.get(position).value.section_title);
                    } else {
                        holder.recentlyAddedRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_TRENDING_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_TRENDING)) {
                        holder.trendingRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadTrendingVideoSection(holder, (TrendingVideoResponse) sectionMap.get(AppConstants.SECTION_TRENDING), structureListModel.get(position).value.section_title);
                    } else {
                        holder.trendingRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_MOST_VIEWED_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_MOST_VIEWED)) {
                        holder.mostViewedRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadMostViewedVideoSection(holder, (MostViewedVideoResponse) sectionMap.get(AppConstants.SECTION_MOST_VIEWED), structureListModel.get(position).value.section_title);
                    } else {
                        holder.mostViewedRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_SHOWS_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_SHOWS)) {
                        holder.showsRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadShowsSection(holder, (ShowsResponse) sectionMap.get(AppConstants.SECTION_SHOWS), structureListModel.get(position).value.section_title);
                    } else {
                        holder.showsRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_ANCHORS_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_ANCHORS)) {
                        holder.anchorsRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadAnchorsSection(holder, (ArrayList<AnchorsListModel>) sectionMap.get(AppConstants.SECTION_ANCHORS));
                    } else {
                        holder.anchorsRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_MORE_SHOWS_LAYOUT:
                    if (moreShowsCountAL != null && moreShowsCountAL.size() > 0) {
                        for (int i = count; i < moreShowsCountAL.size(); i++) {
                            if (sectionMap.containsKey(moreShowsCountAL.get(i))) {
                                loadMoreShowsSection(holder, (ShowsVideoResponse) sectionMap.get(moreShowsCountAL.get(i)));
                                count++;
                                break;
                            }
                        }
                    }
                    break;

                case AppConstants.SECTION_SW_VIDEOS_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_SW_VIDEOS)) {
                        holder.scoopWhoopVideoRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadScoopWhoopVideoSection(holder, (ScoopWhoopVideoResponse) sectionMap.get(AppConstants.SECTION_SW_VIDEOS), structureListModel.get(position).value.section_title);
                    } else {
                        holder.scoopWhoopVideoRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_SW_SHOWS_LAYOUT:
                    if (sectionMap.containsKey(AppConstants.SECTION_SW_SHOWS)) {
                        holder.scoopWhoopShowsRL.setVisibility(View.VISIBLE);
                        holder.headerTV.setText(structureListModel.get(position).value.section_title);
                        loadScoopWhoopShowsSection(holder, (ScoopWhoopShowsResponse) sectionMap.get(AppConstants.SECTION_SW_SHOWS), structureListModel.get(position).value.section_title);
                    } else {
                        holder.scoopWhoopShowsRL.setVisibility(View.GONE);
                    }
                    break;

                case AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT:
                    if (swShowsVideoCountAL != null && swShowsVideoCountAL.size() > 0) {
                        for (int i = swVideoCount; i < swShowsVideoCountAL.size(); i++) {
                            if (sectionMap.containsKey(swShowsVideoCountAL.get(i))) {
                                loadScoopWhoopShowVideoSection(holder, (ScoopWhoopShowVideoResponse) sectionMap.get(swShowsVideoCountAL.get(i)));
                                swVideoCount++;
                                break;
                            }
                        }
                    }
                    break;

                case AppConstants.SECTION_BANNER_AD_LAYOUT:
                    if (bannerAdCountAL != null && bannerAdCountAL.size() > 0) {
                        for (int i = bannerAdCount; i < bannerAdCountAL.size(); i++) {
                            if (sectionMap.containsKey(bannerAdCountAL.get(i))) {
                                loadBannerAdSection(holder, (HomeStructureListModel.HomeSectionValue) sectionMap.get(bannerAdCountAL.get(i)));
                                bannerAdCount++;
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (structureListModel.get(position).section_type) {
            case AppConstants.SECTION_EDITOR_PICK_LAYOUT:
                return AppConstants.SECTION_EDITOR_PICK;

            case AppConstants.SECTION_BANNER_LAYOUT:
                return AppConstants.SECTION_BANNER;

            case AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT:
                return AppConstants.SECTION_APP_EXCLUSIVE;

            case AppConstants.SECTION_RECENTLY_ADDED_LAYOUT:
                return AppConstants.SECTION_RECENTLY_ADDED;

            case AppConstants.SECTION_TRENDING_LAYOUT:
                return AppConstants.SECTION_TRENDING;

            case AppConstants.SECTION_MOST_VIEWED_LAYOUT:
                return AppConstants.SECTION_MOST_VIEWED;

            case AppConstants.SECTION_SHOWS_LAYOUT:
                return AppConstants.SECTION_SHOWS;

            case AppConstants.SECTION_ANCHORS_LAYOUT:
                return AppConstants.SECTION_ANCHORS;

            case AppConstants.SECTION_MORE_SHOWS_LAYOUT:
                return AppConstants.SECTION_MORE_SHOWS;

            case AppConstants.SECTION_SW_VIDEOS_LAYOUT:
                return AppConstants.SECTION_SW_VIDEOS;

            case AppConstants.SECTION_SW_SHOWS_LAYOUT:
                return AppConstants.SECTION_SW_SHOWS;

            case AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT:
                return AppConstants.SECTION_SW_SHOWS_VIDEO;

            case AppConstants.SECTION_BANNER_AD_LAYOUT:
                return AppConstants.SECTION_BANNER_AD;

            default:
                return AppConstants.SECTION_UNKNOWN;
        }
    }

    @Override
    public int getItemCount() {
        return structureListModel.size();
    }

    public void setHomeSectionData(Map<Integer, Object> sectionMap, ArrayList<Integer> moreShowsCountAL) {
        this.sectionMap = sectionMap;
        this.moreShowsCountAL = moreShowsCountAL;
        notifyDataSetChanged();
    }

    private void loadEditorPickSection(VideosViewHolder holder, DataItem dataItem) {
        if (holder != null) {
            if (dataItem != null && dataItem.show != null) {
                holder.editorPickRL.setVisibility(View.VISIBLE);
                if (Helper.isContainValue(dataItem.show.topic)) {
                    holder.topicTV.setText(dataItem.show.topic);
                }
                if (Helper.isContainValue(dataItem.title)) {
                    holder.titleTV.setText(dataItem.title);
                }
                if (Helper.isContainValue(dataItem.onexone_img)) {
                    Glide.with(context)
                            .load(dataItem.onexone_img)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(holder.editorPickIV);
                }

                holder.editorPickRL.setOnClickListener(view -> {
                    ArrayList<DataItem> dataItemList = new ArrayList<>();
                    dataItemList.add(dataItem);
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putParcelableArrayListExtra("dataItemList", dataItemList);
                    intent.putExtra("position", 0);
                    context.startActivity(intent);
                });
            } else {
                holder.editorPickRL.setVisibility(View.GONE);
                holder.editorPickRL.getLayoutParams().height = 0;
                holder.editorPickRL.requestLayout();
            }
        }
    }

    private void loadBannerSection(VideosViewHolder holder, HomeStructureListModel.HomeSectionValue homeSectionValue) {
        if (holder != null && homeSectionValue != null) {
            if (Helper.isContainValue(homeSectionValue.image_path)) {
                Glide.with(context)
                        .load(homeSectionValue.image_path)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.bannerIV);
            }

            if (Helper.isContainValue(homeSectionValue.image_link)) {
                holder.bannerIV.setOnClickListener(view -> {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("link", homeSectionValue.image_link);
                    context.startActivity(intent);
                });
            }
        }
    }

    private void loadAppExclusiveSection(VideosViewHolder holder, AppExclusiveResponse appExclusiveResponse, String sectionTitle) {
        if (holder != null && appExclusiveResponse.data != null && appExclusiveResponse.data.size() > 0) {
            holder.appExclusiveRV.setHasFixedSize(true);
            holder.appExclusiveRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.appExclusiveRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, appExclusiveResponse.data, sectionTitle, appExclusiveResponse.next_offset, AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT);
            holder.appExclusiveRV.setAdapter(appExclusiveAdapter);

            holder.appExclusiveRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllVideoActivity.class);
                intent.putExtra("offset", appExclusiveResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_APP_EXCLUSIVE_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", appExclusiveResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadRecentlyAddedSection(VideosViewHolder holder, VideoListResponse videoListResponse, String sectionTitle) {
        if (holder != null && videoListResponse.data != null && videoListResponse.data.size() > 0) {
            holder.recentlyAddedRV.setHasFixedSize(true);
            holder.recentlyAddedRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.recentlyAddedRV.setItemAnimator(new DefaultItemAnimator());

            ArrayList<DataItem> recentlyAddedAL = new ArrayList<>();
            for (int i = 0; i < videoListResponse.data.size(); i++) {
                if (videoListResponse.data.get(i) != null && videoListResponse.data.get(i).show != null) {
                    recentlyAddedAL.add(videoListResponse.data.get(i));
                }
            }

            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, recentlyAddedAL, sectionTitle, videoListResponse.next_offset, AppConstants.SECTION_RECENTLY_ADDED_LAYOUT);
            holder.recentlyAddedRV.setAdapter(appExclusiveAdapter);

            holder.recentlyAddedRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllVideoActivity.class);
                intent.putExtra("offset", videoListResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_RECENTLY_ADDED_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", videoListResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadTrendingVideoSection(VideosViewHolder holder, TrendingVideoResponse trendingVideoResponse, String sectionTitle) {
        if (holder != null && trendingVideoResponse.data != null && trendingVideoResponse.data.size() > 0) {
            holder.trendingRV.setHasFixedSize(true);
            holder.trendingRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.trendingRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, trendingVideoResponse.data, sectionTitle, trendingVideoResponse.next_offset, AppConstants.SECTION_TRENDING_LAYOUT);
            holder.trendingRV.setAdapter(appExclusiveAdapter);

            holder.trendingRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllVideoActivity.class);
                intent.putExtra("offset", trendingVideoResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_TRENDING_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", trendingVideoResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadMostViewedVideoSection(VideosViewHolder holder, MostViewedVideoResponse mostViewedVideoResponse, String sectionTitle) {
        if (holder != null && mostViewedVideoResponse.data != null && mostViewedVideoResponse.data.size() > 0) {
            holder.mostViewedRV.setHasFixedSize(true);
            holder.mostViewedRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.mostViewedRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, mostViewedVideoResponse.data, sectionTitle, mostViewedVideoResponse.next_offset, AppConstants.SECTION_MOST_VIEWED_LAYOUT);
            holder.mostViewedRV.setAdapter(appExclusiveAdapter);

            holder.mostViewedRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllVideoActivity.class);
                intent.putExtra("offset", mostViewedVideoResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_MOST_VIEWED_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", mostViewedVideoResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadShowsSection(VideosViewHolder holder, ShowsResponse showsResponse, String sectionTitle) {
        if (holder != null && showsResponse.data != null && showsResponse.data.size() > 0) {
            holder.showsRV.setHasFixedSize(true);
            holder.showsRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.showsRV.setItemAnimator(new DefaultItemAnimator());
            ShowsAdapter showsAdapter = new ShowsAdapter(context, showsResponse.data, showsResponse.next_offset, AppConstants.SECTION_SHOWS_LAYOUT, sectionTitle);
            holder.showsRV.setAdapter(showsAdapter);

            holder.showsRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, BrowseShowActivity.class);
                intent.putParcelableArrayListExtra("showModel", showsResponse.data);
                intent.putExtra("offset", showsResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_SHOWS_LAYOUT);
                intent.putExtra("heading", "Our Shows");
                context.startActivity(intent);
            });
        }
    }

    private void loadAnchorsSection(VideosViewHolder holder, ArrayList<AnchorsListModel> anchorsAL) {
        if (holder != null && anchorsAL != null && anchorsAL.size() > 0) {
            holder.anchorsRV.setHasFixedSize(true);
            holder.anchorsRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.anchorsRV.setItemAnimator(new DefaultItemAnimator());
            AnchorsAdapter anchorsAdapter = new AnchorsAdapter(context, anchorsAL);
            holder.anchorsRV.setAdapter(anchorsAdapter);

            holder.anchorsRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, BrowseAnchorActivity.class);
                context.startActivity(intent);
            });
        }
    }

    private void loadMoreShowsSection(VideosViewHolder holder, ShowsVideoResponse showsVideoResponse) {
        if (holder != null && showsVideoResponse.data != null && showsVideoResponse.data.size() > 0) {
            holder.moreShowsArrowIV.setVisibility(View.VISIBLE);
            String sectionTitle = "";
            if (Helper.isContainValue(showsVideoResponse.data.get(0).show.topic)) {
                sectionTitle = showsVideoResponse.data.get(0).show.topic;
                holder.showNameTV.setText(sectionTitle);
            }
            holder.moreShowsRV.setHasFixedSize(true);
            holder.moreShowsRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.moreShowsRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, showsVideoResponse.data, sectionTitle, showsVideoResponse.next_offset, AppConstants.SECTION_MORE_SHOWS_LAYOUT);
            holder.moreShowsRV.setAdapter(appExclusiveAdapter);

            holder.moreShowsRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, ShowDetailActivity.class);
                intent.putExtra("slug", showsVideoResponse.data.get(0).show.topicDisplay.topicSlug);
                intent.putExtra("sw_more", false);
                context.startActivity(intent);
            });
        }
    }

    private void loadScoopWhoopVideoSection(VideosViewHolder holder, ScoopWhoopVideoResponse scoopWhoopVideoResponse, String sectionTitle) {
        if (holder != null && scoopWhoopVideoResponse.data != null && scoopWhoopVideoResponse.data.size() > 0) {
            holder.scoopWhoopVideoRV.setHasFixedSize(true);
            holder.scoopWhoopVideoRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.scoopWhoopVideoRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, scoopWhoopVideoResponse.data, sectionTitle, scoopWhoopVideoResponse.next_offset, AppConstants.SECTION_SW_VIDEOS_LAYOUT);
            holder.scoopWhoopVideoRV.setAdapter(appExclusiveAdapter);

            holder.scoopWhoopVideoRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, AllVideoActivity.class);
                intent.putExtra("offset", scoopWhoopVideoResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_SW_VIDEOS_LAYOUT);
                intent.putExtra("sectionTitle", sectionTitle);
                intent.putParcelableArrayListExtra("dataItemList", scoopWhoopVideoResponse.data);
                context.startActivity(intent);
            });
        }
    }

    private void loadScoopWhoopShowsSection(VideosViewHolder holder, ScoopWhoopShowsResponse scoopWhoopShowsResponse, String sectionTitle) {
        if (holder != null && scoopWhoopShowsResponse.data != null && scoopWhoopShowsResponse.data.size() > 0) {
            holder.scoopWhoopShowsRV.setHasFixedSize(true);
            holder.scoopWhoopShowsRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.scoopWhoopShowsRV.setItemAnimator(new DefaultItemAnimator());
            ShowsAdapter showsAdapter = new ShowsAdapter(context, scoopWhoopShowsResponse.data, scoopWhoopShowsResponse.next_offset, AppConstants.SECTION_SW_SHOWS_LAYOUT, sectionTitle);
            holder.scoopWhoopShowsRV.setAdapter(showsAdapter);

            holder.scoopWhoopShowsRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, BrowseShowActivity.class);
                intent.putParcelableArrayListExtra("showModel", scoopWhoopShowsResponse.data);
                intent.putExtra("offset", scoopWhoopShowsResponse.next_offset);
                intent.putExtra("screenName", AppConstants.SECTION_SW_SHOWS_LAYOUT);
                intent.putExtra("heading", sectionTitle);
                context.startActivity(intent);
            });
        }
    }

    private void loadScoopWhoopShowVideoSection(VideosViewHolder holder, ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse) {
        if (holder != null && scoopWhoopShowVideoResponse.data != null && scoopWhoopShowVideoResponse.data.size() > 0) {
            holder.scoopWhoopShowsArrowIV.setVisibility(View.VISIBLE);
            String sectionTitle = "";
            if (Helper.isContainValue(scoopWhoopShowVideoResponse.data.get(0).show.topic)) {
                sectionTitle = scoopWhoopShowVideoResponse.data.get(0).show.topic;
                holder.scoopWhoopShowNameTV.setText(sectionTitle);
            }
            holder.scoopWhoopShowVideoRV.setHasFixedSize(true);
            holder.scoopWhoopShowVideoRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            holder.scoopWhoopShowVideoRV.setItemAnimator(new DefaultItemAnimator());
            AppExclusiveAdapter appExclusiveAdapter = new AppExclusiveAdapter(context, scoopWhoopShowVideoResponse.data, sectionTitle, scoopWhoopShowVideoResponse.next_offset, AppConstants.SECTION_SW_SHOWS_VIDEO_LAYOUT);
            holder.scoopWhoopShowVideoRV.setAdapter(appExclusiveAdapter);

            holder.scoopWhoopShowVideoRL.setOnClickListener(view -> {
                Intent intent = new Intent(context, ShowDetailActivity.class);
                intent.putExtra("slug", scoopWhoopShowVideoResponse.data.get(0).show.topicDisplay.topicSlug);
                intent.putExtra("sw_more", true);
                context.startActivity(intent);
            });
        }
    }

    private void loadBannerAdSection(VideosViewHolder holder, HomeStructureListModel.HomeSectionValue homeSectionValue) {
        if (holder != null && homeSectionValue != null) {
            if (Helper.isContainValue(homeSectionValue.ad_code)) {
                MyBannerAd.getInstance().getAd(context, holder.middleAdView, homeSectionValue.is_mid_ad, homeSectionValue.ad_code);
            }
        }
    }
}