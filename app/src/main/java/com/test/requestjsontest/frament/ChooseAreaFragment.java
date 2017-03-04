package com.test.requestjsontest.frament;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.test.requestjsontest.R;
import com.test.requestjsontest.activity.MainActivity;
import com.test.requestjsontest.activity.WeatherActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 瞿康宁 on 2017/2/24.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private Button setting;


    /**
     * 省列表
     */
    private List<String> provinceList;

    /**
     * 市列表
     */
    private List<String> cityList;

    /**
     * 县列表
     */
    private List<String> countyList;

    /**
     * 当前选中的省
     */
    private String selectedProvince;

    /**
     * 当前选中的市
     */
    private String selectedCity;

    /**
     * 当前选中的县
     */
    private String selectedCounty;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        setting = (Button) view.findViewById(R.id.setting);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    selectedCounty = countyList.get(position);
                    String weatherId = queryCode(selectedCounty);

                    //判断当前活动属于哪个活动，是主活动的话，就跳转，是weatherActivity的话，就刷新天气并关闭导航栏
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.swipeRefresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        queryProvince();
    }

    /**
     * 将从数据库中查询全国所有省的数据移到listView的list中
     */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = queryProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < provinceList.size(); i++) {
                dataList.add(provinceList.get(i));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
    }

    /**
     * 将从数据库中查询指定省/直辖市的市的数据移到listView的list中
     */
    private void queryCities() {
        titleText.setText(selectedProvince);
        backButton.setVisibility(View.VISIBLE);
        cityList = queryCities(selectedProvince);
        if (cityList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < cityList.size(); i++) {
                dataList.add(cityList.get(i));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
    }

    /**
     * 将从数据库中查询指定市的县的数据移到listView的list中
     */
    private void queryCounties() {
        titleText.setText(selectedCity);
        backButton.setVisibility(View.VISIBLE);
        countyList = queryCounties(selectedCity);
        if (countyList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < countyList.size(); i++) {
                dataList.add(countyList.get(i));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
    }

    /**
     * 查询全国所有的省
     * @return 包含全国所有省的ArrayList
     */
    private List<String> queryProvinces() {
        List<String> list = new ArrayList<>();
        Cursor cursor = DataSupport.findBySQL("select distinct province from jsondata");
        if (cursor.moveToFirst()) {
            do{
                String province = cursor.getString(cursor.getColumnIndex("province"));
                list.add(province);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    /**
     * 从数据库中查询指定省或直辖市的市
     * @param province 指定省或直辖市
     * @return 指定省或直辖市的市的ArrayList
     */
    private List<String> queryCities(String province) {
        List<String> list = new ArrayList<>();
        Cursor cursor = DataSupport.findBySQL("select distinct leader from jsondata where province = ?", province);
        if (cursor.moveToFirst()) {
            do {
                String city = cursor.getString(cursor.getColumnIndex("leader"));
                list.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 从数据库中查询指定市的县
     * @param city 指定的市
     * @return 指定市的县的ArrayList
     */
    private List<String> queryCounties(String city) {
        List<String> list = new ArrayList<>();
        Cursor cursor = DataSupport.findBySQL("select city from jsondata where leader = ?", city);
        if (cursor.moveToFirst()) {
            do {
                String county = cursor.getString(cursor.getColumnIndex("city"));
                list.add(county);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 根据指定市查询对应的天气代码
     * @param city 指定市或县
     * @return 对应的天气代码
     */
    public static String queryCode(String city) {
        Cursor cursor = DataSupport.findBySQL("select code from jsondata where city = ?", city);
        String code = "";
        if (cursor.moveToFirst()) {
            do {
                code = cursor.getString(cursor.getColumnIndex("code"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return code;
    }
}
