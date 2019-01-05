package com.makemusiccount.pianoview.listener;

/**
 * Created by Gautam on 2016-11-26.
 */

public interface OnLoadAudioListener {

  void loadPianoAudioStart();

  void loadPianoAudioFinish();

  void loadPianoAudioError(Exception e);

  void loadPianoAudioProgress(int progress);
}
