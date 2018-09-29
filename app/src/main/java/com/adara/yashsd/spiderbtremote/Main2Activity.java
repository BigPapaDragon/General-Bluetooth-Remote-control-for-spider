package com.adara.yashsd.spiderbtremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Main2Activity extends AppCompatActivity {

    Button button;
    ListView lvDevices;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairdevices;

    String lastDeviceAdressFile = "lastDeviceAdressFile";
    String lastDeviceAdress = null;

    ArrayList<String> LampName;
    ArrayList<String> LampAddress ;

    customListviewAdapter CLVA;

    boolean isResultSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        LampName = new ArrayList<>();
        LampAddress = new ArrayList<>();

        button = (Button)findViewById(R.id.button);
        lvDevices = (ListView)findViewById(R.id.lvdevices);

        try{
            FileInputStream fis = openFileInput(lastDeviceAdressFile);
            String temp = "";
            int c;
            while((c = fis.read())!= -1)
            {
                temp = temp + Character.toString((char)c);
            }
            lastDeviceAdress = temp;
        }catch (IOException e)
        {e.printStackTrace();}

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastDeviceAdress.equals("")||lastDeviceAdress.equals(null)){
                    Toast.makeText(Main2Activity.this, "No Last Device Available", Toast.LENGTH_SHORT).show();
                }
                else{
                    isResultSet = true;

                    Intent i = new Intent();
                    i.putExtra("address",lastDeviceAdress);
                    setResult(007,i);
                    finish();
                }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(this, "Device does not have Bluetooth", Toast.LENGTH_SHORT).show();
        }
        else if(!bluetoothAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        pairdevices = bluetoothAdapter.getBondedDevices();

        if(pairdevices.size()>0){
            for(BluetoothDevice bd : pairdevices){
                LampName.add(bd.getName());
                LampAddress.add(bd.getAddress());
            }

            String[] LampNameArr = new String[LampName.size()];
            LampNameArr = LampName.toArray(LampNameArr);

            String[] LampAddressArr = new String[LampAddress.size()];
            LampAddressArr = LampAddress.toArray(LampAddressArr);

            CLVA = new customListviewAdapter(this,LampNameArr,LampAddressArr);
            lvDevices.setAdapter(CLVA);

            lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView deviceAddress = (TextView)view.findViewById(R.id.pattern);

                    try{
                        FileOutputStream fos = openFileOutput(lastDeviceAdressFile,MODE_PRIVATE);
                        fos.write(deviceAddress.getText().toString().getBytes());
                        fos.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    isResultSet = true;

                    Intent i = new Intent();
                    i.putExtra("address",deviceAddress.getText().toString());
                    setResult(007,i);
                    finish();
                }
            });
        }

    }

    @Override
    public void finish() {
        super.finish();
        if(!isResultSet){
            Intent i = new Intent();
            i.putExtra("address","null");
            setResult(007,i);
            finish();
        }
    }
}
