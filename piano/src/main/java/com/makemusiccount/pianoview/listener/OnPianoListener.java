package com.makemusiccount.pianoview.listener;

/**
 * Created by Gautam on 2017-02-20.
 */

import com.makemusiccount.pianoview.entity.Piano;

public interface OnPianoListener {

  void onPianoInitFinish();

  void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup);
}
