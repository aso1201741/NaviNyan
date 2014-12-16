package com.botty.navinyan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �^�C�g�����\���ɂ��܂��B
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// splash.xml��View�Ɏw�肵�܂��B
		setContentView(R.layout.activity_splash);
		Handler hdl = new Handler();
		// 500ms�x��������splashHandler�����s���܂��B
		hdl.postDelayed(new splashHandler(), 1000);
	}
	class splashHandler implements Runnable {
		public void run() {
			// �X�v���b�V��������Ɏ��s����Activity���w�肵�܂��B
			Intent intent = new Intent(getApplication(), TopActivity.class);
			startActivity(intent);
			// SplashActivity���I�������܂��B
			SplashActivity.this.finish();
		}
	}
}
