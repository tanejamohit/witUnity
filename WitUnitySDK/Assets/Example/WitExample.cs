using UnityEngine;
using System.Collections;
using Wit;

public class WitExample : MonoBehaviour {

	// Use this for initialization
	void Start () {
		WitEngine.onSpeechResult += ShowWitResult;
	}

	// Show the GUI
	void OnGUI() {
		GUI.matrix = Matrix4x4.Scale(new Vector3(2, 2, 2));
		
		if (GUILayout.Button("Test Wit Utility")) {
			WitEngine.startListening();
		}
	}

	// Update is called once per frame
	void Update () {
	
	}

	void ShowWitResult(string witResult) {
		gameObject.guiText.text = witResult;
	}
}
