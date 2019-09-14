package com.lamarrulla.baseandroid.activities;

import android.content.Context;
/*import android.support.v7.app.AppCompatActivity;*/
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/*import android.widget.Toast;*/

import androidx.appcompat.app.AppCompatActivity;

import com.lamarrulla.baseandroid.R;
import com.lamarrulla.baseandroid.utils.Utils;

public class AltaUsuarioActivity extends AppCompatActivity {

    Button btnSiguiente;
    Utils utils = new Utils();
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.OpenMain(context);
            }
        });
    }
}
