LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := OpenCvUtil
LOCAL_SRC_FILES := com_xindany_OpenCvUtil.cpp
include $(BUILD_SHARED_LIBRARY)