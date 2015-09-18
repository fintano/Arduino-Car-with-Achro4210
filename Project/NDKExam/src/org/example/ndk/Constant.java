package org.example.ndk;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Constant {

	public static final int FPGAMSG = 1;
	public static final int GPIOMSG = 0;
	public static OutputStreamWriter out;
	public static InputStreamReader in;
	public static boolean isFpgaStarted = false;
	public static boolean isGpioStarted = false;
	public static boolean isInputStarted = false;
}
