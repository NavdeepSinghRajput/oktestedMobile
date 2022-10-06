package com.oktested.roomDatabase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.oktested.core.network.NetworkManager;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactService extends IntentService {

    public ContactService() {
        super(ContactService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String filename = intent.getStringExtra("filename");

        Call<List<ContactModel>> call = NetworkManager.getUserContacts().downloadContact(filename ,System.currentTimeMillis());
        call.enqueue(new Callback<List<ContactModel>>() {
            @Override
            public void onResponse(Call<List<ContactModel>> call, Response<List<ContactModel>> response) {
                if (response.body() != null) {
                    AppDatabase db = AppDatabase.getAppDatabase(getBaseContext());
//                    List<ContactModel> pendingContactList = db.userDao().getPendingContact("pending");
                    db.userDao().deleteData();
                    for (int i = 0; i < response.body().size(); i++) {
                       /* for (int j = 0; j < pendingContactList.size(); j++) {
                            if(pendingContactList.get(j).contact_no.equalsIgnoreCase(response.body().get(i).contact_no)){
                                    response.body().get(i).status=pendingContactList.get(j).status;
                                    break;
                            }
                        }*/
                        db.userDao().insertAll(response.body().get(i));
                    }
                    AppPreferences.getInstance(getApplicationContext()).savePreferencesString(AppConstants.CONTACT_UPDATE, "true");
                }
            }

            @Override
            public void onFailure(Call<List<ContactModel>> call, Throwable t) {
                Log.e("failure", t.getMessage());
            }
        });
    }
}