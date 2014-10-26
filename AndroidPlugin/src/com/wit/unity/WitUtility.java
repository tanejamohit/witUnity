package com.wit.unity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class WitUtility extends UnityPlayerActivity {
	protected static final int RESULT_SPEECH = 1;
	public static String _accessToken;
	SpeechRecognizer _speechRecognizer;
	public static String _gameObjectName;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	protected void onCreate(Bundle savedInstanceState) {

	    // call UnityPlayerActivity.onCreate()
	    super.onCreate(savedInstanceState);

	    // print debug message to logcat
	    Log.d("WitUnity", "onCreate called!");
	  }

	public static void setGameObjectName(String gameObjectName)
	{
		_gameObjectName = gameObjectName;
	}
	
	public static void setAccessToken(String accessToken)
	{
	    _accessToken = accessToken;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	/**
     * Running the recognition process. Checks availability of recognition Activity,
     * If Activity is absent, send user to Google Play to install Google Voice Search.
    * If Activity is available, send Intent for running.
     *
     * @param callingActivity = Activity, that initializing recognition process
     */
    public void startListening() {
    	Activity callingActivity = UnityPlayer.currentActivity;
        // check if there is recognition Activity
        if (isSpeechRecognitionActivityPresented(callingActivity) == true) {
            // if yes – running recognition
            startRecognition();
        } else {
            // if no, then showing notification to install Voice Search
            Toast.makeText(callingActivity, "In order to activate speech recognition you must install 'Google Voice Search'", Toast.LENGTH_LONG).show();
            // start installing process
            installGoogleVoiceSearch(callingActivity);
        }
    }
	
    /**
     * Checks availability of speech recognizing Activity
     *
     * @param callerActivity – Activity that called the checking
     * @return true – if Activity there available, false – if Activity is absent
     */
    private static boolean isSpeechRecognitionActivityPresented(Activity callerActivity) {
        try {
            // getting an instance of package manager
            PackageManager pm = callerActivity.getPackageManager();
            // a list of activities, which can process speech recognition Intent
            List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (activities.size() != 0) {    // if list not empty
                return true;                // then we can recognize the speech
            }
        } catch (Exception e) {

        }

        return false; // we have no activities to recognize the speech
    }
    
    /**
     * Asking the permission for installing Google Voice Search. 
     * If permission granted – sent user to Google Play
     * @param callerActivity – Activity, that initialized installing
     */
    private static void installGoogleVoiceSearch(final Activity ownerActivity) {

        // creating a dialog asking user if he want
        // to install the Voice Search
        Dialog dialog = new AlertDialog.Builder(ownerActivity)
            .setMessage("For recognition it’s necessary to install 'Google Voice Search'")    // dialog message
            .setTitle("Install Voice Search from Google Play?")    // dialog header
            .setPositiveButton("Install", new DialogInterface.OnClickListener() {    // confirm button

                // Install Button click handler
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        // creating an Intent for opening applications page in Google Play
                        // Voice Search package name: com.google.android.voicesearch
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.voicesearch"));
                        // setting flags to avoid going in application history (Activity call stack)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        // sending an Intent
                        ownerActivity.startActivity(intent);
                     } catch (Exception ex) {
                         // if something going wrong
                         // doing nothing
                     }
                }})

            .setNegativeButton("Cancel", null)    // cancel button
            .create();

        dialog.show();    // showing dialog
    }
    
	public void startRecognition()
	{
		Log.d("WitUnity", "startRecording Called");
	    final Intent recIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
	    recIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
	    recIntent.putExtra("calling_package", UnityPlayer.currentActivity.getApplicationContext().getPackageName());
	    recIntent.putExtra("android.speech.extra.PROMPT", "Robot Listening...");
	    recIntent.putExtra("android.speech.extra.MAX_RESULTS", 1);
	    startActivityForResult(recIntent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d("WitUnity","onActivityResult requestCode : "+requestCode+" resultCode : "+resultCode+" data : "+data);
	    switch (requestCode) {
	    case VOICE_RECOGNITION_REQUEST_CODE:
	      if ((resultCode == Activity.RESULT_OK) && (null != data)) {
	        ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        Log.d("WitUnity","Speech Result is : "+(String)text.get(0));
	        captureTextIntent(text != null ? (String)text.get(0) : null);
	      }
	      break;
	  }
	}
	
	  public void captureTextIntent(String text)
	  {
	    if (text == null)
	    	UnityPlayer.UnitySendMessage(_gameObjectName, "onWitResult", "{'error': 'Input Text Null'}");
	    WitRequestTask request = new WitRequestTask(_accessToken)
	    {
	      protected void onPostExecute(String result) {
	        WitResponse response = null;
	        Error errorDuringRecognition = null;
	        Log.d("Wit", "Wit : Response " + result);
	        try {
	          Gson gson = new Gson();
	          response = (WitResponse)gson.fromJson(result, WitResponse.class);
	          Log.d("Wit", "Gson : Response " + gson.toJson(response));
	        } catch (Exception e) {
	          Log.e("Wit", "Wit : Error " + e.getMessage());
	          errorDuringRecognition = new Error(e.getMessage());
	        }
	        if (errorDuringRecognition != null) {
	        	UnityPlayer.UnitySendMessage(WitUtility._gameObjectName, "onWitResult", "{'error': 'error during recognition'}");
	        } else if (response == null) {
	        	UnityPlayer.UnitySendMessage(WitUtility._gameObjectName, "onWitResult", "{'error': 'null value'}");
	        } else {
	        	Log.d("Wit", "didGraspIntent Correctly " + response.getOutcome().get_intent());
	        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	            String jsonOutput = gson.toJson(response.getOutcome().get_entities());
	            String resultResponse = String.format("{\"intent\": %s, \"confidence\": %s, \"json\": %s}",response.getOutcome().get_intent(), response.getOutcome().get_confidence(), jsonOutput); 
	            UnityPlayer.UnitySendMessage(WitUtility._gameObjectName, "onWitResult", resultResponse);
	        }
	      }
	    };
	    request.execute(new String[] { text });
	  }
}
