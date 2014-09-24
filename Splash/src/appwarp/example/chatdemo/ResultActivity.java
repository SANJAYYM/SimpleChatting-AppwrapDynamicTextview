package appwarp.example.chatdemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

public class ResultActivity extends Activity implements ZoneRequestListener, RoomRequestListener,OnClickListener{
	
	private long time;
	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	private TextView resultTextView;
	private TextView onlineUser;
	private TextView AvailGroup;
	private Button chatButton;
	private WarpClient theClient;
	HashMap<String, Object> propertiesToMatch ;
	private String roomIdJoined = "";
	private long timeCounter = 0;
	private long startTime = 0;
	private boolean withoutStatus = false;
//	private boolean isOnJoinRoom = false;
	private String[] roomIds;
	private int roomIdCounter=0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d("on create", "Result Activity");
		setContentView(R.layout.result);
		cb1 = (CheckBox)findViewById(R.id.checkBox1);
		cb2 = (CheckBox)findViewById(R.id.checkBox2);
		cb3 = (CheckBox)findViewById(R.id.checkBox3);
		onlineUser= (TextView)findViewById(R.id.onlineuser);
		AvailGroup = (TextView)findViewById(R.id.GroupOnline);
		chatButton = (Button)findViewById(R.id.chatBtn);
		chatButton.setEnabled(false);
		resultTextView = (TextView)findViewById(R.id.resultTextView);
		withoutStatus = getIntent().getBooleanExtra("isWithout", false);
		String topic = getIntent().getStringExtra("topic").toString();
		if(propertiesToMatch==null){
			propertiesToMatch = new HashMap<String, Object>();
		}else{
			propertiesToMatch.clear();
		}
		propertiesToMatch.put("topic", topic);
		timeCounter = 0;
		roomIdCounter = 0;
		roomIds = null;
		startTime = System.currentTimeMillis();
		load(withoutStatus);
		roomIdJoined = "";
}
	
	public void onStart(){
		super.onStart();
		theClient.addZoneRequestListener(this);
		theClient.addRoomRequestListener(this);
	}
	public void OnlineUserClick(){
		onlineUser.setOnClickListener(this);
	}
	
	public void OnlineGroupClick(){
		AvailGroup.setOnClickListener(this);
		}
			
			public void onClick1(View v) {
				
				Intent intent = new Intent(ResultActivity.this, GroupChatActivity.class);
				intent.putExtra("roomId", roomIdJoined);
				AvailGroup.setTextColor(Color.BLUE);
				startActivity(intent);
				
			}
		
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(roomIdJoined.length()>0){
			theClient.leaveRoom(roomIdJoined);
			theClient.disconnect();
		}
	}
	
	public void onStop(){
		super.onStop();
		theClient.removeZoneRequestListener(this);
		theClient.removeRoomRequestListener(this);
	}
	
	public void onChatClicked(View view){
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("roomId", roomIdJoined);
		startActivity(intent);
	}
	 
	private void load(boolean isWithout){
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(isWithout){
			theClient.getAllRooms();
		}else{
			findViewById(R.id.beforeView).setVisibility(View.GONE);
			theClient.joinRoomWithProperties(propertiesToMatch);
		}
		cb1.setChecked(false);
		cb2.setChecked(false);
		cb3.setChecked(false);
		cb1.setVisibility(View.GONE);
		cb2.setVisibility(View.GONE);
		cb3.setVisibility(View.GONE);
		chatButton.setVisibility(View.GONE);
		resultTextView.setVisibility(View.GONE);
	}
	
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		HashMap roomProperties = event.getProperties();
		Log.d("roomProperties"+roomProperties, "propertiesToMatch"+propertiesToMatch);
		boolean status = hasMatchingProperties(roomProperties, propertiesToMatch);
        if(status){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					cb2.setChecked(true);
				}
			});
        	theClient.joinRoom(""+event.getData().getId());
        }else{
        	if(roomIdCounter<roomIds.length){
        		theClient.getLiveRoomInfo(roomIds[roomIdCounter]);
        		roomIdCounter++;
        	}else{
        		timeCounter = System.currentTimeMillis()-startTime;
        		runOnUiThread(new Runnable() {
    				@Override
    				public void run() {
    					resultTextView.setText("\nNo Such Room Found \nTime Taken: "+timeCounter +"\nResult code: "+event.getResult());
    				}
    			});
        		
        	}
        }
	}
	
	public boolean hasMatchingProperties(HashMap<String, Object> totalProperties, HashMap<String, Object> propertiesToMatch) {
        if(propertiesToMatch == null || totalProperties == null ){
            return false;    
        }
        for (Map.Entry<String, Object> entry : propertiesToMatch.entrySet()) { 
            String key_join = entry.getKey().toString();
            if(totalProperties.get(key_join) == null){
                return false;
            }
            if(totalProperties.get(key_join).equals(propertiesToMatch.get(key_join))){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }
	
	@Override
	public void onJoinRoomDone(final RoomEvent event) {
		timeCounter = System.currentTimeMillis()-startTime;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(withoutStatus){
					cb3.setChecked(true);
				}
				if(event.getResult()==WarpResponseResultCode.SUCCESS){
					chatButton.setEnabled(true);
					roomIdJoined = event.getData().getId();
					resultTextView.setText("\nTime Taken: "+timeCounter+"(ms)\nResult code: "+event.getResult()+" Success \nRoomID: "+roomIdJoined );
				}else{
					resultTextView.setText("\nRoom join failed \nTime Taken: "+timeCounter+"(ms)\nResult code: "+event.getResult());
				}
			}
		});
	}
	
	@Override
	public void onLeaveRoomDone(RoomEvent event ) {
		
	}
	
	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
	}
	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
	}
	@Override
	public void onCreateRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onDeleteRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onGetAllRoomsDone(AllRoomsEvent event) {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cb1.setChecked(true);
			}
		});
		
		roomIds = event.getRoomIds();
		Log.d("onGetAllRoomsDone"+roomIds, "onGetAllRoomsDone"+roomIds.length);
		if(roomIds!=null && roomIds.length>0){
			theClient.getLiveRoomInfo(roomIds[0]);
			roomIdCounter++;
		}
	}
	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
		
	}
	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent arg0) {
		
	}
	@Override
	public void onGetOnlineUsersDone(AllUsersEvent arg0) {
		
	}
	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
		
	}
	public void update(){
		timeCounter++;
	}
	@Override
	public void onLockPropertiesDone(byte arg0) {
		
	}
	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		
	}

	@Override
	public void onClick(View v) {
		onlineUser.setTextColor(Color.BLUE);
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("roomId", roomIdJoined);
		startActivity(intent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.resultscreenmenu, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Findfriendsonmap:
			 Intent intent = new Intent(this, MapActivity.class);
	            startActivity(intent) ;
			
			break;
		case R.id.Exit:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}

