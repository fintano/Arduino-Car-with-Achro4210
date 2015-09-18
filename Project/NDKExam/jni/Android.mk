LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:=ndk-exam
LOCAL_SRC_FILES:=myjni.c myjnioutput.c
LOCAL_LDLIBS := -llog -landroid
#LOCAL_LDLIB := -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)

