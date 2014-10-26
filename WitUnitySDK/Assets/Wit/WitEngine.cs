using UnityEngine;
using System.Collections;
using System;

namespace Wit {
	public class WitEngine : MonoBehaviour {

		private static AndroidJavaObject witUtilityActivity;
		public static event Action<String> onSpeechResult;

		// Use this for initialization
		void Start () {
			using (AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
				witUtilityActivity = jc.GetStatic<AndroidJavaObject>("currentActivity");
			}
			//Wit.Call("init","");
			witUtilityActivity.CallStatic("setGameObjectName", gameObject.name);
			witUtilityActivity.CallStatic("setAccessToken", "KEG6DZE66ZYDLVQELWXCMNQ7HLFTKRAF");
		}
		
		// Update is called once per frame
		void Update () {
			
		}



		public static void startListening() {
			witUtilityActivity.Call("startListening");
		}

		public void onWitResult(string jsonLog) {
			onSpeechResult(jsonLog);
		}
	}
}
