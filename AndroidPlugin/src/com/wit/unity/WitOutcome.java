package com.wit.unity;


import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

public class WitOutcome
{

  @SerializedName("intent")
  private String _intent;

  @SerializedName("entities")
  private HashMap<String, JsonElement> _entities;

  @SerializedName("confidence")
  private double _confidence;

  public double get_confidence()
  {
    return this._confidence;
  }

  public String get_intent() {
    return this._intent;
  }

  public HashMap<String, JsonElement> get_entities() {
    return this._entities;
  }
}