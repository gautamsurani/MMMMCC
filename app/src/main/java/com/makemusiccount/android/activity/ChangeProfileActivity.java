package com.makemusiccount.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.MyRoundImageView;
import com.makemusiccount.android.util.AndroidMultiPartEntity;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ChangeProfileActivity extends AppCompatActivity {

    Activity context;

    Toolbar toolbar;

    EditText etName, etEmail, etMobile, etPassword;

    TextView tvUpdate, tvPage;

    MyRoundImageView imgUpdateImage;

    Boolean IsFileMAke = false;

    String uploadFilePath = "";

    String file_data = "no";

    ImageView imgChangeImage;

    private static final int CAMERA_CAPTURE = 1;

    private static final int GALLERY_CAPTURE = 2;

    boolean IsAllowAgain = true;

    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 7;

    String Name = "", Email = "", MobileNo = "", Password = "";

    ProgressDialog loading;

    String outPath_Camera, resMessage = "", resCode = "";

    String userID = "", name = "", email = "", phone = "", image = "", currentPass = "", newPass = "", reTypePass = "";

    String upLoadServerUri = "https://www.makemusiccount.online/mmc/index.php?view=change_info";

    LinearLayout llProfile, llPassword, llProfileView, llPasswordView, llMembership;

    TextView showHideCPassword, showHideNPassword, showHideRPassword, tvUpdatePassword;

    EditText etCurrentPassword, etNewPassword, etReTypePassword;

    ImageView ivProfile, ivLock;

    ProgressDialog progressDialog;

    Global global;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_profile);

        context = this;

        global = new Global(context);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        }

        String page = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            page = bundle.getString("page", "");
        }

        initToolbar();

        initComp();

        tvPage.setText("My Profile");

        if (page.equals("profile")) {
            onProfileClick();
        } else if (page.equals("password")) {
            onPasswordClick();
        }

        showHideCPassword.setOnClickListener(view -> {
            if (showHideCPassword.getText().equals("Hide")) {
                showHideCPassword.setText("Show");
                etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (showHideCPassword.getText().equals("Show")) {
                showHideCPassword.setText("Hide");
                etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        llMembership.setOnClickListener(view -> {
            startActivity(new Intent(context, MembershipActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        showHideNPassword.setOnClickListener(view -> {
            if (showHideNPassword.getText().equals("Hide")) {
                showHideNPassword.setText("Show");
                etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (showHideNPassword.getText().equals("Show")) {
                showHideNPassword.setText("Hide");
                etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        showHideRPassword.setOnClickListener(view -> {
            if (showHideRPassword.getText().equals("Hide")) {
                showHideRPassword.setText("Show");
                etReTypePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (showHideRPassword.getText().equals("Show")) {
                showHideRPassword.setText("Hide");
                etReTypePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        llProfile.setOnClickListener(view -> onProfileClick());

        llPassword.setOnClickListener(view -> onPasswordClick());

        File createFolder = new File("sdcard/MMC/Temp");
        if (!createFolder.exists()) {
            createFolder.mkdirs();
        }

        File createFolder2 = new File("sdcard/MMC/Temp/Camera");
        if (!createFolder2.exists()) {
            createFolder2.mkdirs();
        }

        etEmail.setText(Util.getEmail(context));
        etName.setText(Util.getUserName(context));
        etMobile.setText(Util.getMobileNo(context));

        Glide.with(context)
                .load(Util.getUserImage(context))
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(imgUpdateImage);

        imgChangeImage.setOnClickListener(v -> ShowDialogForImage());

        tvUpdate.setOnClickListener(view -> {
            Name = etName.getText().toString();
            Email = etEmail.getText().toString();
            MobileNo = etMobile.getText().toString();
            Password = etPassword.getText().toString();
            if (Name.equals("")) {
                Toast.makeText(context, "Please Enter Name!!!", Toast.LENGTH_SHORT).show();
            } else if (Email.equals("")) {
                Toast.makeText(context, "Please Enter Email!!!", Toast.LENGTH_SHORT).show();
            } else if (MobileNo.equals("")) {
                Toast.makeText(context, "Please Enter Mobile No!!!", Toast.LENGTH_SHORT).show();
            } else {
                loading = ProgressDialog.show(context, "", "Please wait...", false, false);
                new Thread(() -> {
                    runOnUiThread(() -> {
                    });
                    Util.hideKeyboard(context);
                    UploadFileToServer task = new UploadFileToServer();
                    task.execute();
                }).start();
            }
        });

        tvUpdatePassword.setOnClickListener(view -> {
            currentPass = etCurrentPassword.getText().toString();
            newPass = etNewPassword.getText().toString();
            reTypePass = etReTypePassword.getText().toString();
            if (currentPass.isEmpty()) {
                Toast.makeText(context, "Enter current password", Toast.LENGTH_SHORT).show();
            } else if (newPass.isEmpty()) {
                Toast.makeText(context, "Enter new password", Toast.LENGTH_SHORT).show();
            } else if (!reTypePass.equals(newPass)) {
                Toast.makeText(context, "Retype password doesn't match", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new ChangePassword().execute();
                } else {
                    global.retryInternet("change_password");
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class ChangePassword extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strLogin = AppConstant.API_CHANGE_PASSWORD + Util.getUserId(context)
                    + "&oldpass=" + currentPass
                    + "&newpass=" + newPass;

            String strTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strTrim", strTrim);
            try {
                RestClient restClient = new RestClient(strTrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String APIString = restClient.getResponse();
                Log.e("APIString", APIString);

                if (APIString != null && APIString.length() != 0) {
                    jsonObjectList = new JSONObject(APIString);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onProfileClick() {
        llProfileView.setVisibility(View.VISIBLE);
        llPasswordView.setVisibility(View.GONE);
        ivProfile.setImageResource(R.drawable.profile_shadow);
        ivLock.setImageResource(R.drawable.lock);
        int padding = Util.convertDpToPixel(5, context);
        ivLock.setPadding(padding, padding, padding, padding);
        ivProfile.setPadding(0, 0, 0, 0);
    }

    private void onPasswordClick() {
        llPasswordView.setVisibility(View.VISIBLE);
        llProfileView.setVisibility(View.GONE);
        ivProfile.setImageResource(R.drawable.profile);
        ivLock.setImageResource(R.drawable.lock_shadow);
        int padding = Util.convertDpToPixel(5, context);
        ivProfile.setPadding(padding, padding, padding, padding);
        ivLock.setPadding(0, 0, 0, 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(upLoadServerUri);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        num -> {
                        });
                if (file_data.equals("yes")) {
                    File sourceFile = new File(uploadFilePath);
                    entity.addPart("userphoto", new FileBody(sourceFile));
                }
                entity.addPart("username", new StringBody(Name));
                entity.addPart("useremail", new StringBody(Email));
                entity.addPart("userphone", new StringBody(MobileNo));
                entity.addPart("userpass", new StringBody(Password));
                entity.addPart("userID", new StringBody(Util.getUserId(context)));
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
                Log.e("response", responseString);
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String temp) {
            IsFileMAke = false;
            loading.dismiss();
            file_data = "no";
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(temp);
                String resMessage = jsonObject.getString("message");
                String resCode = jsonObject.getString("msgcode");
                if (resCode.equalsIgnoreCase("0")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("detail");
                    JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                    if (jsonObjectList != null && jsonObjectList.length() != 0) {
                        userID = jsonObjectList.getString("userID");
                        name = jsonObjectList.getString("name");
                        image = jsonObjectList.getString("image");
                        email = jsonObjectList.getString("email");
                        phone = jsonObjectList.getString("phone");
                    }
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e("AA", e.getMessage());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();
        if (addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");
        if (addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return !shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || perms.get(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
                        IsAllowAgain = false;
                        CRateWhyDialog();
                    }
                    if (IsAllowAgain) {
                        insertDummyContactWrapper();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void CRateWhyDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_new_login_no_permission);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCanceledOnTouchOutside(true);

            TextView txtDialogBottomText = dialog.findViewById(R.id.txtDialogBottomText);

            txtDialogBottomText.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
                finish();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        tvUpdate = findViewById(R.id.tvUpdate);
        llProfile = findViewById(R.id.llProfile);
        llPassword = findViewById(R.id.llPassword);
        llProfileView = findViewById(R.id.llProfileView);
        llPasswordView = findViewById(R.id.llPasswordView);
        imgUpdateImage = findViewById(R.id.imgUpdateimage);
        imgChangeImage = findViewById(R.id.imgChangeImage);
        showHideCPassword = findViewById(R.id.showHideCPassword);
        showHideNPassword = findViewById(R.id.showHideNPassword);
        showHideRPassword = findViewById(R.id.showHideRPassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etReTypePassword = findViewById(R.id.etReTypePassword);
        ivProfile = findViewById(R.id.ivProfile);
        ivLock = findViewById(R.id.ivLock);
        tvPage = findViewById(R.id.tvPage);
        llMembership = findViewById(R.id.llMembership);
        tvUpdatePassword = findViewById(R.id.tvUpdatePassword);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        TextView tvDate = findViewById(R.id.tvDate);
        Util.setDate(tvDate);
    }

    private void ShowDialogForImage() {
        android.support.v7.app.AlertDialog.Builder Dilaog_3DMethod = new android.support.v7.app.AlertDialog.Builder(context);
        Dilaog_3DMethod.setCancelable(true);
        final CharSequence[] items = {getResources().getString(R.string.str_common_TakeCamera), getResources().getString(R.string.str_common_TakeGallery)};
        Dilaog_3DMethod.setItems(items, (dialog, item) -> {
            if (item == 0) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                Random as = new Random();
                int a = as.nextInt();
                outPath_Camera = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MMC/Temp/Camera/" + a + ".jpg";
                File outFile = new File(outPath_Camera);
                Uri outURI = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outURI);
                startActivityForResult(intent, CAMERA_CAPTURE);
            } else {
                Intent captureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(captureIntent, GALLERY_CAPTURE);
            }
        });
        final AlertDialog asd_Dialog = Dilaog_3DMethod.create();
        asd_Dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        File f = new File(outPath_Camera);
                        String path = f.getAbsolutePath();
                        try {
                            IsFileMAke = true;
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            imgUpdateImage.setImageBitmap(bmp);
                            uploadFilePath = path;
                            file_data = "yes";
                        } catch (Exception e) {
                            IsFileMAke = false;
                            Toast.makeText(context, "Allow Make Music Count to access Storage", Toast.LENGTH_SHORT).show();
                            file_data = "no";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GALLERY_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    assert uri != null;
                    Log.d("File Uri:", "File Uri: " + uri.toString());
                    String path;
                    try {
                        IsFileMAke = true;
                        path = getPath(context, uri);
                        Bitmap bmp = BitmapFactory.decodeFile(path);
                        imgUpdateImage.setImageBitmap(bmp);
                        uploadFilePath = path;
                        file_data = "yes";
                    } catch (Exception e) {
                        Toast.makeText(context, "Allow Make Music Count to access Storage", Toast.LENGTH_SHORT).show();
                        file_data = "no";
                        IsFileMAke = false;
                        e.printStackTrace();
                    }
                }
                break;
            case NO_NETWORK_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String extraValue = data.getStringExtra("extraValue");
                    if (extraValue.equalsIgnoreCase("change_password")) {
                        new ChangePassword().execute();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
