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
 * �ȸ��ͼ����������
 * ���㵼��
 * @author chunjiang.shieh
 *
 */
public class SearchMap extends MapActivity {

	private static final String TAG = SearchMap.class.getName();
	
	Button mBtnDrive = null;	// �ݳ�����
	Button mBtnTransit = null;	// ��������
	Button mBtnWalk = null;	// ��������
	
	MapView mMapView = null;	// ��ͼView
	MKSearch mSearch = null;	// ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	
	private Drawable mDrawable_Blue;
	private Drawable mDrawable_Red;
	
	
	
	/**
	 * ����·���ϵ�ÿһ����
	 */
	private  MyNaviRouteItemizedOverlay mMyNaviRouteItemizedOverlay_Blue;
	
	/**
	 * ������·�ϵ������յ�
	 * ��ҪTap����¼����������� �ɼ̳�MyNaviRouteItemizedOverlay
	 */
	private MyNaviRouteItemizedOverlay mMyNaviRouteItemizedOverlay_Red;
	/**
	 * ��������·
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
        
        // �趨������ť����Ӧ
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
		// ��ʼ������ģ�飬ע���¼�����
        mSearch = new MKSearch();
        mSearch.init(MKSearch.OUTPUT_XML, new MKSearchListener(){
			@Override
			public void onGetBicyclingRouteResult(ArrayList<Route> routes) {
				// TODO Auto-generated method stub
				Toast.makeText(SearchMap.this, "�ݲ�֧��", Toast.LENGTH_LONG).show();
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
		// ����������ť��Ӧ
		EditText editSt = (EditText)findViewById(R.id.start);
		EditText editEn = (EditText)findViewById(R.id.end);
		
		String origin = editSt.getText().toString();
		String destination = editEn.getText().toString();

		// ʵ��ʹ�����������յ���н�����ȷ���趨
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch(origin,destination,false);
		} else if (mBtnTransit.equals(v)) {
			
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch(origin, destination, false);
		}
	}


	/**
	 * ���·�ߵ���������ͼ
	 * @param route
	 */
	private void addRouteToOverlay(Route route){
		clearLatelyOvery();
		List<GeoPoint> points = route.getOverviewPolyline();
		if(points != null && points.size()>0){
			GeoPoint startPoint = points.get(0);
			GeoPoint endPoint = points.get(points.size()-1);
			/**
			 * �����յ�
			 */
			OverlayItem startItem = new OverlayItem(startPoint, 
					"start point", null);
			OverlayItem endItem = new OverlayItem(endPoint, "end point", null);
			mMyNaviRouteItemizedOverlay_Red.addItem(startItem);
			mMyNaviRouteItemizedOverlay_Red.addItem(endItem);
			mMapView.getOverlays().add(mMyNaviRouteItemizedOverlay_Red);
			
			/**
			 *����·���ϵ�ÿһ���� 
			 */
			for(int i=1;i<points.size()-1;i++){
				GeoPoint point = points.get(i);
				OverlayItem item = new OverlayItem(point, "point "+ i , null);
				mMyNaviRouteItemizedOverlay_Blue.addItem(item);
			}
			mMapView.getOverlays().add(mMyNaviRouteItemizedOverlay_Blue);
			
			/**
			 * ������
			 */
			mMyRouteOverlay = new MyRouteOverlay(points);
			mMapView.getOverlays().add(mMyRouteOverlay);
			
			/**
			 * ��λ�����
			 */
			mMapView.getController().animateTo(startPoint);
		}
	}
	
	
	/**
	 * ������һ�ε�ͼ��
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
