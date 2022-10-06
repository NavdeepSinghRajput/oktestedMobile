package com.oktested.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.oktested.R;
import com.oktested.dashboard.model.GetUserDataresponse;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.favourite.ui.FavouriteFragment;
import com.oktested.profile.presenter.ProfilePresenter;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.DataHolder;
import com.oktested.utils.Helper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements ProfileView, View.OnClickListener {

    private Context context;
    private ProfilePresenter profilePresenter;
    private SpinKitView spinKitView;
    private Spinner daySpinner, monthSpinner, yearSpinner;
    private String[] dayMode = {"Day", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private String[] monthMode = {"Month", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String[] yearMode = {"Year", "1970", "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020"};
    private LinearLayout afterEditLL, beforeEditLL, editProfileLL, userImageLL, userImageLL1, mobileLL, emailLL;
    private RelativeLayout userImageRL;
    private CircleImageView userImageCircle, userImageCircle1;
    private TextView saveTV, userNameTV, emailTV, mobileTV, dobTV, genderTV, emailStaticTV, mobileStaticTV;
    private EditText userNameET, emailET, mobileET;
    private RadioButton femaleRB, maleRB, otherRB;
    private String dayString, monthString, yearString, dobString, genderString, nameString, emailString, mobileString;
    private String picturePath, base64Path = "", imageName = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_profile, container, false);

        inUi(root_view);
        setDateOfBirth();
        setUserData(DataHolder.getInstance().getUserDataresponse);
        return root_view;
    }

    private void inUi(View root_view) {
        profilePresenter = new ProfilePresenter(context, this);
        spinKitView = root_view.findViewById(R.id.spinKit);
        LinearLayout backLL = root_view.findViewById(R.id.backLL);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        afterEditLL = root_view.findViewById(R.id.afterEditLL);
        beforeEditLL = root_view.findViewById(R.id.beforeEditLL);
        emailLL = root_view.findViewById(R.id.emailLL);
        mobileLL = root_view.findViewById(R.id.mobileLL);

        daySpinner = root_view.findViewById(R.id.daySpinner);
        monthSpinner = root_view.findViewById(R.id.monthSpinner);
        yearSpinner = root_view.findViewById(R.id.yearSpinner);

        editProfileLL = root_view.findViewById(R.id.editProfileLL);
        editProfileLL.setOnClickListener(this);

        saveTV = root_view.findViewById(R.id.saveTV);
        saveTV.setOnClickListener(this);

        userImageLL = root_view.findViewById(R.id.userImageLL);
        userImageLL1 = root_view.findViewById(R.id.userImageLL1);
        userImageRL = root_view.findViewById(R.id.userImageRL);

        userImageCircle = root_view.findViewById(R.id.userImageCircle);
        userImageCircle1 = root_view.findViewById(R.id.userImageCircle1);

        userNameTV = root_view.findViewById(R.id.userNameTV);
        emailTV = root_view.findViewById(R.id.emailTV);
        mobileTV = root_view.findViewById(R.id.mobileTV);
        dobTV = root_view.findViewById(R.id.dobTV);
        genderTV = root_view.findViewById(R.id.genderTV);
        emailStaticTV = root_view.findViewById(R.id.emailStaticTV);
        mobileStaticTV = root_view.findViewById(R.id.mobileStaticTV);

        userNameET = root_view.findViewById(R.id.userNameET);
        emailET = root_view.findViewById(R.id.emailET);
        mobileET = root_view.findViewById(R.id.mobileET);
        femaleRB = root_view.findViewById(R.id.femaleRB);
        maleRB = root_view.findViewById(R.id.maleRB);
        otherRB = root_view.findViewById(R.id.otherRB);

        userImageRL.setOnClickListener(this);
        userImageLL1.setOnClickListener(this);
        femaleRB.setOnClickListener(this);
        maleRB.setOnClickListener(this);
        otherRB.setOnClickListener(this);
    }

    private void setDateOfBirth() {
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dayMode);
        dayAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        daySpinner.setAdapter(dayAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) daySpinner.getSelectedView()).setTextColor(Color.parseColor("#c4c4c4"));
                ((TextView) daySpinner.getSelectedView()).setTextSize(11);
                ((TextView) daySpinner.getSelectedView()).setGravity(Gravity.CENTER);

                dayString = daySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, monthMode);
        monthAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        monthSpinner.setAdapter(monthAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) monthSpinner.getSelectedView()).setTextColor(Color.parseColor("#c4c4c4"));
                ((TextView) monthSpinner.getSelectedView()).setTextSize(11);
                ((TextView) monthSpinner.getSelectedView()).setGravity(Gravity.CENTER);

                monthString = monthSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, yearMode);
        yearAdapter.setDropDownViewResource(R.layout.date_spinner_item);
        yearSpinner.setAdapter(yearAdapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) yearSpinner.getSelectedView()).setTextColor(Color.parseColor("#c4c4c4"));
                ((TextView) yearSpinner.getSelectedView()).setTextSize(11);
                ((TextView) yearSpinner.getSelectedView()).setGravity(Gravity.CENTER);

                yearString = yearSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUserData(GetUserDataresponse dataResponse) {
        if (dataResponse != null) {
            if (Helper.isContainValue(dataResponse.picture)) {
                userImageCircle.setVisibility(View.VISIBLE);
                userImageLL.setVisibility(View.GONE);
                Glide.with(context)
                        .load(dataResponse.picture)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(userImageCircle);

                userImageRL.setVisibility(View.VISIBLE);
                userImageLL1.setVisibility(View.GONE);
                Glide.with(context)
                        .load(dataResponse.picture)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(userImageCircle1);
            }

            if (Helper.isContainValue(dataResponse.display_name)) {
                userNameTV.setText(dataResponse.display_name);
                userNameET.setText(dataResponse.display_name);
            } else {
                if (Helper.isContainValue(dataResponse.name)) {
                    userNameTV.setText(dataResponse.name);
                    userNameET.setText(dataResponse.name);
                }
            }

            if (Helper.isContainValue(dataResponse.email)) {
                emailTV.setText(dataResponse.email);
                emailET.setText(dataResponse.email);
            }

            if (Helper.isContainValue(dataResponse.mobile)) {
                mobileTV.setText(dataResponse.mobile.substring(3));
                mobileET.setText(dataResponse.mobile.substring(3));
            }

            if (Helper.isContainValue(dataResponse.last_login_type) && dataResponse.last_login_type.equalsIgnoreCase("social")) {
                emailET.setEnabled(false);

                mobileStaticTV.setVisibility(View.GONE);
                mobileTV.setVisibility(View.GONE);
                mobileLL.setVisibility(View.GONE);
            } else {
                mobileET.setEnabled(false);

                emailStaticTV.setVisibility(View.GONE);
                emailTV.setVisibility(View.GONE);
                emailLL.setVisibility(View.GONE);
            }

            if (Helper.isContainValue(dataResponse.dob)) {
                dobTV.setText(dataResponse.dob);

                String[] dobArray = dataResponse.dob.split("/");
                for (int i = 0; i < dayMode.length; i++) {
                    if (dobArray[0].equalsIgnoreCase(dayMode[i])) {
                        daySpinner.setSelection(i);
                        break;
                    }
                }

                for (int i = 0; i < monthMode.length; i++) {
                    if (dobArray[1].equalsIgnoreCase(monthMode[i])) {
                        monthSpinner.setSelection(i);
                        break;
                    }
                }

                for (int i = 0; i < yearMode.length; i++) {
                    if (dobArray[2].equalsIgnoreCase(yearMode[i])) {
                        yearSpinner.setSelection(i);
                        break;
                    }
                }
            }

            if (Helper.isContainValue(dataResponse.gender)) {
                if (dataResponse.gender.equalsIgnoreCase("F")) {
                    genderTV.setText("Female");
                    femaleRB.setChecked(true);
                    genderString = "F";
                } else if (dataResponse.gender.equalsIgnoreCase("M")) {
                    genderTV.setText("Male");
                    maleRB.setChecked(true);
                    genderString = "M";
                } else if (dataResponse.gender.equalsIgnoreCase("O")) {
                    genderTV.setText("Other");
                    otherRB.setChecked(true);
                    genderString = "O";
                }
            }
        }
    }

    @Override
    public void showLoader() {
        spinKitView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editProfileLL:
                beforeEditLL.setVisibility(View.GONE);
                afterEditLL.setVisibility(View.VISIBLE);
                break;

            case R.id.saveTV:
                if (!Helper.isContainValue(userNameET.getText().toString().trim())) {
                    showMessage("Please enter name");
                } else if (dayString.equalsIgnoreCase("Day")) {
                    showMessage("Please select day");
                } else if (monthString.equalsIgnoreCase("Month")) {
                    showMessage("Please select month");
                } else if (yearString.equalsIgnoreCase("Year")) {
                    showMessage("Please select year");
                } else if (!Helper.isContainValue(genderString)) {
                    showMessage("Please select gender");
                } else {
                    dobString = dayString + "/" + monthString + "/" + yearString;
                    nameString = userNameET.getText().toString().trim();
                    emailString = emailET.getText().toString().trim();
                    mobileString = mobileET.getText().toString().trim();

                    callUpdateUserApi();
                }
                break;

            case R.id.maleRB:
                genderString = "M";
                break;

            case R.id.femaleRB:
                genderString = "F";
                break;

            case R.id.otherRB:
                genderString = "O";
                break;

            case R.id.userImageRL:
                openGallery();
                break;

            case R.id.userImageLL1:
                openGallery();
                break;

            default:
                break;
        }
    }

    private void openGallery() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, AppConstants.REQUEST_CODE_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callUpdateUserApi() {
        if (Helper.isNetworkAvailable(context)) {
            showLoader();
            profilePresenter.callUpdateUserApi(genderString, dobString, nameString, base64Path, imageName);
        } else {
            showMessage(getString(R.string.please_check_internet_connection));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if (requestCode == AppConstants.REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                if (selectedImage != null) {
                    cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }

                userImageLL1.setVisibility(View.GONE);
                userImageRL.setVisibility(View.VISIBLE);

                if (Helper.isContainValue(picturePath)) {
                    InputStream imageStream = null;
                    int orientation = 0;
                    try {
                        if (selectedImage != null) {
                            imageStream = context.getContentResolver().openInputStream(selectedImage);
                            orientation = getOrientation(context, selectedImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    if (orientation != 0) {
                        yourSelectedImage = rotateBitmap(yourSelectedImage, orientation);
                    }
                    userImageCircle1.setImageBitmap(yourSelectedImage);
                    base64Path = encodeToBase64(yourSelectedImage, picturePath);

                    if (picturePath.contains("/")) {
                        String[] splitString = picturePath.split("/");
                        imageName = splitString[splitString.length - 1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        try {
            Matrix matrix = new Matrix();
            matrix.setRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    private String encodeToBase64(Bitmap image, String picturePath) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (picturePath.endsWith("jpg") || picturePath.endsWith("jpeg") || picturePath.endsWith("JPG") || picturePath.endsWith("JPEG")) {
                image.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            } else {
                image.compress(Bitmap.CompressFormat.PNG, 10, baos);
            }
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picturePath;
    }

    private int getOrientation(Context context, Uri photoUri) {
        try {
            Cursor cursor = context.getContentResolver().query(photoUri,
                    new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

            if (cursor != null && cursor.getCount() != 1) {
                return -1;
            }
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setUpdateData(UpdateUserResponse updateUserResponse) {
        if (updateUserResponse != null) {
            if (!updateUserResponse.status.equalsIgnoreCase("fail")) {
                beforeEditLL.setVisibility(View.VISIBLE);
                afterEditLL.setVisibility(View.GONE);

                userNameTV.setText(nameString);
                emailTV.setText(emailString);
                mobileTV.setText(mobileString);
                dobTV.setText(dobString);
                if (genderString.equalsIgnoreCase("F")) {
                    genderTV.setText("Female");
                } else if (genderString.equalsIgnoreCase("M")) {
                    genderTV.setText("Male");
                } else if (genderString.equalsIgnoreCase("O")) {
                    genderTV.setText("Other");
                }

                if (updateUserResponse.data != null && Helper.isContainValue(updateUserResponse.data.picture)) {
                    userImageLL.setVisibility(View.GONE);
                    userImageCircle.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(updateUserResponse.data.picture)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .into(userImageCircle);
                }

                if (Helper.isNetworkAvailable(context)) {
                    profilePresenter.getUserData();
                }
            } else if (updateUserResponse.status.equalsIgnoreCase("fail") && Helper.isContainValue(updateUserResponse.message)) {
                showMessage(updateUserResponse.message);
            }
        }
    }

    @Override
    public void getUserData(GetUserResponse getUserResponse) {
        if (getUserResponse != null && getUserResponse.data != null) {
            DataHolder.getInstance().getUserDataresponse = getUserResponse.data;

            if (DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(AppConstants.USER_DATA_UPDATE));
            }
        }
    }

    @Override
    public void onDestroy() {
        if (profilePresenter != null) {
            profilePresenter.onDestroyedView();
        }
        super.onDestroy();
    }
}