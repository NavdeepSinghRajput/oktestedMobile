package com.oktested.home.async;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.oktested.friend.ContactResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ContactFetching extends AsyncTask<Void, Void, File> {

    Context context;
    ContactFileInterface contactFileInterface;

    public ContactFetching(Context context, ContactFileInterface contactFileInterface) {
        this.context = context;
        this.contactFileInterface = contactFileInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        contactFileInterface.uploadContacts(file);

    }

    @Override
    protected File doInBackground(Void... voids) {
        JSONArray jsonArray = new JSONArray();
        List<ContactResponse> contactResponsesList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ContactResponse contactResponse = new ContactResponse(name, phoneNo);
                        contactResponsesList.add(contactResponse);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", name);
                            jsonObject.put("mobileNumber", phoneNo);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        File file = creatingFile(jsonArray);
        return file;
    }

    private File creatingFile(JSONArray jsonArray) {
     /*   File file = new File(context.getFilesDir(), "contactFile");
        if (!file.exists()) {
            file.mkdir();
        }*/
        try {
            File userContactListFile = new File(context.getFilesDir(), "userContactListFile.json");
            FileWriter writer = new FileWriter(userContactListFile);
            writer.append(jsonArray.toString());
            writer.flush();
            writer.close();
            return userContactListFile;
           /*   ArrayList<String> navi = readFile();
            Log.e("navi", navi.size() + "     a");*/
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //Log.e("navi", e.getMessage());
        }

    }

}
