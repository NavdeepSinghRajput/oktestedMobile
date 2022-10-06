package com.oktested.roomDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ContactsDao {

    @Query("SELECT * FROM contactsTable")
    List<ContactModel> getAll();

    @Query("SELECT * FROM contactsTable WHERE status =:add")
    List<ContactModel> getAddContact(String add);

    @Query("SELECT * FROM contactsTable WHERE status =:pending")
    List<ContactModel> getPendingContact(String pending);

    @Query("SELECT * FROM contactsTable WHERE status =:invite")
    List<ContactModel> getInviteContact(String invite);

    @Query("SELECT * FROM contactsTable WHERE status =:pendingfriendRequest")
    List<ContactModel> getPendingFriendRequestContact(String pendingfriendRequest);

    @Query("SELECT * FROM contactsTable WHERE name LIKE :value and status!=:friend")
    List<ContactModel> getSearchContact(String value,String friend);

    @Query("SELECT * FROM contactsTable WHERE contact_no LIKE :contact_no")
    ContactModel getSearchContactWithNumber(String contact_no);

    @Query("SELECT * FROM contactsTable WHERE id LIKE :id")
    ContactModel getSearchContactWithId(String id);

    @Insert
    void insertAll(ContactModel contactModel);

    @Query("DELETE FROM contactsTable")
     void deleteData();

    @Update
    void updateUserStatus(ContactModel contactModel);
}