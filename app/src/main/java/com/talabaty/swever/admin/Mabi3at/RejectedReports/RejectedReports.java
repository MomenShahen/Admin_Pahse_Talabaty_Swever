package com.talabaty.swever.admin.Mabi3at.RejectedReports;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.talabaty.swever.admin.LoginDatabae;
import com.talabaty.swever.admin.Mabi3at.DoneTalabat.Talabat;
import com.talabaty.swever.admin.Mabi3at.Mabi3atNavigator;
import com.talabaty.swever.admin.Mabi3at.Methods;
import com.talabaty.swever.admin.Mabi3at.NewTalabat.NewTalabatAdapter;
import com.talabaty.swever.admin.Mabi3at.SearchModel;
import com.talabaty.swever.admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RejectedReports extends Fragment {

    Button to_talab, from_talab;
    DatePickerDialog.OnDateSetListener DatePicker1, DatePicker2;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Talabat> talabats;
    TextView next, num, last;
    int item_num, page_num;
    int temp_first, temp_last;
//    HashMap<String, Talabat> holder_alpha, holder_date, holder_num;
    //    CheckBox order_alpha;
//    Spinner order_up, order_down;
    Button show_all;
    ImageButton search;
    Spinner client;
    ArrayList<String> EmpoyeeList, indexOfEmpoyeeList;
    Button pdf;

    LoginDatabae loginDatabae;
    Cursor cursor;
    int userid, shopid;
//    private APIService mAPIService;
    ImageButton show;
    int open_close = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rejected_report_talabat, container, false);
        to_talab = view.findViewById(R.id.to_talab);
        from_talab = view.findViewById(R.id.from_talab);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec);
        recyclerView.setLayoutManager(layoutManager);
        talabats = new ArrayList<>();
        next = view.findViewById(R.id.next);
        last = view.findViewById(R.id.previous);
        num = view.findViewById(R.id.item_num);
        item_num = page_num = 0;
        num.setText(0 + "");
//        holder_alpha = holder_num = holder_date = new HashMap<>();
//        order_alpha = view.findViewById(R.id.order_alpha);
//        order_up = view.findViewById(R.id.order_up);
//        order_down = view.findViewById(R.id.order_down);
        show_all = view.findViewById(R.id.all);
        search = view.findViewById(R.id.search);
        client = view.findViewById(R.id.client_name);
        pdf = view.findViewById(R.id.printpdf);
        show = view.findViewById(R.id.show);
        loginDatabae = new LoginDatabae(getActivity());
        cursor = loginDatabae.ShowData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ((Mabi3atNavigator) getActivity())
                .setActionBarTitle("تقارير الطلبات المرفوضه");

        temp_first = 0;
        temp_last = 10;
        while (cursor.moveToNext()) {
            userid = Integer.parseInt(cursor.getString(2));
            shopid = Integer.parseInt(cursor.getString(3));

        }

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_close++;
                final Handler handler = new Handler();
                if (open_close%2==0){
                    show.setBackgroundResource(R.drawable.ic_right);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            client.setAlpha(1);
                        }
                    }, 100);

                }else {
                    show.setBackgroundResource(R.drawable.ic_left);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            client.setAlpha(0);
                        }
                    }, 100);

                }
            }
        });

        loadEmployeeData();
        show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(shopid+"", userid+"", 0, 1);
            }
        });


        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UUID uuid = new UUID(1000, 10000000);
                String filename = "Talabaty " + uuid;
                //String filecontent = "Contenido";
                Methods fop = new Methods(getActivity());
                if (fop.writeRejectedReport(filename, talabats)) {
                    Toast.makeText(getActivity(),
                            filename + ".pdf created", Toast.LENGTH_SHORT)
                            .show();
//                        String[] mailto = {"momen.shahen2020@gmail.com"};
//                        Uri uri = Uri.parse("/sdcard/" + filename + ".pdf");
//
//                        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename + ".pdf");
//                        Uri path = Uri.fromFile(filelocation);
//
//                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                        emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
//                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Result of Exam Powered by @FCI-Learn");
//                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Thanks For Your Kinds!");
//                        emailIntent.setType("application/pdf");
//                        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
//
//                        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
//                        resultDatabase.DeleteTableAnswer();
                } else {
                    Toast.makeText(getActivity(), "I/O error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAPIService = ApiUtils.getAPIService();
                if (!to_talab.getText().toString().isEmpty() && !from_talab.getText().toString().isEmpty()) {
                    SearchModel Search = new SearchModel(shopid + "", userid + "", "0", "1");
                    loadData(Search, from_talab.getText().toString(), to_talab.getText().toString());
                } else {
                    SearchModel Search = new SearchModel(indexOfEmpoyeeList.get(EmpoyeeList.indexOf(client.getSelectedItem().toString())), "3", "5", "0", "1");
                    loadData(Search);
//                    APIService apiService = ApiUtils.getAPIService();
//                    Call<SearchModel> call = apiService.Search(Search);
//                    call.enqueue(new Callback<SearchModel>() {
//                        @Override
//                        public void onResponse(Call<SearchModel> call, retrofit2.Response<SearchModel> response) {
//                            if(response.isSuccessful()) {
////                    response.body().toString());
//                                Log.e("post submitted " , response.body().toString());
//                                Toast.makeText(getActivity(),response.body().toString(),Toast.LENGTH_SHORT).show();
//                            }else {
//                                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<SearchModel> call, Throwable t) {
////                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
//                            call.cancel();
//                        }
//                    });
//                    loadData(indexOfEmpoyeeList.get(EmpoyeeList.indexOf(client.getSelectedItem().toString())),"0");
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (talabats.size() == 10) {
                    loadData(shopid + "", userid + "", item_num, 1);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "نهايه التقارير", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
            }
        });

        /** Order Code*/
/*
        order_alpha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    orderDate("alpha");
                }
            }
        });

        order_up.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_alpha.setChecked(false);
                if (order_up.getSelectedItem().toString().equals("الرقم")) {
                    orderDate("up_num");
                } else {
                    orderDate("up_date");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        order_down.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_alpha.setChecked(false);
                if (order_down.getSelectedItem().toString().equals("الرقم")) {
                    orderDate("down_num");
                } else {
                    orderDate("down_date");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page_num > 1) {
                    loadData(shopid + "", userid + "", item_num, 0);
                } else {
                    Snackbar.make(v, "بدايه الطلبات", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        loadData(shopid + "", userid + "", 0, 1);


        from_talab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , DatePicker2
                        , year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }

        });
        DatePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                from_talab.setText(year + "/" + month + "/" + dayOfMonth);
            }
        };

        to_talab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , DatePicker1
                        , year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }

        });
        DatePicker1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                to_talab.setText(year + "/" + month + "/" + dayOfMonth);
                Log.e("Date", dayOfMonth + "-" + month + "-" + year);
            }
        };


    }

    private void loadEmployeeData() {

        EmpoyeeList = new ArrayList<>();
        indexOfEmpoyeeList = new ArrayList<>();

        if (EmpoyeeList.size() > 0) {
            for (int x = 0; x < EmpoyeeList.size(); x++) {
                EmpoyeeList.remove(x);
                indexOfEmpoyeeList.remove(x);
            }
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.sellsapi.rivile.com/order/SelectCustomers?ShopId="+shopid+"&token=bKPNOJrob8x", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray jsonArray = jsonObject.getJSONArray("Customers");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String fname = jsonObject1.getString("CustomerName");
                        String id = jsonObject1.getString("CustomerId");
                        EmpoyeeList.add(fname);
                        indexOfEmpoyeeList.add(id);

                    }

                    client.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, EmpoyeeList));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_warning,
                        (ViewGroup) getActivity().findViewById(R.id.lay));

                TextView text = (TextView) layout.findViewById(R.id.txt);

                if (error instanceof ServerError)
                    text.setText("خطأ فى الاتصال بالخادم");
                else if (error instanceof TimeoutError)
                    text.setText("خطأ فى مدة الاتصال");
                else if (error instanceof NetworkError)
                    text.setText("شبكه الانترنت ضعيفه حاليا");

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap = new HashMap();
                hashMap.put("token", "bKPNOJrob8x");
                return hashMap;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

//    private void orderDate(String type) {
//
//        // Remove All Previous Data
//        final int size = talabats.size();
//        if (size > 0) {
//            for (int i = 0; i < size; i++) {
//                talabats.remove(0);
//            }
//            adapter.notifyItemRangeRemoved(0, size);
//        }
//
//        if (type.equals("alpha")) {
//            List<String> name = new ArrayList<>();
//            Iterator myVeryOwnIterator = holder_alpha.keySet().iterator();
//            while (myVeryOwnIterator.hasNext()) {
//                String key = (String) myVeryOwnIterator.next();
//                name.add(holder_alpha.get(key).getName());
//            }
//
//            Collator collator = Collator.getInstance(new Locale("ar"));
//            Collections.sort(name, collator);
//
//            // Fill New Data
//            for (int x = 0; x < name.size(); x++) {
//                talabats.add(holder_alpha.get(name.get(x)));
//            }
//
//
//        } else if (type.equals("up_num")) {
//            List<String> num = new ArrayList<>();
//            Iterator myVeryOwnIterator = holder_num.keySet().iterator();
//            while (myVeryOwnIterator.hasNext()) {
//                String key = (String) myVeryOwnIterator.next();
//                num.add(holder_alpha.get(key).getNum());
//            }
//
//            Collections.sort(num);
//
//            // Fill New Data
//            for (int x = 0; x < num.size(); x++) {
//                talabats.add(holder_num.get(num.get(x)));
//            }
//
//        } else if (type.equals("up_date")) {
//            List<String> datetime = new ArrayList<>();
//            Iterator myVeryOwnIterator = holder_date.keySet().iterator();
//            while (myVeryOwnIterator.hasNext()) {
//                String key = (String) myVeryOwnIterator.next();
//                datetime.add(holder_date.get(key).getName());
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
//            Collections.sort(datetime);
//
//            // Fill New Data
//            for (int x = 0; x < datetime.size(); x++) {
//                talabats.add(holder_date.get(datetime.get(x)));
//            }
//
//
//        } else if (type.equals("down_num")) {
//
//            List<String> num = new ArrayList<>();
//            Iterator myVeryOwnIterator = holder_num.keySet().iterator();
//            while (myVeryOwnIterator.hasNext()) {
//                String key = (String) myVeryOwnIterator.next();
//                num.add(holder_alpha.get(key).getNum());
//            }
//
//            Collections.sort(num);
//
//            // Fill New Data
//            for (int x = num.size() - 1; x >= 0; x--) {
//                talabats.add(holder_num.get(num.get(x)));
//            }
//
//        } else if (type.equals("down_date")) {
//            List<String> datetime = new ArrayList<>();
//            Iterator myVeryOwnIterator = holder_date.keySet().iterator();
//            while (myVeryOwnIterator.hasNext()) {
//                String key = (String) myVeryOwnIterator.next();
//                datetime.add(holder_date.get(key).getName());
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
//            Collections.sort(datetime);
//
//            // Fill New Data
//            for (int x = datetime.size() - 1; x >= 0; x--) {
//                talabats.add(holder_date.get(datetime.get(x)));
//            }
//        }
//
//        adapter = new NewTalabatAdapter(getActivity(), talabats, temp_first, temp_last);
//        recyclerView.setAdapter(adapter);
//
//    }

    private void loadData(final String ShopId, final String UserId, final int x, final int type) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل البيانات ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.sellsapi.rivile.com/order/RefusedReportList",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {

                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("RefusedReport");
                            if (array.length() > 0) {
//                                final int size = talabats.size();
//                                if (size > 0) {
//                                    for (int i = 0; i < size; i++) {
//                                        talabats.remove(0);
//                                    }
//                                    adapter.notifyItemRangeRemoved(0, size);
//                                }
                                talabats = new ArrayList<>();
                                temp_first = Integer.parseInt(array.getJSONObject(0).getString("Id"));
                                temp_last = Integer.parseInt(array.getJSONObject(array.length() - 1).getString("Id"));
                                for (int x = 0; x < array.length(); x++) {
                                    JSONObject object1 = array.getJSONObject(x);
//                                if (x == 0) {
//                                    temp_first = Integer.parseInt(object1.getString("Id"));
//                                } else if (x == array.length() - 1) {
//                                    temp_last = Integer.parseInt(object1.getString("Id"));
//                                }
                                    Talabat talabat = new Talabat
                                            ((x + 1) + "",
                                                    object1.getString("Id"),
                                                    object1.getString("CustomerName"),
                                                    object1.getString("Total"),
                                                    object1.getString("RefuseReson"),
                                                    object1.getString("Time"),
                                                    object1.getString("Date")
                                            );

                                    // Fill Data For Sort in orderDate()
//                                    holder_num.put(object1.getString("Id"), talabat);
//                                    holder_alpha.put(object1.getString("CustomerName"), talabat);
//                                    holder_date.put(object1.getString("Date") + " " + object1.getString("Time"), talabat);

                                    talabats.add(talabat);
                                }
                                if (type == 1) {
                                    page_num++;
                                } else if (type == 0) {
                                    page_num--;
                                } else {

                                }
                                num.setText(String.valueOf(page_num));
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_info,
                                        (ViewGroup) getActivity().findViewById(R.id.lay));

                                TextView text = (TextView) layout.findViewById(R.id.txt);
                                text.setText("لا توجد بيانات");

                                Toast toast = new Toast(getActivity());
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter = new RejectedReportsTalabatAdapter(getActivity(), talabats);
                        recyclerView.setAdapter(adapter);
                        item_num = temp_last;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_warning,
                        (ViewGroup) getActivity().findViewById(R.id.lay));

                TextView text = (TextView) layout.findViewById(R.id.txt);

                if (error instanceof ServerError)
                    text.setText("خطأ فى الاتصال بالخادم");
                else if (error instanceof TimeoutError)
                    text.setText("خطأ فى مدة الاتصال");
                else if (error instanceof NetworkError)
                    text.setText("شبكه الانترنت ضعيفه حاليا");

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("ShopId", ShopId);
                hashMap.put("UserId", UserId);
                hashMap.put("x", x + "");
                hashMap.put("type", type + "");
                hashMap.put("token", "bKPNOJrob8x");
                return hashMap;
            }
        };
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                2,  // maxNumRetries = 2 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadData(final SearchModel Search, final String from, final String to) {

        Gson gson = new Gson();
//        SearchModel model = new SearchModel("8","3","5",0);

        final String jsonInString = gson.toJson(Search);
        Log.e("Data", jsonInString);
        Log.e("From", from);
        Log.e("To", to);


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل البيانات ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.sellsapi.rivile.com/order/RefusedReport",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int temp = 0;
                        progressDialog.dismiss();
                        try {

                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("RefusedReport");
                            if (array.length() > 0) {
                                final int size = talabats.size();
                                if (size > 0) {
                                    for (int i = 0; i < size; i++) {
                                        talabats.remove(0);
                                    }
                                    adapter.notifyItemRangeRemoved(0, size);
                                }
                                for (int x = 0; x < array.length(); x++) {
                                    JSONObject object1 = array.getJSONObject(x);
                                    if (x == 0) {
                                        temp_first = Integer.parseInt(object1.getString("Id"));
                                    } else if (x == array.length() - 1) {
                                        temp_last = Integer.parseInt(object1.getString("Id"));
                                    }
                                    Talabat talabat = new Talabat
                                            ((x + 1) + "",
                                                    object1.getString("Id"),
                                                    object1.getString("CustomerName"),
                                                    object1.getString("Total"),
                                                    object1.getString("RefuseReson"),
                                                    object1.getString("Time"),
                                                    object1.getString("Date")
                                            );

                                    // Fill Data For Sort in orderDate()
//                                    holder_num.put(object1.getString("Id"), talabat);
//                                    holder_alpha.put(object1.getString("CustomerName"), talabat);
//                                    holder_date.put(object1.getString("Date") + " " + object1.getString("Time"), talabat);

                                    talabats.add(talabat);
                                    temp = Integer.parseInt(object1.getString("Id"));
                                }
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_info,
                                        (ViewGroup) getActivity().findViewById(R.id.lay));

                                TextView text = (TextView) layout.findViewById(R.id.txt);
                                text.setText("لا توجد بيانات");

                                Toast toast = new Toast(getActivity());
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter = new RejectedReportsTalabatAdapter(getActivity(), talabats);
                        recyclerView.setAdapter(adapter);
                        item_num = temp;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_warning,
                        (ViewGroup) getActivity().findViewById(R.id.lay));

                TextView text = (TextView) layout.findViewById(R.id.txt);

                if (error instanceof ServerError)
                    text.setText("خطأ فى الاتصال بالخادم");
                else if (error instanceof TimeoutError)
                    text.setText("خطأ فى مدة الاتصال");
                else if (error instanceof NetworkError)
                    text.setText("شبكه الانترنت ضعيفه حاليا");

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("Search", jsonInString);
                hashMap.put("From", from);
                hashMap.put("To", to);
                hashMap.put("token", "bKPNOJrob8x");
                return hashMap;
            }
        };
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                2,  // maxNumRetries = 2 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void loadData(final SearchModel Search) {

        Gson gson = new Gson();
//        SearchModel model = new SearchModel("8","3","5",0);

        final String jsonInString = gson.toJson(Search);
        Log.e("Data", jsonInString);


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جارى تحميل البيانات ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.sellsapi.rivile.com/order/RefusedReport",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int temp = 0;
                        progressDialog.dismiss();
                        try {

                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("RefusedReport");
                            if (array.length() > 0) {
                                final int size = talabats.size();
                                if (size > 0) {
                                    for (int i = 0; i < size; i++) {
                                        talabats.remove(0);
                                    }
                                    adapter.notifyItemRangeRemoved(0, size);
                                }
                                for (int x = 0; x < array.length(); x++) {
                                    JSONObject object1 = array.getJSONObject(x);
                                    if (x == 0) {
                                        temp_first = Integer.parseInt(object1.getString("Id"));
                                    } else if (x == array.length() - 1) {
                                        temp_last = Integer.parseInt(object1.getString("Id"));
                                    }
                                    Talabat talabat = new Talabat
                                            ((x + 1) + "",
                                                    object1.getString("Id"),
                                                    object1.getString("CustomerName"),
                                                    object1.getString("Total"),
                                                    object1.getString("RefuseReson"),
                                                    object1.getString("Time"),
                                                    object1.getString("Date")
                                            );

                                    // Fill Data For Sort in orderDate()
//                                    holder_num.put(object1.getString("Id"), talabat);
//                                    holder_alpha.put(object1.getString("CustomerName"), talabat);
//                                    holder_date.put(object1.getString("Date") + " " + object1.getString("Time"), talabat);

                                    talabats.add(talabat);
                                    temp = Integer.parseInt(object1.getString("Id"));
                                }
                            } else {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast_info,
                                        (ViewGroup) getActivity().findViewById(R.id.lay));

                                TextView text = (TextView) layout.findViewById(R.id.txt);
                                text.setText("لا توجد بيانات");

                                Toast toast = new Toast(getActivity());
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter = new RejectedReportsTalabatAdapter(getActivity(), talabats);
                        recyclerView.setAdapter(adapter);
                        item_num = temp;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_warning,
                        (ViewGroup) getActivity().findViewById(R.id.lay));

                TextView text = (TextView) layout.findViewById(R.id.txt);

                if (error instanceof ServerError)
                    text.setText("خطأ فى الاتصال بالخادم");
                else if (error instanceof TimeoutError)
                    text.setText("خطأ فى مدة الاتصال");
                else if (error instanceof NetworkError)
                    text.setText("شبكه الانترنت ضعيفه حاليا");

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("Search", jsonInString);
                hashMap.put("token", "bKPNOJrob8x");
                return hashMap;
            }
        };
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                2,  // maxNumRetries = 2 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public void Search(SearchModel title, String x) {


//        mAPIService.Search(title).enqueue(new Callback<SearchModel>() {
//            @Override
//            public void onResponse(Call<SearchModel> call, retrofit2.Response<SearchModel> response) {
//                Log.e("post submitted " , response.body().toString());
//                if(response.isSuccessful()) {
////                    response.body().toString());
//                    Log.e("post submitted " , response.body().toString());
//                    Toast.makeText(getActivity(),response.body().toString(),Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SearchModel> call, Throwable t) {
//                Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
