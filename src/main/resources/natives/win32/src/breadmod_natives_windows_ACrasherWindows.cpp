#include "breadmod_natives_windows_ACrasherWindows.h"

#include <iostream>

void AAAAAGHHH() {
  std::cout << "IT DOESN'T WORK!!!!!!!!" << std::endl;
}

extern "C"
JNIEXPORT void JNICALL Java_breadmod_natives_windows_ACrasherWindows_run (JNIEnv * env, jobject thisObject) {
    AAAAAGHHH();
}
