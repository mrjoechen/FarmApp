LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
OpenCV_INSTALL_MODULES := on
OpenCV_CAMERA_MODULES := on
OPENCV_LIB_TYPE :=STATIC


ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
include $(LOCAL_PATH)/native/jni/OpenCV.mk
else
include $(OPENCV_MK_PATH)
endif
LOCAL_MODULE := OpenCvUtil
LOCAL_SRC_FILES := com_xindany_OpenCvUtil.cpp
LOCAL_LDLIBS +=  -lm -llog
include $(BUILD_SHARED_LIBRARY)