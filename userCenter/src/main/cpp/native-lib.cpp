#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_dj_usercenter_LoginActivity_getSecret(JNIEnv *env, jclass type) {
    return env->NewStringUTF("38693b9aba3759fd8a74b5a1088aed73");
}

