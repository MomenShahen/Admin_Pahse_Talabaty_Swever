package com.talabaty.swever.admin.Montagat.AddMontag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.talabaty.swever.admin.Montagat.FragmentMontag;
import com.talabaty.swever.admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.app.Activity.RESULT_OK;
import static android.support.v7.widget.AppCompatDrawableManager.get;

public class AddMontag extends Fragment {
    // For Color
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ColorCode> colorCodes;
    List<ColorCode> temp;

    // For Image
    RecyclerView image_rec;
    RecyclerView.Adapter image_adap;
    List<Bitmap> imageSources;
    List<Bitmap> imageTemp;

    // For Size
    RecyclerView size_rec;
    RecyclerView.Adapter size_adap;
    List<Size> sizeDimention;
    List<Size> sizeTemp;


    //For Uploading To Server
    Sanf sanf;
    List<ColorCode> colorStrings;
    List<Size> sizeStrings;
    List<ImageContainer> imageStrings;
    List<Uri> imageUri;
    List<Uri> tempimageUri;


    //Views
    Button choose_color, back, choose_size, add_image;
    static int color = 0xffffff00;
    FragmentManager fragmentManager;
    Button save, empty;
    // First CardView
    EditText sanf_name, initialamount, desc;
    // Second CardView
    EditText buy_price, critical_amount, summary;
    // Third CardView
    EditText buyex_price, notes;
    Spinner department;
    List<String> DepatmentList, indexOfDepatmentList;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL = "http://192.168.0.107/uploads/UploadAndro";

    private String KEY_IMAGE = "base64imageString";
    private String KEY_NAME = "name";
    List<byte[]> bytes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_montag, container, false);
        // Normal Views (Edittext, Spinner, Buttons)
        sanf_name = view.findViewById(R.id.sanf_name);
        initialamount = view.findViewById(R.id.initialamount);
        desc = view.findViewById(R.id.desc);
        buy_price = view.findViewById(R.id.buy_price);
        critical_amount = view.findViewById(R.id.critical_amount);
        summary = view.findViewById(R.id.summary);
        buyex_price = view.findViewById(R.id.buyex_price);
        notes = view.findViewById(R.id.notes);
        department = view.findViewById(R.id.department);


        // RecycleViews
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.color_rec);
        recyclerView.setLayoutManager(layoutManager);
        colorCodes = new ArrayList<>();
        temp = new ArrayList<>();

        image_rec = (RecyclerView) view.findViewById(R.id.image_rec);
        image_rec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        imageSources = new ArrayList<>();
        imageTemp = new ArrayList<>();

        size_rec = (RecyclerView) view.findViewById(R.id.size_rec);
        size_rec.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        sizeDimention = new ArrayList<>();
        sizeTemp = new ArrayList<>();

        choose_color = view.findViewById(R.id.choose_color);
        choose_size = view.findViewById(R.id.size);
        add_image = view.findViewById(R.id.add_image);


        back = view.findViewById(R.id.back);
        save = view.findViewById(R.id.save);
        empty = view.findViewById(R.id.empty);


        colorStrings = new ArrayList<>();
        sizeStrings = new ArrayList<>();
        imageStrings = new ArrayList<>();
        imageUri = new ArrayList<>();
        tempimageUri = new ArrayList<>();
        bytes = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fragmentManager = getFragmentManager();
        temp = colorCodes;

        DepatmentList = new ArrayList<>();
        indexOfDepatmentList = new ArrayList<>();

        loadDepartment();
        sanf = new Sanf();
        choose_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(false);
            }
        });

        choose_size.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                final EditText size = new EditText(getActivity());
                size.setHint("اضف مقاس المنتج");
                // Text Type
//                size.setInputType(InputType.TYPE_CLASS_NUMBER);
                size.setTextColor(R.color.colorPrimaryDark);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("المقاس")
                        .setView(size)
                        .setCancelable(false)
                        .setPositiveButton(" تم ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (size.getText().toString().isEmpty()) {
                                    Toast.makeText(getActivity(), "من فضلك قم بإدخال مقاس مناسب", Toast.LENGTH_SHORT).show();
                                } else {
                                    displaySize(size.getText().toString());
                                }
                                if (size != null) {
                                    ViewGroup parent = (ViewGroup) size.getParent();
                                    if (parent != null) {
                                        parent.removeAllViews();
                                    }
                                }
                            }
                        })
                        .setNegativeButton(" إلغاء ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do Nothing
                                if (size != null) {
                                    ViewGroup parent = (ViewGroup) size.getParent();
                                    if (parent != null) {
                                        parent.removeAllViews();
                                    }
                                }
                            }
                        }).show();
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                sanf.setName(sanf_name.getText().toString());
                sanf.setAmount(initialamount.getText().toString());
                sanf.setBuyPrice(buy_price.getText().toString());
                sanf.setCriticalQuantity(critical_amount.getText().toString());
                sanf.setDescription(desc.getText().toString());
//                sanf.setEditDate("fbfb");
                sanf.setEditUserId("3");
                sanf.setId(0);
                sanf.setUserId("3");
                sanf.setSummary(summary.getText().toString());
                sanf.setShop_Id("5");
                sanf.setSampleCatogoriesId("3");
                sanf.setNotes(notes.getText().toString());
                sanf.setInsertDate("3");
                sanf.setSellPrice(buyex_price.getText().toString());
                sanf.setColorCodes(colorStrings);
                sanf.setSizeList(sizeStrings);
                uploadImage();
                final String jsonInString = gson.toJson(sanf);

                Log.e("Data", jsonInString);
            }
        });

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sanf_name.setText("");
                initialamount.setText("");
                desc.setText("");
                buy_price.setText("");
                critical_amount.setText("");
                summary.setText("");
                buyex_price.setText("");
                notes.setText("");

                // Empty Colors
                final int size = colorCodes.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        colorCodes.remove(0);
                    }
                    adapter.notifyItemRangeRemoved(0, size);
                }
                adapter = new ColorAdapter(getActivity(), colorCodes);
                recyclerView.setAdapter(adapter);

                // Empty Images
                final int sizeImages = imageSources.size();
                if (sizeImages > 0) {
                    for (int i = 0; i < sizeImages; i++) {
                        imageSources.remove(0);
                        imageUri.remove(0);
                    }
                    image_adap.notifyItemRangeRemoved(0, sizeImages);
                }
                image_adap = new ImageAdapter(getActivity(), imageSources, imageUri);
                image_rec.setAdapter(image_adap);

                // Empty Sizes
                final int sizeSizes = sizeDimention.size();
                if (sizeSizes > 0) {
                    for (int i = 0; i < sizeSizes; i++) {
                        sizeDimention.remove(0);
                    }
                    size_adap.notifyItemRangeRemoved(0, size);
                }
                size_adap = new SizeAdapter(getActivity(), sizeDimention);
                size_rec.setAdapter(size_adap);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.frame_mabi3at, new FragmentMontag()).addToBackStack("FragmentMontag").addToBackStack("FragmentMontag").commit();
            }
        });
    }

    private void uploadImage() {
        Gson gson = new Gson();
        Log.e("Start: ", "1");
//        List<String> strings = new ArrayList<>();
//        String imageList = "[";
//        for (int x=0 ; x<imageStrings.size(); x++){
//            Log.e("Image ", getStringImage(imageSources.get(x)));
//            Log.e("Image ", imageStrings.get(0).getSource());

//            imageStrings.add(getStringImage(imageSources.get(x)));
//            imageStrings.add(getStringImage(imageSources.get(x)));
//                imageList+=  ",";
//        imageStrings.get(0).getSource().length();
//            Log.e("Start ", "[ "+bytes.get(0)+" ]");
//        }

//        imageList+="]";

//        String test = imageList;
        final String allImages = gson.toJson(imageStrings);
                Log.e("Start: ", allImages);
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Log.e("Path: ", s);
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

//                        for (int x=0; x<imageStrings.size(); x++){
//                            imageStrings.remove(0);
//                        }
//                        imageStrings.add(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
//                for (int x= 0; x<imageSources.size(); x++) {
//                    String image = getStringImage(bitmap);
//                }

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, allImages);

                params.put(KEY_NAME, "Mohamed");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    private void loadDepartment() {

        DepatmentList = new ArrayList<>();
        indexOfDepatmentList = new ArrayList<>();

        if (DepatmentList.size()>0){
            for (int x=0; x<DepatmentList.size(); x++){
                DepatmentList.remove(x);
                indexOfDepatmentList.remove(x);
            }
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.sellsapi.sweverteam.com/SampleProduct/SelectSampleCatogories", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("SampleCatogory");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String fname = jsonObject1.getString("Name");
                        String id = jsonObject1.getString("Id");
                        DepatmentList.add(fname);
                        indexOfDepatmentList.add(id);

                    }

                    department.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, DepatmentList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError)
                    Toast.makeText(getActivity(), "خطأ إثناء الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                else if (error instanceof NetworkError)
                    Toast.makeText(getActivity(), "خطأ فى شبكه الانترنت", Toast.LENGTH_SHORT).show();
                else if (error instanceof TimeoutError)
                    Toast.makeText(getActivity(), "خطأ فى مده الانتظار", Toast.LENGTH_SHORT).show();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    void openDialog(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), color, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
                AddMontag.color = color;
                //Todo: Check List To Upload
                colorStrings.add(new ColorCode(0,String.format("0x%08x", color),3,5));
                displayColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getActivity(), "cancel", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    void displaySize(String sizetype) {
        final int size = sizeDimention.size();
//        ContactItem []x = new ContactItem[size];
        if (size > 0) {
//            temp.add(colorCodes);
            for (int i = 0; i < size; i++) {
                sizeTemp.add(sizeDimention.get(0));
                sizeDimention.remove(0);
            }

            sizeDimention = sizeTemp;
            sizeDimention.add(new Size(sizetype));
            sizeStrings.add(new Size(sizetype,"1","6"));

            size_adap.notifyItemRangeRemoved(0, size);
        } else {
            sizeDimention.add(new Size(sizetype));
        }
//        Toast.makeText(getActivity(),String.format("Current color: 0x%08x", color),Toast.LENGTH_SHORT).show();

        size_adap = new SizeAdapter(getActivity(), sizeDimention);
        size_rec.setAdapter(size_adap);
    }

    void displayColor(int color) {
        final int size = colorCodes.size();
//        ContactItem []x = new ContactItem[size];
        if (size > 0) {
//            temp.add(colorCodes);
            for (int i = 0; i < size; i++) {
                temp.add(colorCodes.get(0));
                colorCodes.remove(0);
            }

            colorCodes = temp;
            colorCodes.add(new ColorCode(color));

            adapter.notifyItemRangeRemoved(0, size);
        } else {
            colorCodes.add(new ColorCode(color));
        }
//        Toast.makeText(getActivity(),String.format("Current color: 0x%08x", color),Toast.LENGTH_SHORT).show();

        adapter = new ColorAdapter(getActivity(), colorCodes);
        recyclerView.setAdapter(adapter);
    }

    void displayImage(Bitmap path, Uri uri) throws IOException {
        final int size = imageSources.size();
//        ContactItem []x = new ContactItem[size];
        if (size > 0) {
//            temp.add(colorCodes);
            for (int i = 0; i < size; i++) {
                imageTemp.add(imageSources.get(0));
                imageSources.remove(0);
                tempimageUri.add(imageUri.get(0));
                imageUri.remove(0);
            }

            imageSources = imageTemp;
            imageUri = tempimageUri;
            imageSources.add(path);
            imageUri.add(uri);
//            bytes.add(compress(getStringImage(path)));
//            Log.e("Compress",compress(getStringImage(path))+"");
            imageStrings.add(new ImageContainer(getStringImage(path)));

            image_adap.notifyItemRangeRemoved(0, size);
        } else {
            imageSources.add(path);
            imageStrings.add(new ImageContainer(getStringImage(path)));
            imageUri.add(uri);
        }
//        Toast.makeText(getActivity(),String.format("Current color: 0x%08x", color),Toast.LENGTH_SHORT).show();

        image_adap = new ImageAdapter(getActivity(), imageSources, imageUri);
        image_rec.setAdapter(image_adap);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 8;
//                bitmap = BitmapFactory.decodeFile(filePath.getPath(),options);


                displayImage(bitmap, filePath);
                imageStrings.add(new ImageContainer(getStringImage(bitmap)));
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION, true);
//                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compresser);
////                deflaterOutputStream.write(getStringImage(bitmap));
//                deflaterOutputStream.close();
//                byte[] output = stream.toByteArray();
//                Log.e("Compress", output.toString());
//
//                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
//                Inflater decompresser = new Inflater(true);
//                InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(stream2, decompresser);
////                inflaterOutputStream.write(output);
//                inflaterOutputStream.close();
//                byte[] output2 = stream2.toByteArray();
//                Log.e("DeCompress", output2.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static byte[] compress(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    public static String decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        gis.close();
        bis.close();
        return sb.toString();
    }
}
