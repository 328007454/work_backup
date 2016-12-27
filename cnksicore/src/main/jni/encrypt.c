#include "com_cnksi_core_utils_EncryptUtils.h"
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "Ksi", __VA_ARGS__)

pthread_t t_id;

int getnumberfor_str(char *str) {
    if (str == NULL) {
        return -1;
    }
    char result[20];
    int count = 0;
    while (*str != '\0') {
        if (*str >= 48 && *str <= 57) {
            result[count] = *str;
            count++;
        }
        str++;
    }
    int val = atoi(result);
    return val;
}

void thread_fuction() {
    int pid = getpid();
    char file_name[20] = {'\0'};
    sprintf(file_name, "/proc/%d/status", pid);
    char linestr[256];
    int i = 0, traceid;
    FILE *fp;
    while (1) {
        i = 0;
        fp = fopen(file_name, "r");
        if (fp == NULL) {
            break;
        }
        while (!feof(fp)) {
            fgets(linestr, 256, fp);
            if (i == 5) {
                traceid = getnumberfor_str(linestr);
                if (traceid > 0) {
                    LOGD("I was be traced...trace pid:%d", traceid);
                    exit(0);
                }
                break;
            }
            i++;
        }
        fclose(fp);
        sleep(5);
    }

}

void create_thread_check_traceid() {
    int err = pthread_create(&t_id, NULL, thread_fuction, NULL);
    if (err != 0) {
        LOGD("create thread fail: %s\n", strerror(err));
    }
}

const char *app_signature = "30820249308201b2a0030201020204516f694d300d06092a864886f70d01010505003068310b300906035504061302434e310f300d06035504080c06e59b9be5b79d310f300d06035504070c06e68890e983bd3111300f060355040a13086b696e6773746f6e3111300f060355040b13086b696e6773746f6e3111300f060355040313086b696e6773746f6e3020170d3133303431383033333232395a180f33303132303831393033333232395a3068310b300906035504061302434e310f300d06035504080c06e59b9be5b79d310f300d06035504070c06e68890e983bd3111300f060355040a13086b696e6773746f6e3111300f060355040b13086b696e6773746f6e3111300f060355040313086b696e6773746f6e30819f300d06092a864886f70d010101050003818d003081890281810081d06a7709819004f0760d64405862ab6caaeb9849d16a34ce79190629ee577c4bd2866c4354023f4a4a71a181f9cd50fe8655d0cded75579bae1192cb2befce2a5ef22a122fbc24f1a4467cae93a5ce09f3c58d974a32b4e593234a0a1a651f79b6c1dfe9ec6656f3b3c8e29253ee0803dad5d738aefd7a2f36bc8ba0061e750203010001300d06092a864886f70d01010505000381810022a4ab2b0a5e84def8982d664d17b5071ea945388f86dcee32e7cb88ad2195aab2dfabcd3b77bc9ba7c4961e41346107e521427391a735c5e145b0e5a89adfd4d596533bc9106cbade808e392f2c3a742eceb000fd35d61d2a082ed7bf8a645078e5b7540b79aa4c5b42628c747f333dd945af2e28336653fc901c705fe5aaf8";

int equal_sign(JNIEnv *env) {

    //调用Java层的Utils中的获取签名的方法
    char *className = "com/cnksi/core/utils/EncryptUtils";
    jclass clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        LOGD("not find class '%s'", className);
        return 1;
    }

    LOGD("class name:%p", clazz);

    jmethodID method = (*env)->GetStaticMethodID(env, clazz, "getSignature",
                                                 "()Ljava/lang/String;");
    if (method == NULL) {
        LOGD("not find method 'getSignature'");
        return 1;
    }

    jstring obj = (jstring)(*env)->CallStaticObjectMethod(env, clazz, method);
    if (obj == NULL) {
        LOGD("method invoke error:%p", obj);
        return 1;
    }

    const char *strAry = (*env)->GetStringUTFChars(env, obj, 0);
    int cmpval = strcmp(strAry, app_signature);
    if (cmpval == 0) {
        return 1;
    }

    (*env)->ReleaseStringUTFChars(env, obj, strAry);

    return 0;

}

JNIEXPORT jboolean JNICALL Java_com_cnksi_core_utils_AppUtils_isEquals
        (JNIEnv * env, jclass class, jstring str){
    return 1;
}


jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI on load...");

    //检测自己有没有被trace
    create_thread_check_traceid();

    //声明变量
    jint result = JNI_ERR;
    JNIEnv *env = NULL;
    //获取JNI环境对象
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGD("ERROR: GetEnv failed\n");
        return result;
    }
//
    LOGD("JNIEnv:%p", env);
    LOGD("start equal signature...");
    int check_sign = equal_sign(env);
    LOGD("check_sign:%d", check_sign);
    if (check_sign == 0) {
        exit(0);
    }

    result = JNI_VERSION_1_4;
    return result;
}

//onUnLoad方法，在JNI组件被释放时调用
void JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGD("JNI unload...");
}

