package com.example.team_project.MainFragment;

import static com.example.team_project.Retrofit.RetrofitClient.KEY;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.team_project.Adapter.FavAdapter;
import com.example.team_project.Adapter.LatestAdapter;
import com.example.team_project.Adapter.RowData;
import com.example.team_project.Event.LinePagerIndicatorDecoration;
import com.example.team_project.Event.SnapPagerScrollListener;
import com.example.team_project.LatestRowListActivity;
import com.example.team_project.R;
import com.example.team_project.Retrofit.RetrofitClient;
import com.example.team_project.RowDetailActivity;
import com.example.team_project.RowProcessActivity;
import com.example.team_project.API.SharedPreference;
import com.example.team_project.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private final String TAG=this.getClass().getSimpleName();
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView_latest;
    private RecyclerView recyclerView_fav;
    private LatestAdapter latestAdapter;
    private FavAdapter favAdapter;
    private ArrayList<RowData> rowList=new ArrayList<>();
    private ArrayList<RowData> favList=new ArrayList<>();
    private JSONArray row;
    private int i=0;
    public HomeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        recyclerView_latest=binding.rvLatest;
        recyclerView_fav=binding.rvFav;
        getData();

        binding.btnDetail.setOnClickListener(this);

        binding.step1.setOnClickListener(view -> {
            searchNaverNews("??????");
        });
        binding.step2.setOnClickListener(view -> {
            searchNaverNews("????????????");
        });
        binding.step3.setOnClickListener(view -> {
            searchNaverNews("????????? ??????");
        });
        binding.step4.setOnClickListener(view -> {
            searchNaverNews("????????? ??????");
        });
        binding.step5.setOnClickListener(view -> {
            searchNaverNews("??????");
        });
        // ????????? ?????? -> ????????????

        return binding.getRoot();
    }



    private void showNotification(int step,String title) {
        createNotificationChannel();
        String status="";
        int icon_image=0;
        switch (step){
            case 2:
                status="[???????????? ?????? ???]";
                icon_image=R.drawable.icon_step2;
                break;
            case 3:
                status="[?????????/?????????????????? ?????? ???]";
                icon_image=R.drawable.icon_step3;
                break;
            case 4:
                status="[?????? ????????? ????????? ??????]";
                icon_image=R.drawable.icon_step4;
                break;
            case 5:
                Log.d(TAG, "showNotification: 5");
                status="[????????? ????????????]";
                icon_image=R.drawable.icon_step5;
                break;
        }
        title="<b>"+title+"</b>";
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT); //PendingIntent.FLAG_MUTABLE or FLAG_IMMUTABLE
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), i+"")
                .setSmallIcon(icon_image) //????????? ?????? ?????????. ???????????? ??? ??? ?????? ????????? ?????? ??????????????????.
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), icon_image))
                //.setContentTitle(title) // ????????? ??????
                .setContentTitle(Html.fromHtml(title)) // ????????? ??????
                .setContentText(status) //?????? ?????????
                .setAutoCancel(true) //???????????? ????????? ????????? ???????????? ????????? ???????????????.
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getContext().getResources(), icon_image)))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH); //?????? ????????????.Android 8.0 ????????? ?????? ?????? ????????? ????????? ?????? ???????????? ?????? ???????????? ?????????

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(0,builder.build());
        i++;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because // ?????? ????????? ??????????????? API 26+????????? ????????? ????????? ??? ????????????.
        // the NotificationChannel class is new and not in the support library // Notification Channel ???????????? ??? ???????????? ?????? ?????????????????? ??????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="CHANNEL"; //?????????
            String description = "description"; //?????? ??????
            int importance = NotificationManager.IMPORTANCE_HIGH; //?????????
            NotificationChannel channel = new NotificationChannel(i+"", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance // ????????? ???????????? ???????????????. ???????????? ????????? ??? ????????????.
            // or other notification behaviors after this // ?????? ??? ????????? ?????? ?????? ??????
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void getFavData() {
        favList=SharedPreference.getInstance(getContext()).getFavList();
        if(favList.size()==0){
            binding.text.setVisibility(View.VISIBLE);
            recyclerView_fav.setVisibility(View.GONE);
        }else {
            binding.text.setVisibility(View.GONE);
            recyclerView_fav.setVisibility(View.VISIBLE);
        }
        favAdapter=new FavAdapter(favList);
        recyclerView_fav.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false));

        favAdapter.setOnItemClickListener((v, pos) -> {
            startActivity(new Intent(getActivity(),RowDetailActivity.class).putExtra("row",favList.get(pos)));
            Log.d(TAG, "???????????? ?????? onResponse: row = " + rowList.get(pos) );
        });

        recyclerView_fav.setAdapter(favAdapter);
        //setRecyclerViewEvent(recyclerView_fav);
        showNotification(2,favList.get(0).getBill_name());
    }


    private void getData() {
        RetrofitClient.getRetrofitInterface().get_???????????????(KEY,"JSON","1","130","21")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "onResponse: "+response.body());
                        try {
                            JSONObject jsonObject=new JSONObject(response.body());
                            JSONArray jsonArray=new JSONArray(jsonObject.getString("nzmimeepazxkubdpn"));

                            JSONArray head=new JSONArray(jsonObject.getString("nzmimeepazxkubdpn")).getJSONObject(0).getJSONArray("head");
                            row=new JSONArray(jsonObject.getString("nzmimeepazxkubdpn")).getJSONObject(1).getJSONArray("row");
                            Log.d(TAG, "row: "+row);

                            // index = 0
                            rowList.add(new RowData("2118059", "????????????????????? ?????????????????????", "??????????????? ??? 10???", "2022-11-02",
                                    null,null, "http://likms.assembly.go.kr/bill/billDetail.do?billId=PRC_Q2U2A0V6X1V3Q0D9K2W6W2N3R7J1V0&ageFrom=21&ageTo=21", 0));
                            // index = 1
                            rowList.add(new RowData("2118058", "????????? ?????????????????????", "??????????????? ??? 11???", "2022-11-02",
                                    null,null, "http:\\/\\/likms.assembly.go.kr\\/bill\\/billDetail.do?billId=PRC_N2O2P1E1L0G2I1G7N1C5E0C8A4U0S3&ageFrom=21&ageTo=21", 0));
                            // index = 2
                            rowList.add(new RowData("2118057", "????????????????????? ?????????????????????", "???????????????15???", "2022-11-02",
                                    null,null, "http:\\/\\/likms.assembly.go.kr\\/bill\\/billDetail.do?billId=PRC_E2X2B1P0M2E8J1B4G0Y5N3G2W0A6H6&ageFrom=21&ageTo=21", 0));
//                            for(int i=0;i<3;i++){
//
//                                // ????????? ????????? ????????????
//                                // row list ??? ???????????? ??????.
//
//
//                                if (!"null".equals(row.getJSONObject(i).getString("COMMITTEE"))){
//                                    rowList.add(new RowData(
//                                                    row.getJSONObject(i).getString("BILL_NO"),
//                                                    row.getJSONObject(i).getString("BILL_NAME"),
//                                                    row.getJSONObject(i).getString("PROPOSER"),
//                                                    row.getJSONObject(i).getString("PROPOSE_DT"),
//                                                    row.getJSONObject(i).getString("COMMITTEE_ID"),
//                                                    row.getJSONObject(i).getString("COMMITTEE"),
//                                                    row.getJSONObject(i).getString("DETAIL_LINK")
//                                            ));
//                                }else {
//                                    rowList.add(new RowData(
//                                            row.getJSONObject(i).getString("BILL_NO"),
//                                            row.getJSONObject(i).getString("BILL_NAME"),
//                                            row.getJSONObject(i).getString("PROPOSER"),
//                                            row.getJSONObject(i).getString("PROPOSE_DT"),
//                                            "-",
//                                            "-",
//                                            row.getJSONObject(i).getString("DETAIL_LINK")
//                                    ));
//                                }
//
//                            }

                            latestAdapter=new LatestAdapter(rowList);
                            recyclerView_latest.setLayoutManager(new LinearLayoutManager(
                                    getActivity(), LinearLayoutManager.HORIZONTAL, false));

                            // ?????? ????????? ????????? ????????? ????????? ??????????????? ??????
                            latestAdapter.setOnItemClickListener((v, pos) -> {
                                startActivity(new Intent(getActivity(),RowDetailActivity.class).putExtra("row",rowList.get(pos)));
                                Log.d(TAG, "???????????? ?????? onResponse: row = " + rowList.get(pos) );
                            });

                            recyclerView_latest.setAdapter(latestAdapter);
                            setRecyclerViewEvent(recyclerView_latest);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
    }
    PagerSnapHelper snapHelper;
    private void setRecyclerViewEvent(RecyclerView recyclerView) {
        if(snapHelper==null){
            Log.d(TAG, "setRecyclerViewEvent: "+snapHelper);
            snapHelper = new PagerSnapHelper();
            recyclerView.addItemDecoration(new LinePagerIndicatorDecoration());
            snapHelper.attachToRecyclerView(recyclerView);
            SnapPagerScrollListener listener = new SnapPagerScrollListener(
                    new PagerSnapHelper(),
                    SnapPagerScrollListener.ON_SCROLL,
                    true,
                    new SnapPagerScrollListener.OnChangeListener() {
                        @Override
                        public void onSnapped(int position) {
                            //position ????????? ????????? ??????
                            Log.d(TAG, "onSnapped: "+position);
                        }
                    }
            );
            recyclerView.addOnScrollListener(listener);
        }
    }

    // ?????? ?????? ???????????? ??? ?????? ???????????? ????????? || ???????????? ???????????? ??????
    private void searchNaverNews(String keyword) {
        Log.d(TAG, "searchNaverNews: "+keyword);
        startActivity(new Intent(getActivity(), RowProcessActivity.class).putExtra("keyword",keyword));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_detail:
                startActivity(new Intent(getActivity(), LatestRowListActivity.class).putExtra("row", String.valueOf(row)));
                break;
        }
    }
    @Override
    public void onResume() { Log.d(TAG, "onResume: ");
        super.onResume();
        getFavData();
    }

    @Override
    public void onStart() { Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onDestroyView() { Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }


    @Override
    public void onDestroy() { Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}