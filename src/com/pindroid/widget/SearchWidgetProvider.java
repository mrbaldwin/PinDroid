package com.pindroid.widget;

import com.pindroid.R;
import com.pindroid.Constants;
import com.pindroid.action.IntentHelper;

import com.pindroid.activity.Main;
import com.pindroid.platform.BookmarkManager;
import com.pindroid.providers.BookmarkContentProvider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public class SearchWidgetProvider extends AppWidgetProvider {
	
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int n = appWidgetIds.length;
        
		AccountManager mAccountManager = AccountManager.get(context);
		Account mAccount = null;
		String username = "";
		
		if(mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE).length > 0) {	
			mAccount = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE)[0];
			username = mAccount.name;
		}

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < n; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent bookmarkIntent = new Intent();
            bookmarkIntent.setAction(Intent.ACTION_VIEW);
            bookmarkIntent.addCategory(Intent.CATEGORY_DEFAULT);
    		Uri.Builder bookmarkData = new Uri.Builder();
    		bookmarkData.scheme(Constants.CONTENT_SCHEME);
    		bookmarkData.encodedAuthority(username + "@" + BookmarkContentProvider.AUTHORITY);
    		bookmarkData.appendEncodedPath("bookmarks");
    		bookmarkIntent.setData(bookmarkData.build());
    		
    		Intent tagIntent = new Intent();
    		tagIntent.setAction(Intent.ACTION_VIEW);
    		tagIntent.addCategory(Intent.CATEGORY_DEFAULT);
    		Uri.Builder tagData = new Uri.Builder();
    		tagData.scheme(Constants.CONTENT_SCHEME);
    		tagData.encodedAuthority(username + "@" + BookmarkContentProvider.AUTHORITY);
    		tagData.appendEncodedPath("tags");
    		tagIntent.setData(tagData.build());
    		
    		Intent searchIntent = new Intent(context, Main.class);
    		searchIntent.setAction(Intent.ACTION_SEARCH);
    		
    		Intent addIntent = IntentHelper.AddBookmark(null, username);
    		
    		Intent unreadIntent = new Intent();
    		unreadIntent.setAction(Intent.ACTION_VIEW);
    		unreadIntent.addCategory(Intent.CATEGORY_DEFAULT);
    		Uri.Builder data = new Uri.Builder();
    		data.scheme(Constants.CONTENT_SCHEME);
    		data.encodedAuthority(username + "@" + BookmarkContentProvider.AUTHORITY);
    		data.appendEncodedPath("bookmarks");
    		data.appendQueryParameter("unread", "1");
    		unreadIntent.setData(data.build());
    		
            PendingIntent bookmarkPendingIntent = PendingIntent.getActivity(context, 0, bookmarkIntent, 0);
            PendingIntent tagPendingIntent = PendingIntent.getActivity(context, 0, tagIntent, 0);
            PendingIntent searchPendingIntent = PendingIntent.getActivity(context, 0, searchIntent, 0);
            PendingIntent addPendingIntent = PendingIntent.getActivity(context, 0, addIntent, 0);
            PendingIntent unreadPendingIntent = PendingIntent.getActivity(context, 0, unreadIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.search_appwidget);
            views.setOnClickPendingIntent(R.id.search_widget_bookmarks_button, bookmarkPendingIntent);
            views.setOnClickPendingIntent(R.id.search_widget_tags_button, tagPendingIntent);
            views.setOnClickPendingIntent(R.id.search_widget_search_button, searchPendingIntent);
            views.setOnClickPendingIntent(R.id.search_widget_add_button, addPendingIntent);
            views.setOnClickPendingIntent(R.id.search_widget_unread_button, unreadPendingIntent);
            
            int count = BookmarkManager.GetUnreadCount(username, context);

            String countText = Integer.toString(count);
            if(count > 99) {
            	countText = "+";
            }

            if(count > 0) {
            	views.setViewVisibility(R.id.search_widget_unread_count, View.VISIBLE);
                views.setViewVisibility(R.id.search_widget_unread_count_background, View.VISIBLE);
            	
            	views.setTextViewText(R.id.search_widget_unread_count, countText);
            } else {
            	views.setViewVisibility(R.id.search_widget_unread_count, View.GONE);
            	views.setViewVisibility(R.id.search_widget_unread_count_background, View.GONE);
            }

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}