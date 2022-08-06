package com.gramup.lead.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gramup.lead.BuildConfig;
import com.gramup.lead.Multipart.FileBody;
import com.gramup.lead.Multipart.MultipartEntity;
import com.gramup.lead.Multipart.StringBody;
import com.gramup.lead.R;
import com.gramup.lead.activity.LeadsMainActivity;
import com.gramup.lead.activity.MainActivity;
import com.gramup.lead.model.State;
import com.gramup.lead.model.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.icu.util.ULocale.getName;
import static com.gramup.lead.activity.CorporateRegistrationActivity.trustEveryone;
import static com.gramup.lead.model.FileUtils.getPath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

public class LeadsFragmentA extends Fragment {


    EditText edt_qty,edt_budget,edt_description,edt_hsncode,edt_remark;
    ImageView img_attachement;
    Spinner spinner_item;
    Button button_post;
    SharedPreferences userpreferences;
    String Token="";
    Utility utility;
    private ProgressDialog progressDialog;
    private long totalSize;
    ArrayList<State> ProductArrayList;
    public static String USERINFO="USER";
    String ItemId="",ItemDescription="",Price="",UserCode="",Quantity="",Description="",Date="",HSNCode="",Attachment="";

    private static int RESULT_LOAD_IMG = 2;

    private static final int RESULT_CAPTURE_IMG = 3;
    private static final int RESULT_DOCUMENT = 4;
    private Uri outPutfileUri;
    private int APP_REQUEST_CODE = 4478;
    File Attachmentfile;
    private File file;
    private String dateString="";
    private String FinalJson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.lead_fragment, container, false);

        progressDialog=new ProgressDialog(getActivity());
        userpreferences = getActivity().getSharedPreferences(USERINFO,
                Context.MODE_PRIVATE);
        utility=new Utility();
        ProductArrayList=new ArrayList<>();
        UserCode=userpreferences.getString("code","");


        edt_qty=rootView.findViewById(R.id.edt_qty);
        edt_budget=rootView.findViewById(R.id.edt_budget);
        edt_description=rootView.findViewById(R.id.edt_description);
        spinner_item=rootView.findViewById(R.id.edt_product);
        //edt_hsncode=rootView.findViewById(R.id.edt_hsncode);
        button_post=rootView.findViewById(R.id.button_post);
        img_attachement=rootView.findViewById(R.id.img_attachement);


        img_attachement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoreImages();
            }
        });
        long date1 = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(date1);

        Token=userpreferences.getString("Token","");
        if (utility.isNet(getActivity())){
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            GetProduct getProduct=new GetProduct();
            getProduct.execute();
        }

        spinner_item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (ProductArrayList.size()>0) {
                    ItemId = String.valueOf(ProductArrayList.get(position).getId());
                    ItemDescription = ProductArrayList.get(position).getName();
                  //  Price = ProductArrayList.get(position).getPrice();
                    //edt_budget.setText(Price);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Description=edt_description.getText().toString();
                Price=edt_budget.getText().toString();
                Quantity=edt_qty.getText().toString();
             //   ItemDescription=edt_product.getText().toString();
               // HSNCode=edt_hsncode.getText().toString();



                if (ItemDescription.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Please enter product name",Toast.LENGTH_LONG).show();
                }else if (Description.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Please enter product description",Toast.LENGTH_LONG).show();
                }
                else if (Quantity.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Please enter quantity",Toast.LENGTH_LONG).show();
                }
                else if (Price.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),"Please enter price",Toast.LENGTH_LONG).show();
                }else {

                    if (utility.isNet(getActivity())) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_code", UserCode);
                            jsonObject.put("remarks",Description );
                            jsonObject.put("required_on", dateString);
                            jsonObject.put("delivery_address", "Pune");
                            JSONArray arrForA = new JSONArray();
                            JSONObject itemA = new JSONObject();
                            itemA.put("item_master_id", ItemId);
                            itemA.put("item_name", ItemDescription);
                            itemA.put("item_description", ItemDescription);
                            itemA.put("item_best_price", Price);
                            itemA.put("qty", Quantity);
                            arrForA.put(itemA);
                            jsonObject.put("items", arrForA);

                            FinalJson = jsonObject.toString();

                       /* UploadFileToServer uploadFileToServer = new UploadFileToServer();
                        uploadFileToServer.execute();*/

                            CreateLeadPost createLeadPost = new CreateLeadPost();
                            createLeadPost.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        return rootView;




    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
    class GetProduct extends AsyncTask<String, Void, String> {
        String url, res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();
            if (!integer.equalsIgnoreCase("")) {


                try {
                    ProductArrayList.clear();
                    JSONArray jResults = new JSONArray(integer);
                    for (int i = 0; i < jResults.length(); i++) {
                        JSONObject jsonObject = jResults.getJSONObject(i);
                        State state = new State();
                        state.setId(jsonObject.getInt("id"));//UserLoginId
                        state.setName(jsonObject.getString("name"));
                        state.setPrice(jsonObject.getString("price"));
                        ProductArrayList.add(state);
                    }
                    ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(getActivity(), android.R.layout.simple_spinner_item, ProductArrayList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_item.setAdapter(dataAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                if (utility.isNet(getActivity())) {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Record not found", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String doInBackground(String ... params) {


            try {

                url = getResources().getString(R.string.baseurl) + "/items";

                URL mUrl = new URL(url);
                trustEveryone();
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
                httpConnection.setRequestProperty("Authorization", "Bearer"+" "+Token);

/*                Token = userpreferences.getString("userid", "");
                httpConnection.setRequestProperty("Cookie", Token);*/
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                //SSLSocketFactory sslSocketFactory;
                //sslSocketFactory = new TLSSocketFactory();
                // HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value

            // updating percentage value
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            trustEveryone();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.baseurl)
                    +"/requisition");

            try {
                MultipartEntity multipartEntity=new MultipartEntity();
                long date1 = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String dateString = sdf.format(date1);


                // Extra parameters if you want to pass to server ### Use here our text inputs

               // multipartEntity.addPart("item_id", new StringBody(ItemId));
                multipartEntity.addPart("item_name", new StringBody(ItemDescription));
                multipartEntity.addPart("item_description", new StringBody(ItemDescription));
                multipartEntity.addPart("item_best_price", new StringBody(Price));
                multipartEntity.addPart("user_code", new StringBody(UserCode));
                multipartEntity.addPart("remarks", new StringBody(Description));
                multipartEntity.addPart("required_on", new StringBody(dateString));
                multipartEntity.addPart("hsn_code", new StringBody(HSNCode));
                if (file == null) {
                    multipartEntity.addPart("file", new StringBody(""));
                }else {
                    multipartEntity.addPart("file", new FileBody(file));
                }
                multipartEntity.addPart("qty", new StringBody(Quantity));



                totalSize = multipartEntity.getContentLength();
                httppost.setEntity(multipartEntity);

                // Making server call
                Token=userpreferences.getString("Token","");
                httppost.setHeader("Authorization", "Bearer"+" "+Token);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            // showing the server response in an alert dialog

            super.onPostExecute(result);
            progressDialog.dismiss();

            try {
                if (result.contains("items_master_id")) {
                    edt_budget.setText("");
                    edt_description.setText("");
                    edt_qty.setText("");

                    Toast.makeText(getActivity(), "Requirement posted successfully", Toast.LENGTH_SHORT).show();

                }else {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("original");
                    Toast.makeText(getActivity(),ja.toString(),Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                SharedPreferences.Editor editor = userpreferences.edit();
                editor.remove("Token");
                editor.remove("Type");
                editor.remove("Login");
                editor.remove("Role");
                editor.remove("code");
                editor.commit();
                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                progressDialog.dismiss();
            }
        }

    }



    private void addMoreImages() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.choose_attachment_option_dialog);
        dialog.setTitle(getResources().getString(R.string.app_name));
        TextView camera = (TextView) dialog.findViewById(R.id.camera);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        TextView textViewCancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView document=dialog.findViewById(R.id.document);
        gallery.setVisibility(View.VISIBLE);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestCameraPermission();

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestGalleryPermission();

            }
        });
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestDocumentPermission();

            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void startGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMG);

    }

    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "attachment.jpg");
        outPutfileUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        startActivityForResult(intent, RESULT_CAPTURE_IMG);
    }
    private String getRealPathFromURI(Uri outPutfileUri) {
        Cursor cur = getActivity().getContentResolver().query(outPutfileUri, null, null, null, null);
        cur.moveToFirst();
        int idx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cur.getString(idx);

    }

    public void handleSendImage(Uri imageUri) throws IOException {
        //Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            file = new File(getActivity().getCacheDir(), "image");
            InputStream inputStream=getActivity().getContentResolver().openInputStream(imageUri);
            try {

                OutputStream output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }

                    output.flush();
                } finally {
                    output.close();
                }
            } finally {
                inputStream.close();
                byte[] bytes =getFileFromPath(file);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmapToUriConverter(bitmap);
                //Upload Bytes.
            }
        }
    }

    public static byte[] getFileFromPath(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;


        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, w, h,
                    true);
            String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString();
            File file = new File(path1 + "/" + "POS"+"/"+"Sender");
            if (!file.exists())
                file.mkdirs();
            File file1 = new File(file, "Image-"+ new Random().nextInt() + ".jpg");
            if (file1.exists())
                file1.delete();
           /* File file = new File(SharefunctionActivity.this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");*/
            FileOutputStream out = new FileOutputStream(file1);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
            String attachment = file1.getAbsolutePath();
            file = new File(attachment);
            Attachment=file.getName();
            Toast.makeText(getActivity(),"Image attached successfully",Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static String getRealPathFromUri(Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

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
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    private void requestDocumentPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    201);
        } else {
/*
            DocumentIntent();
*/
            //  browseDocuments();
            // getDocument();

            Intent intent;
            if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        RESULT_DOCUMENT);
            } else {

                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip", "application/vnd.android.package-archive"};

                intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        RESULT_DOCUMENT);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    startCameraIntent();
                }
                break;
            case 201:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startGalleryIntent();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                    200);
        } else {
            startCameraIntent();
        }
    }

    private void requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    201);
        } else {
            startGalleryIntent();
        }
    }
    public static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null) {
            String path = getPath(context, uri);
            if (path == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    filename = getName(uri.toString());
                }
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null,
                    null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }

        return filename;
    }



    public static void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_CAPTURE_IMG && resultCode == getActivity().RESULT_OK) {
                String uri = outPutfileUri.toString();
                Log.e("uri-:", uri);
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), outPutfileUri);
                    FileOutputStream out = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                    String url = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap,"IMG_" + Calendar.getInstance().getTime(),null);
                    outPutfileUri = Uri.parse(url);
                    if (outPutfileUri.toString().contains("content")) {
                        handleSendImage(outPutfileUri);
                    }else {
                        file = new File(getRealPathFromUri(getActivity(),outPutfileUri));//create path from uri
                                            }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (requestCode == RESULT_LOAD_IMG && resultCode == getActivity().RESULT_OK && null != data) {

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getData() != null) {
                    outPutfileUri = data.getData();
                    // Get the cursor
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outPutfileUri);
                    //	uploadFileBitMap = bitmap;
                    file = new File(getRealPathFromURI(outPutfileUri));
                    FileOutputStream out = new FileOutputStream(Attachmentfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
                    String url = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap,"IMG_" + Calendar.getInstance().getTime(),null);
                    outPutfileUri = Uri.parse(url);
                    if (outPutfileUri.toString().contains("content")) {
                        handleSendImage(outPutfileUri);
                    }else {
                        Attachmentfile = new File(getRealPathFromUri(getActivity(),outPutfileUri));//create path from uri
                        // attachment = file.getName();


                    }



                    //img_userpic.setImageURI(fileUri);
                    //callChangeProfileImageApi(file.getAbsoluteFile().toString());


                } else {
                    Toast.makeText(getActivity(), "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            }else if (requestCode == RESULT_DOCUMENT && null != data) {

                try {
                  /*  Uri selectedFileURI = data.getData();
                    File file = new File(getRealPathFromUri(CreateAssignmentActivity.this, selectedFileURI));//create path from uri
                    Log.d("", "File : " + file.getName());
                    attachment = file.toString();
                    Attachment = file.getName();
                    txt_file.setText(Attachment);*/

                    outPutfileUri = data.getData();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                         file = new File(getRealPathFromUri(getActivity(), outPutfileUri));//create path from uri
                        Log.d("", "File : " + file.getName());
                        String attachment = file.toString();
                        Attachment = file.getName();
                    }





                    Toast.makeText(getActivity(), "Document attached successfully", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    e.printStackTrace();

                    String id = DocumentsContract.getDocumentId(outPutfileUri);
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(outPutfileUri);
                    file = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + id);
                    writeFile(inputStream, file);
                    String attachment = file.getAbsolutePath();
                    Attachment = getFileName(getActivity(), outPutfileUri);
                    Toast.makeText(getActivity(), "Document attached successfully", Toast.LENGTH_SHORT).show();

                }


            }
            else {
                if (requestCode == APP_REQUEST_CODE) {
                    Toast.makeText(getActivity(), "verification cancel",
                            Toast.LENGTH_LONG).show();
                } else if (requestCode == RESULT_LOAD_IMG) {
                    Toast.makeText(getActivity(), "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


    }
    public class CreateLeadPost extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progreass_lay);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }

        @Override
        protected String doInBackground(String... params) {



            try {

               // trustEveryone();
                String url_string ="https://gram-up.com/gramup/api/requisition";

                URL mUrl = new URL(url_string);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.addRequestProperty("Content-Type", "application/json");
                httpConnection.addRequestProperty("Client-Platform", "android");
                Token=userpreferences.getString("Token","");
                httpConnection.setRequestProperty("Authorization", "Bearer"+" "+Token);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);

                httpConnection.connect();

                DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream ());
                wr.writeBytes(FinalJson);
                wr.flush();
                wr.close();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            try {
                if (result.contains("reference_requisition_no")) {
                    edt_budget.setText("");
                    edt_description.setText("");
                    edt_qty.setText("");
                   // edt_hsncode.setText("");

                    Toast.makeText(getActivity(), "Requirement posted successfully", Toast.LENGTH_SHORT).show();

                }else {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("original");
                    Toast.makeText(getActivity(),ja.toString(),Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                SharedPreferences.Editor editor = userpreferences.edit();
                editor.remove("Token");
                editor.remove("Type");
                editor.remove("Login");
                editor.remove("Role");
                editor.remove("code");
                editor.commit();
                startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                progressDialog.dismiss();
            }
        }

    }


}