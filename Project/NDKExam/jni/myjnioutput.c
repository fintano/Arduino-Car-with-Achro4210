#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <jni.h>
#include <time.h>
#include "android/log.h"

//#include "myJni.h"
#define LOG_TAG "MyTag"
#define LOGV(...)   __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define PAGE_SIZE 4096

JNIEXPORT void JNICALL Java_org_example_ndk_InputToBoard_getFD(JNIEnv* env,
		jobject thisObj) {

	int lcd_fd, dot_fd, fnd_fd, buzzer_fd;
	unsigned long n = 0;

	jclass thisClass = (*env)->GetObjectClass(env, thisObj);

	//Get fd id

	jfieldID fidNum_lcd = (*env)->GetFieldID(env, thisClass, "lcdFd", "I");
	if (NULL == fidNum_lcd)
		return;
	jfieldID fidNum_dot = (*env)->GetFieldID(env, thisClass, "dotFd", "I");
	if (NULL == fidNum_dot)
		return;
	jfieldID fidNum_fnd = (*env)->GetFieldID(env, thisClass, "fndFd", "I");
	if (NULL == fidNum_fnd)
		return;
	jfieldID fidNum_buzzer = (*env)->GetFieldID(env, thisClass, "buzzerFd",	 "I");
	if (NULL == fidNum_buzzer)
		return;

	jint number;

	/*
	 *  Open driver
	 */

	lcd_fd = open("dev/fpga_text_lcd", O_RDWR);
	dot_fd = open("dev/fpga_dot", O_RDWR);
	fnd_fd = open("dev/fpga_fnd", O_RDWR);
	buzzer_fd = open("dev/fpga_buzzer", O_RDWR);

	if (lcd_fd < 0 || dot_fd < 0 || fnd_fd < 0 || buzzer_fd < 0) {
		LOGV("Device Open ERROR!");
		return;
	}

	/*
	 * enter file descriptor in valuable fds of NDKExam.java
	 */

	number = lcd_fd;
	(*env)->SetIntField(env, thisObj, fidNum_lcd, number);
	number = dot_fd;
	(*env)->SetIntField(env, thisObj, fidNum_dot, number);
	number = fnd_fd;
	(*env)->SetIntField(env, thisObj, fidNum_fnd, number);
	number = buzzer_fd;
	(*env)->SetIntField(env, thisObj, fidNum_buzzer, number);

	LOGV("interrupt ***%d", number);

}

JNIEXPORT void JNICALL Java_org_example_ndk_InputToBoard_outFnd(JNIEnv* env,
		jobject thisObj, jint fd, jint tmp_data) {

	unsigned char retval;
	unsigned char data[4];
	int tmp_int = tmp_data;

	if(tmp_int > 10000 || tmp_int < 0){
		LOGV("Fnd number is too small or big");
	}

	data[0] = (tmp_int) / 1000;
	tmp_int -= data[0] * 1000;
	data[1] = (tmp_int) / 100;
	tmp_int -= data[1] * 100;
	data[2] = (tmp_int) / 10;
	tmp_int -= data[2] * 10;
	data[3] = tmp_int;

	retval = write(fd, &data, 4);
	if(retval < 0){
		LOGV("Fnd write ERROR!");
	}
}

JNIEXPORT void JNICALL Java_org_example_ndk_InputToBoard_outDot(JNIEnv* env,
		jobject thisObj, jint fd, jint left, jint front, jint right){
	unsigned char retval;
	int i,j;
	char dot_mat[10][7];
	char dot_set[9];
	int data;

	for(i=0;i<10;i++)for(j=0;j<7;j++) dot_mat[i][j]=0;	//initialize
	dot_mat[4][3]=1;
	dot_mat[5][3]=1;

	/*
	 *  dist is (mm) unit.
	 *  10 = 1cm
	 *  100 = 10cm
	 *  1000 = 100cm = 1m
	 */
	int wall_1_dist=300;
	int wall_2_dist=200;
	int wall_3_dist=100;

	if(front<=wall_1_dist && front>wall_2_dist){
		for(i=0;i<7;i++){
			dot_mat[0][i]=1;
		}
	}
	else if(front<=wall_2_dist && front>wall_3_dist){
		for(i=0;i<7;i++){
			dot_mat[0][i]=1;
			dot_mat[1][i]=1;
		}
	}
	else if(front<=wall_3_dist){
		for(i=0;i<7;i++){
			dot_mat[0][i]=1;
			dot_mat[1][i]=1;
			dot_mat[2][i]=1;
		}
	}

	if(left<=wall_2_dist && left>wall_3_dist){
		for(i=0;i<10;i++){
			dot_mat[i][0]=1;
		}
	}
	else if(left<=wall_3_dist){
		for(i=0;i<10;i++){
			dot_mat[i][0]=1;
			dot_mat[i][1]=1;
		}
	}

	if(right<=wall_2_dist && right>wall_3_dist){
		for(i=0;i<10;i++){
			dot_mat[i][6]=1;
		}
	}
	else if(right<=wall_3_dist){
		for(i=0;i<10;i++){
			dot_mat[i][6]=1;
			dot_mat[i][6]=1;
		}
	}
	for(i=0;i<10;i++){
		data = arrToInt(dot_mat[i]);
		dot_set[i]=data;

	}
	//LOGV("DOT data : %x",data);
	retval = write(fd, dot_set, 9);
	if(retval < 0){
		LOGV("DOT write ERROR!");
	}
}

int arrToInt(char arr[]){
	int temp, i;
	temp=0;
	for(i=0;i<7;i++){
		temp*=2;
		if(arr[i]==1)temp++;
	}
	return temp;
}

JNIEXPORT void JNICALL Java_org_example_ndk_InputToBoard_outBuzzer(JNIEnv* env,
		jobject thisObj, jint fd, jint tmp_data){
	int flag=0, data, retval;
	int danger_dist = 100;

	if(tmp_data<danger_dist){
		flag=1;
	}

	if(flag==1){
		data=1;
		retval=write(fd,&data,1);
	}
	else{
		data=0;
		retval=write(fd,&data,1);
	}

}
