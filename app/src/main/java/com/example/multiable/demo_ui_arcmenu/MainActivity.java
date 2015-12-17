package com.example.multiable.demo_ui_arcmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.multiable.demo_ui_arcmenu.MyView.ArcMenu;

public class MainActivity extends AppCompatActivity {
    private ArcMenu arcMenu ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcMenu=(ArcMenu)findViewById(R.id.id_arcmenu1);
        arcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(MainActivity.this,
                        position + ":" + view.getTag(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
        //通过代码添加item
        ImageView imageView = new ImageView(this) ;
        imageView.setImageResource(R.mipmap.add);
        imageView.setTag("通过java代码添加的");
        arcMenu.addView(imageView);
    }
}
