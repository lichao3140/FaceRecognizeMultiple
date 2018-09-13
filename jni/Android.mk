LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := THFacialPos
LOCAL_SRC_FILES := libTHFacialPos.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := THFaceLive
LOCAL_SRC_FILES := libTHFaceLive.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := JPG
LOCAL_SRC_FILES := libJPG.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := THFaceImage
LOCAL_SRC_FILES := libTHFaceImage.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := THFeature
LOCAL_SRC_FILES := libTHFeature.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := ImageUtil
LOCAL_SRC_FILES :=  yuv420.cpp imageUtil.cpp
LOCAL_SHARED_LIBRARIES := JPG \

LOCAL_CXXFLAGS := -fopenmp
LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp

#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS += -mhard-float -lm
#LOCAL_LDFLAGS += -fopenmp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := FaceDM2016
LOCAL_SRC_FILES :=  dm2016.cpp
#LOCAL_SHARED_LIBRARIES :=  \
LOCAL_CXXFLAGS := -fopenmp
LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp

#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS += -mhard-float -lm
#LOCAL_LDFLAGS += -fopenmp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := FaceDetect
LOCAL_SRC_FILES :=  faceDetect.cpp
LOCAL_SHARED_LIBRARIES := THFaceImage \
                          THFacialPos \
LOCAL_CXXFLAGS := -fopenmp
LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp

#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS += -mhard-float -lm
#LOCAL_LDFLAGS += -fopenmp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := FaceLive
LOCAL_SRC_FILES :=  faceLive.cpp
LOCAL_SHARED_LIBRARIES := THFacialPos \
                          THFaceLive \

LOCAL_CXXFLAGS := -fopenmp
LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS += -mhard-float -lm
#LOCAL_LDFLAGS += -fopenmp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
#LOCAL_MODULE    := FaceFeature

#LOCAL_SRC_FILES :=  faceFeature.cpp

#LOCAL_SHARED_LIBRARIES := THFeature \
                          libcmp.so \

#LOCAL_CXXFLAGS := -fopenmp
#LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
#LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS +=  -mhard-float -lm
#LOCAL_LDFLAGS += -fopenmp
#include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := FaceRecognize

LOCAL_SRC_FILES := dm2016.cpp yuv420.cpp faceRecognize.cpp

LOCAL_SHARED_LIBRARIES := Jpg \
                          THFaceImage \
                          THFacialPos \
                          THFaceLive \
                          THFeature

LOCAL_CXXFLAGS := -fopenmp
LOCAL_CFLAGS += -mfloat-abi=soft -fopenmp -DDEF_ENABLE_OPENMP
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog -lz -Wl,--no-warn-mismatch -lm_hard -lm_hard -fopenmp
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz
#LOCAL_CFLAGS +=  -mhard-float -lm
LOCAL_LDFLAGS += -fopenmp
include $(BUILD_SHARED_LIBRARY)


#include $(CLEAR_VARS)

#LOCAL_MODULE    := mainTest

#LOCAL_SRC_FILES := mainTest.cpp

#LOCAL_CXXFLAGS := -fopenmp
#LOCAL_CFLAGS += -fopenmp
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lz #-fopenmp
#include $(BUILD_EXECUTABLE)