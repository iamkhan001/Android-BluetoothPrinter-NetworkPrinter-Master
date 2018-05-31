package studio.n.printwireless;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import studio.n.printwireless.utils.PrinterCommands;
import studio.n.printwireless.utils.PrinterUtils;



/**
 * Created by Imran Khan on 31 may 2018.
 */

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "Print";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter bluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progressDialog;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private int printType;
    private EditText editInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editInput = findViewById(R.id.edit_input);

        findViewById(R.id.btn_print_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editInput.getText().length()>0){
                    printType = 0;
                    if (bluetoothSocket !=null){
                        printBluetooth();
                    }else {
                        connectDeviceBT();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Please Enter Some Text",Toast.LENGTH_SHORT).show();
                }

            }
        });


        findViewById(R.id.btn_print_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editInput.getText().length()>0){
                    printType = 1;
                    new PrinterChooser(MainActivity.this).show();
                }else {
                    Toast.makeText(MainActivity.this,"Please Enter Some Text",Toast.LENGTH_SHORT).show();
                }
            }
        });


        findViewById(R.id.btn_print_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editInput.getText().length()>0){
                    printType = 2;
                    new PrinterChooser(MainActivity.this).show();
                }else {
                    Toast.makeText(MainActivity.this,"Please Enter Some Text",Toast.LENGTH_SHORT).show();
                }
            }
        });


        findViewById(R.id.btn_print_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printType = 3;
                new PrinterChooser(MainActivity.this).show();
            }
        });

        findViewById(R.id.btn_print_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printType = 4;
                new PrinterChooser(MainActivity.this).show();
            }
        });



    }


    private void printBluetooth() {

        switch (printType){
            case 0:
                printText(editInput.getText().toString(),0,0);
                break;
            case 1:
                try {
                    Bitmap bitmap = getBarcodeBitmap(editInput.getText().toString(),BarcodeFormat.QR_CODE,250,250);
                    if (bitmap!=null){
                        printPhoto(bitmap,250);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    Bitmap bitmap = getBarcodeBitmap(editInput.getText().toString(),BarcodeFormat.CODE_128,1000,250);
                    if (bitmap!=null){
                        printPhoto(bitmap,500);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            case 3:

                ImageView img = findViewById(R.id.image);
                Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                printPhoto(bitmap,250);

                break;

            case 4:

                Bitmap bitPart1 = getBitmapFromView(findViewById(R.id.layout_part_1));
                Bitmap bitPart2 = getBitmapFromView(findViewById(R.id.layout_part_2));
                printPhoto(bitPart1,600);
                printPhoto(bitPart2,600);


                break;
        }

    }

    void printWifi(){
        switch (printType){

            case 1:
                try {
                    PrintHelper photoPrinter = new PrintHelper(MainActivity.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    Bitmap bitmap = getBarcodeBitmap(editInput.getText().toString(),BarcodeFormat.QR_CODE,250,250);
                    if (bitmap!=null){
                        photoPrinter.printBitmap("test print", bitmap);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                try {
                    PrintHelper photoPrinter = new PrintHelper(MainActivity.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    Bitmap bitmap = getBarcodeBitmap(editInput.getText().toString(),BarcodeFormat.CODE_128,1000,250);
                    if (bitmap!=null){
                        photoPrinter.printBitmap("test print", bitmap);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                try {
                    PrintHelper photoPrinter = new PrintHelper(MainActivity.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    ImageView img = findViewById(R.id.image);
                    Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                    photoPrinter.printBitmap("test print", bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case 4:

                try {
                    PrintHelper photoPrinter = new PrintHelper(MainActivity.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    Bitmap bitmap = getBitmapFromView(findViewById(R.id.layout_parent));
                    photoPrinter.printBitmap("test print", bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }

    }

    private void printText(String msg, int size, int align) {

        OutputStream outputStream;
        try {
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            printNewLine(outputStream);
            printNewLine(outputStream);
            printNewLine(outputStream);

            //outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void printPhoto(Bitmap bMap, int size) {
        try {

            Bitmap bitmap = getResizedBitmap(bMap,size);

            if(bitmap!=null){
                final byte[] command = PrinterUtils.decodeBitmap(bitmap);
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                if (command != null) {
                    outputStream.write(command);
                }
                outputStream.write(PrinterCommands.FEED_LINE);
                printNewLine(outputStream);
                printNewLine(outputStream);
                printNewLine(outputStream);
                //outputStream.close();
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printNewLine(OutputStream outputStream) {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    void connectDeviceBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Message1", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(MainActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
            }
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter
                .getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice mDevice : bondedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    private String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public Bitmap getBarcodeBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {

        if (contents == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap bitmap,int maxSize) {
        //Bitmap bitmap = getBitmapFromView(layoutPhoto);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (bluetoothAdapter != null){
            bluetoothAdapter.disable();
        }

        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {

                    Bundle bundle = intent.getExtras();
                    String deviceAddress = bundle.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + deviceAddress);
                    bluetoothDevice = bluetoothAdapter
                            .getRemoteDevice(deviceAddress);
                    progressDialog = ProgressDialog.show(this,
                            "Connecting...", bluetoothDevice.getName() + " : "
                                    + bluetoothDevice.getAddress(), true, false);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                bluetoothSocket = bluetoothDevice
                                        .createRfcommSocketToServiceRecord(applicationUUID);
                                bluetoothAdapter.cancelDiscovery();
                                bluetoothSocket.connect();
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
                                printBluetooth();
                                //printPhoto(getBitmapFromView(layoutBody));
                            } catch (IOException eConnectException) {
                                Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
                                closeSocket(bluetoothSocket);
                            }
                        }
                    });

                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(MainActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private class PrinterChooser extends Dialog {

        PrinterChooser(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.window_printer_chooser);
            Objects.requireNonNull(getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            findViewById(R.id.view_bluetooth).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bluetoothSocket !=null){
                        printBluetooth();
                    }else {
                        connectDeviceBT();
                    }
                    dismiss();
                }
            });

            findViewById(R.id.view_wifi).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    printWifi();
                    dismiss();
                }
            });




        }
    }

}
