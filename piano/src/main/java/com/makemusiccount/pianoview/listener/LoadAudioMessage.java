package com.makemusiccount.pianoview.listener;

/**
 * Created by Gautam on 2016-11-27.
 */

public interface LoadAudioMessage {

  void sendStartMessage();

  void sendFinishMessage();

  void sendErrorMessage(Exception e);

  void sendProgressMessage(int progress);
}
