package appwarp.example.chatdemo;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class GroupChatActivity extends Activity implements RoomRequestListener, NotifyListener{
	
	private ProgressDialog progressDialog;
	private WarpClient theClient;
	private TextView outputView;
	private EditText inputEditText;
	private ScrollView outputScrollView;
	public Button sendtBtn;
	public Button BackButton;
	private Spinner onlineUsers;
	private TextView outputView1;
	private TextView online_user;
	private String roomId;
	final TextView Username[] = null;
	final TextView Message[] = null;
	private ArrayList<String> onlineUserList = new ArrayList<String>();
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_chat);
//		getActionBar().setDisplayShowHomeEnabled(true);
//		 getActionBar().setHomeButtonEnabled(true);
//		 getActionBar().setDisplayHomeAsUpEnabled(true); 
//		outputView1= (TextView)findViewById(R.id.outputTextView1);
//		outputView = (TextView)findViewById(R.id.outputTextView);
		online_user = (TextView)findViewById(R.id.online_user_name);
		inputEditText = (EditText)findViewById(R.id.inputEditText);
		sendtBtn = (Button)findViewById(R.id.sendBtn);
		BackButton = (Button)findViewById(R.id.Back_Chat);
		outputScrollView = (ScrollView)findViewById(R.id.outputScrollView);
		onlineUsers = (Spinner)findViewById(R.id.onlineUserSpinner);
		onlineUsers.setVisibility(View.GONE);
		roomId = "";
		roomId = getIntent().getStringExtra("roomId");
		
		
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		theClient.addRoomRequestListener(this);
		theClient.subscribeRoom(roomId);
		theClient.addNotificationListener(this);
		theClient.getLiveRoomInfo(roomId);
		progressDialog = ProgressDialog.show(this, "", "Please wait..");
	}
	
	public void onDestroy(){
		super.onDestroy();
		if(theClient!=null){
			theClient.removeRoomRequestListener(this);
			theClient.unsubscribeRoom(roomId);
			theClient.removeNotificationListener(this);
		}
	}

	public void onSendClicked(View view){
		outputScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		theClient.sendChat(inputEditText.getText().toString());
		inputEditText.setText("");
	}
	
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		progressDialog.dismiss();
		if(event.getResult()==0){
			if(event.getJoinedUsers().length>1){// if more than one user is online
				final String onlineUser[] = Utils.removeUsernameFromArray(event.getJoinedUsers());
				for(int i=0;i<onlineUser.length;i++){
					onlineUserList.add(onlineUser[i].toString());
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fillDataInSpinner(event.getData().getName());
					}
				});
			}else{ // Alert for no online user found
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToast(GroupChatActivity.this, "No online user found");
					}
				});
				Log.d("No online user found", "No online user found");
			}
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToast(GroupChatActivity.this, "Error in fetching data. Please try later");
				}
			});
			
		}
	}
	
	
	private void fillDataInSpinner(String name){
		if(name!=null && name.length()>0){
			onlineUsers.setPrompt(name);// room name
			onlineUsers.setVisibility(View.GONE);
		}
		String onlineUserArray[] = new String[onlineUserList.size()];
		for(int i=0;i<onlineUserArray.length;i++){
			onlineUserArray[i] = onlineUserList.get(i).toString();
			String username = onlineUserList.get(i).toString();
			online_user.setText("\t\t\t\t\t\t"+"Graduates");
			//online_user.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_spinner_item, onlineUserArray);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    onlineUsers.setAdapter(adapter);
	}
	
	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private TextView createNewTextView1(String text) {
		final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lparams.gravity = Gravity.RIGHT;
		final TextView textView = new TextView(this);
	    textView.setLayoutParams(lparams);	    
	    textView.setBackgroundResource(R.drawable.bubble_green);
	    textView.setTextColor(Color.BLACK);
	    textView.setText(text);
	    return textView;
	}
	private TextView createNewTextView(String text) {
	    final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	    lparams.gravity = Gravity.LEFT;
	    final TextView textView = new TextView(this);
	    textView.setLayoutParams(lparams);
	    textView.setBackgroundResource(R.drawable.bubble_yellow);
	    textView.setTextColor(Color.BLACK);
	    textView.setText(text);
	    return textView;
	}
	
	
	@Override
	public void onChatReceived(final ChatEvent event) {
			final LinearLayout rel;
			final ScrollView scr;
			scr = (ScrollView) findViewById(R.id.outputScrollView);
			scr.fullScroll(View.FOCUS_DOWN);
		rel = (LinearLayout) findViewById(R.id.ScrollRelative);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String userName = event.getSender();
				if(userName.equalsIgnoreCase("ashok")){
					rel.addView(createNewTextView1("\n"+event.getSender()+": "+event.getMessage()));
					
				}else{				
					rel.addView(createNewTextView("\n"+event.getSender()+": "+event.getMessage()));
				}
			}
		});
	}
	
	@Override
	public void onPrivateChatReceived(final String userName, final String message) {
		
	}
	@Override
	public void onRoomCreated(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRoomDestroyed(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserJoinedRoom(final RoomData roomData, final String userName) {
		if(userName.equals(Utils.USER_NAME)==false){
			onlineUserList.add(userName);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					fillDataInSpinner(null);
				}
			});
		}
	}
	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserLeftRoom(final RoomData roomData, final String userName) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onlineUserList.remove(userName);
				fillDataInSpinner(null);
			}
		});
		
	}
	
	@Override
	public void onMoveCompleted(MoveEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGameStopped(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}	
}
