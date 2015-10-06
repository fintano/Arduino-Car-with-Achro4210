#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <jni.h>
#include "android/log.h"

//#include "myJni.h"
#define LOG_TAG "MyTag"
#define LOGV(...)   __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define PAGE_SIZE 4096

JNIEXPORT void JNICALL Java_org_example_ndk_NDKExam_getFD(JNIEnv* env,
		jobject thisObj) {

	int configfd, fpgafd;
	unsigned int temp_addr = NULL;
	unsigned long n = 0;

	jclass thisClass = (*env)->GetObjectClass(env, thisObj);

	//get fd id

	jfieldID fidNum = (*env)->GetFieldID(env, thisClass, "gpioFd", "I");
	if (NULL == fidNum)
		return;
	jfieldID fidNum_fpga = (*env)->GetFieldID(env, thisClass, "fpgaFd", "I");
	if (NULL == fidNum_fpga)
		return;

	jint number = (*env)->GetIntField(env, thisObj, fidNum);

	/*
	 *  Open Fpga push driver and GPIO push driver
	 */

	configfd = open("dev/inter", O_RDWR);

	if ((fpgafd = open("dev/fpga_push_switch", O_RDWR)) < 0 || configfd <0) {
		LOGV("Device Open ERROR!");
		return;
	}
	/*
	 * Assign mmap space for communicating between kernel and user space
	 */
	temp_addr = (unsigned int) mmap(NULL, PAGE_SIZE, PROT_READ | PROT_WRITE,
			MAP_SHARED, configfd, 0);

	if (temp_addr == MAP_FAILED ) {
		perror("mmap operation fail!\n");
		return;
	}

	/*
	 * enter file descriptor in valuable fds of NDKExam.java
	 */

	number = configfd;
	(*env)->SetIntField(env, thisObj, fidNum, number);
	number = fpgafd;
	(*env)->SetIntField(env, thisObj, fidNum_fpga, number);
	LOGV("interrupt ***%d", number);
	/*
	 * enter address in valuable address of NDKExam.java
	 */

	jfieldID fidAddr = (*env)->GetFieldID(env, thisClass, "address", "I");
	if (NULL == fidAddr)
		return;

	jint address = (jint) (temp_addr); //**
	(*env)->SetIntField(env, thisObj, fidAddr, address);

}

JNIEXPORT jint JNICALL Java_org_example_ndk_NDKExam_getCmd(JNIEnv *env,
		jobject thisObj) {

	int ret;
	int configfd; //file descriptor
	int* address = NULL; // start address of mmap
	unsigned long n = 0;

	jclass thisClass = (*env)->GetObjectClass(env, thisObj);

	jfieldID fidNum = (*env)->GetFieldID(env, thisClass, "gpioFd", "I");
	if (NULL == fidNum)
		return -1;
	// get Filedescriptor
	jint number = (*env)->GetIntField(env, thisObj, fidNum);
	//change number and set
	configfd = number; //**

	jfieldID fidAddr = (*env)->GetFieldID(env, thisClass, "address", "I");

	if (NULL == fidAddr)
		return -1;

	jint temp_addr = (*env)->GetIntField(env, thisObj, fidAddr);
	address = (int*) temp_addr;

	//change number and set
	write(configfd, &n, sizeof(unsigned long)); // Sleep this thread until interrupt is entered
	/*
	 * Return which interrupt is pressed
	 */
	return (*address);
}

JNIEXPORT jobject JNICALL Java_org_example_ndk_NDKExam_getFpgaBtn(JNIEnv *env,
		jobject thisObj, jint fd) {

	unsigned char fpga_buf[9];
	jchar fpga_buf_temp[9];
	int i;

	/*
	 *  Read fpga buttons
	 */

	read(fd, &fpga_buf, sizeof(char) * 9);

	for(i=0; i<9 ; i++){
		fpga_buf_temp[i] = fpga_buf[i];
	}

	/*
	 * Ready to return information of buttons
	 */

	jcharArray outJNIArray = (*env) -> NewCharArray(env,9);
	if(NULL == outJNIArray) return NULL;
	(*env) -> SetCharArrayRegion(env,outJNIArray,0,9,fpga_buf_temp);
	return outJNIArray;
}

