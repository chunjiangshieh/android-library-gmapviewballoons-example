package mapviewballoons.example.search;

import java.util.ArrayList;
import java.util.List;

import mapviewballoons.example.R;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.search.base.MyNaviRouteItemizedOverlay;
import com.readystatesoftware.search.base.MyRouteOverlay;
import com.readystatesoftware.search.base.Route;
import com.readystatesoftware.search.MKSearch;
import com.readystatesoftware.search.MKSearchListener;

/**
 * 谷歌地图的搜索服务
 * 两点导航
 * @author chunjiang.shieh
 *
 */
public class SearchMap extends MapActivity {

	private static final String TAG = SearchMap.class.getName();
	
	Button mBtnDrive = null;	// 驾车搜索
	Button mBtnTransit = null;	// 公交搜索
	Button mBtnWalk = null;	// 步行搜索
	
	MapView mMapView = null;	// 地图View
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	
	private Drawable mDrawable_Blue;
	private Drawable mDrawable_Red;
	
	
	
	/**
	 * 导航路线上的每一个点
	 */
	private  MyNaviRouteItemizedOverlay mMyNaviRouteItemizedOverlay_Blue;
	
	/**
	 * 导航线路上的起点和终点
	 * 需要Tap点击事件或其他操作 可继承MyNaviRouteItemizedOverlay
	 */
	private MyNaviRouteItemizedOverlay mMyNaviRouteItemizedOverlay_Red;
	/**
	 * 导航的线路
	 */
	private MyRouteOverlay mMyRouteOverlay;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.routeplan);
        
        
        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.setBuiltInZoomControls(true);
        
        mDrawable_Blue = getResources().getDrawable(R.drawable.marker2);
        mDrawable_Red = getResources().getDrawable(R.drawable.marker);
        
        mMyNaviRouteItemizedOverlay_Red = new MyNaviRouteItemizedOverlay(mDrawable_Red);
        
        mMyNaviRouteItemizedOverlay_Blue= new  MyNaviRouteItemizedOverlay(mDrawable_Blue);
        
        initSearch();
        
        // 设定搜索按钮的响应
        mBtnDrive = (Button)findViewById(R.id.drive);
        mBtnTransit = (Button)findViewById(R.id.transit);
        mBtnWalk = (Button)findViewById(R.id.walk);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
        };
        
        mBtnDrive.setOnClickListener(clickListener); 
        mBtnTransit.setOnClickListener(clickListener); 
        mBtnWalk.setOnClickListener(clickListener);
	}

	private void initSearch() {
		// 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(MKSearch.OUTPUT_XML, new MKSearchListener(){
			@Override
			public void onGetBicyclingRouteResult(ArrayList<Route> routes) {
				// TODO Auto-generated method stub
				Toast.makeText(SearchMap.this, "暂不支持", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onGetDrivingRouteResult(ArrayList<Route> routes) {
				// TODO Auto-generated method stub
				Log.d(TAG, "------------->onGetDrivingRouteResult");
				if(routes != null && routes.size() > 0){
					Log.d(TAG, "routes is not null");
					Route route = routes.get(0);
					addRouteToOverlay(route);
				}else{
					Log.d(TAG, "routes is  null");
				}
			}

			@Override
			public void onGetWalkingRouteResult(ArrayList<Route> routes) {
				// TODO Auto-generated method stub
				Log.d(TAG, "------------->onGetWalkingRouteResult");
				if(routes != null && routes.size() > 0){
					Log.d(TAG, "routes is not null");
					Route route = routes.get(0);
					addRouteToOverlay(route);
				}else{
					Log.d(TAG, "routes is  null");
				}
			}

        });
	}
	
	void SearchButtonProcess(View v) {
		// 处理搜索按钮响应
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);
		
		String origin = editSt.getText().toString();
		String destination = editEn.getText().toString();

		// 实际使用中请对起点终点城市进行正确的设定
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch(origin,destination,false);
		} else if (mBtnTransit.equals(v)) {
			
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch(origin, destination, false);
		}
	}


	/**
	 * 添加路线导航至覆盖图
	 * @param route
	 */
	private void addRouteToOverlay(Route route){
		clearLatelyOvery();
		List<GeoPoint> points = route.getOverviewPolyline();
		if(points != null && points.size()>0){
			GeoPoint startPoint = points.get(0);
			GeoPoint endPoint = points.get(points.size()-1);
			/**
			 * 起点和终点
			 */
			OverlayItem startItem = new OverlayItem(startPoint, 
					"start point", null);
			OverlayItem endItem = new OverlayItem(endPoint, "end point", null);
			mMyNaviRouteItemizedOverlay_Red.addItem(startItem);
			mMyNaviRouteItemizedOverlay_Red.addItem(endItem);
			mMapView.getOverlays().add(mMyNaviRouteItemizedOverlay_Red);
			
			/**
			 *导航路线上的每一个点 
			 */
			for(int i=1;i<points.size()-1;i++){
				GeoPoint point = points.get(i);
				OverlayItem item = new OverlayItem(point, "point "+ i , null);
				mMyNaviRouteItemizedOverlay_Blue.addItem(item);
			}
			mMapView.getOverlays().add(mMyNaviRouteItemizedOverlay_Blue);
			
			/**
			 * 导航线
			 */
			mMyRouteOverlay = new MyRouteOverlay(points);
			mMapView.getOverlays().add(mMyRouteOverlay);
			
			/**
			 * 定位到起点
			 */
			mMapView.getController().animateTo(startPoint);
		}
	}
	
	
	/**
	 * 清空最近一次的图层
	 */
	private void clearLatelyOvery(){
		if(mMyNaviRouteItemizedOverlay_Red != null){
			mMyNaviRouteItemizedOverlay_Red.removeAllItems();
			mMapView.getOverlays().remove(mMyNaviRouteItemizedOverlay_Red);
			
		}
		if(mMyNaviRouteItemizedOverlay_Blue != null){
			mMyNaviRouteItemizedOverlay_Blue.removeAllItems();
			mMapView.getOverlays().remove(mMyNaviRouteItemizedOverlay_Blue);
		}
		if(mMyRouteOverlay != null){
			mMapView.getOverlays().remove(mMyRouteOverlay);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
