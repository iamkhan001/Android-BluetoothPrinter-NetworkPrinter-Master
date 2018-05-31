package studio.n.printwireless;


/**
 * Created by Imran Khan on 31 may 2018.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class DeviceListActivity extends Activity {
    protected static final String TAG = "TAG";
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<DeviceModel> list;

    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.window_bluetooth_devices);

        setResult(Activity.RESULT_CANCELED);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(DeviceListActivity.this));

        list = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();

        if (deviceSet.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : deviceSet) {
                Log.e("device","type: > "+device.getType()+" class > "+device.getBluetoothClass());
                list.add(new DeviceModel(device.getName(),device.getAddress()));
            }
            DeviceListAdapter adapter = new DeviceListAdapter(list);
            recyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(DeviceListActivity.this,"No Devices Found!",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    void connectToDevice(int pos){
        try {
            bluetoothAdapter.cancelDiscovery();
            String mDeviceInfo = list.get(pos).getAddress();
            String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
            Log.e(TAG, "Device_Address " + mDeviceAddress);

            Bundle mBundle = new Bundle();
            mBundle.putString("DeviceAddress", mDeviceAddress);
            Intent mBackIntent = new Intent();
            mBackIntent.putExtras(mBundle);
            setResult(Activity.RESULT_OK, mBackIntent);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.MyViewHolder>{

        private ArrayList<DeviceModel> list;

        DeviceListAdapter(ArrayList<DeviceModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

            holder.tvName.setText(list.get(position).getName());
            holder.tvAddress.setText(list.get(position).getAddress());
            holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectToDevice(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tvName,tvAddress;
            private RelativeLayout layoutParent;

            MyViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.text_name);
                tvAddress = itemView.findViewById(R.id.text_address);
                layoutParent = itemView.findViewById(R.id.view_parent);
            }
        }

    }

    class DeviceModel{
        private String name,address;

        DeviceModel(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }

}
