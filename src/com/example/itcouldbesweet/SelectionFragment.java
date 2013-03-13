package com.example.itcouldbesweet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class SelectionFragment extends Fragment {
	
	private static final String TAG = "SelectionFragment";
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private UiLifecycleHelper uiHelper;
	private Button sButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
		
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.selection, container, false);

	    	    
		 // Find the user's profile picture custom view
		 profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		 //profilePictureView.setCropped(true);
	
		 // Find the user's name view
		 userNameView = (TextView) view.findViewById(R.id.user_name);
	    
		// Check for an open session
		    Session session = Session.getActiveSession();
		    if (session != null && session.isOpened()) {
		        // Get the user's data
		        makeMeRequest(session);
		    }
		    
		    //Creation du bouton de suggestion d'amis
		    sButton = (Button) view.findViewById(R.id.sugger_Button);
		    
		    sButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String fqlQuery = "SELECT uid, name FROM user WHERE uid in ( SELECT uid2 FROM friend WHERE uid1 = me())";
					Bundle params = new Bundle();
					params.putString("q", fqlQuery);
					Session session = Session.getActiveSession();
					Request request = new Request(session, "fql", params, HttpMethod.GET, new Request.Callback() {						
						@Override
						public void onCompleted(Response response) {
							Log.i(TAG, "Result : " + response.toString());
							
						}
					});
					Request.executeBatchAsync(request);
				}
				Intent intent = new Intent (this, liste.class);
			});
		    
	    return view;    
	    
	}
	
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                    // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(user.getFirstName());
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	} 
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);	        
	    }
	    if (state.isOpened()){
	    	sButton.setVisibility(View.VISIBLE);
	    }
	    else{
	    	sButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }	     
	    
	}
	@Override
    public void onResume(){
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();        
        Session.getActiveSession().close();
        uiHelper.onDestroy();
    }

}
