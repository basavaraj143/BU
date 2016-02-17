package com.example.appsriv_02.loginwithfacebook;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class Mp4_Splash extends Activity  implements MediaPlayer.OnCompletionListener {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.newsplashscreen);
        VideoView video = (VideoView) findViewById(R.id.myvideoview);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.bubble);
        //video.setMediaController(new MediaController(Mp4_Splash.this));
        video.start();
        video.setOnCompletionListener(this);

       /* video.setOnPreparedListener(PreparedListener);
        AudioManager am = (AudioManager) Mp4_Splash.this.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);*/
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Intent intent = new Intent(this, MainClass.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
       // overridePendingTransition(R.anim.in_animation, R.anim.out_animation);
        Mp4_Splash.this.finish(); // Call once you redirect to another activity
    }


    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(false);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}