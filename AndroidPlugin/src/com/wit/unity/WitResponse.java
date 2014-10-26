package com.wit.unity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class WitResponse
  implements Serializable
{
  private static final long serialVersionUID = 1L;

  @SerializedName("msg_id")
  private String _msgId;

  @SerializedName("outcome")
  private WitOutcome _outcome;

  @SerializedName("msg_body")
  private String _body;

  public String getMsgId()
  {
    return this._msgId;
  }

  public WitOutcome getOutcome() {
    return this._outcome;
  }

  public String getBody() {
    return this._body;
  }
}