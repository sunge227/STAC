package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by minseong on 2017-07-28.
 */

public class BlueTooth extends Activity implements AdapterView.OnItemClickListener{
    static final int ACTION_ENABLE_BT = 101;
    BluetoothAdapter mBA;
    ListView mListDevice;
    ArrayList<String> mArDevice;
    TextView mtext;
    EditText mEdit;
    static final String BLUE_NAME = "BluetoothEx";
    static final UUID BLUE_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    ClientThread mCThread = null; // 클라이언트 소켓 접속 스레드
    ServerThread mSThread = null; // 서버 소켓 접속 스레드
    SocketThread mSocketThread = null; // 데이터 송수신 스레드

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        mtext = (TextView)findViewById(R.id.textMessage);
        mEdit = (EditText)findViewById(R.id.editData);

        initListView();

        boolean isBlue = canUseBluetooth();
        if(isBlue)
            getParedDevice();
    }

    public boolean canUseBluetooth() {

        mBA = BluetoothAdapter.getDefaultAdapter();

        if(mBA == null){
            mtext.setText("not found Device");
            return false;
        }

        mtext.setText("Device is exist");

        if(mBA.isEnabled()){
            mtext.append("\nDeivce can use");
            return true;
        }

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, ACTION_ENABLE_BT);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTION_ENABLE_BT){

            if(resultCode == RESULT_OK){
                mtext.setText("\nDevice can use");
                getParedDevice();
            }else {
                mtext.setText("\nDevice can not use");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end onActivityResult

    public void startFindDevice(){
        stopFindDeivce();
        mBA.startDiscovery();
        registerReceiver(mBlueRecv , new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }//end startFindDevice

    // 검색중지
    public void stopFindDeivce(){
        if(mBA.isDiscovering()){
            mBA.cancelDiscovery();
            unregisterReceiver(mBlueRecv);
        }
    }

    BroadcastReceiver mBlueRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == BluetoothDevice.ACTION_FOUND){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                    addDeviceToList(device.getName() , device.getAddress());
            }
        }
    }; // end BroadcastReceiver

    public void addDeviceToList(String name , String address){
        String deviceinfo = name+" - "+address;

        Log.d("tag1" , "Device Find: "+deviceinfo);
        mArDevice.add(deviceinfo);
        ArrayAdapter adapter = (ArrayAdapter)mListDevice.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public void initListView(){
        mArDevice = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 , mArDevice) ;

        mListDevice = (ListView)findViewById(R.id.listDevice);
        mListDevice.setAdapter(adapter);
        mListDevice.setOnItemClickListener(this);
    }

    public void setDiscoverable(){
        if(mBA.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
            return;

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION , 0);
        startActivity(intent);
    }

    public void getParedDevice(){
        if(mSThread != null) return;

        mSThread = new ServerThread();
        mSThread.start();

        Set<BluetoothDevice> devices = mBA.getBondedDevices();

        for(BluetoothDevice device : devices){
            addDeviceToList(device.getName() , device.getAddress());
        }

        startFindDevice();
        setDiscoverable();
    }

    public void onItemClick(AdapterView parent , View view , int position , long id){
        String strItem = mArDevice.get(position);

        int pos = strItem.indexOf(" - ");
        if(pos <= 0) return;
        String address = strItem.substring(pos+3);
        mtext.setText("Sel Device: "+address);

        stopFindDeivce();

        mSThread.cancel();
        mSThread=null;

        if(mCThread != null) return;

        BluetoothDevice device = mBA.getRemoteDevice(address);
        mCThread = new ClientThread(device);
        mCThread.start();
    }

    private class ClientThread extends Thread {
        private BluetoothSocket mmCSocket;

        public ClientThread(BluetoothDevice device){
            try{
                mmCSocket = device.createInsecureRfcommSocketToServiceRecord(BLUE_UUID);
            }catch (IOException e){
                showMessage("Create Client Socket error");
                return ;
            }
        }

        public void run(){
            try {
                mmCSocket.connect();
            }catch (IOException e){
                showMessage("Connect Socket close error");
                try{
                    mmCSocket.close();
                }catch (IOException e2){
                    showMessage("Client Socket close error");
                }
                return;
            }

            onConnected(mmCSocket);
        }

        public void cancel(){
            try{
                mmCSocket.close();
            }catch (IOException e){
                showMessage("Client Socket close error");
            }
        }
    }

    private class ServerThread extends Thread {
        private BluetoothServerSocket mmSSocket;

        // 서버 소켓 생성
        public ServerThread() {
            try {
                mmSSocket = mBA.listenUsingInsecureRfcommWithServiceRecord(BLUE_NAME, BLUE_UUID);
            } catch(IOException e) {
                showMessage("Get Server Socket Error");
            }
        }

        public void run() {
            BluetoothSocket cSocket = null;

            // 원격 디바이스에서 접속을 요청할 때까지 기다린다
            try {
                cSocket = mmSSocket.accept();
            } catch(IOException e) {
                showMessage("Socket Accept Error");
                return;
            }

            // 원격 디바이스와 접속되었으면 데이터 송수신 스레드를 시작
            onConnected(cSocket);
        }

        // 서버 소켓 중지
        public void cancel() {
            try {
                mmSSocket.close();
            } catch (IOException e) {
                showMessage("Server Socket close error");
            }
        }
    }

    // 메시지를 화면에 표시
    public void showMessage(String strMsg) {
        // 메시지 텍스트를 핸들러에 전달
        Message msg = Message.obtain(mHandler, 0, strMsg);
        mHandler.sendMessage(msg);
        Log.d("tag1", strMsg);
    }

    // 메시지 화면 출력을 위한 핸들러
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String strMsg = (String)msg.obj;
                mtext.setText(strMsg);
            }
        }
    };

    public void onConnected(BluetoothSocket socket){
        showMessage("Socket connected");

        if(mSocketThread != null)
            mSocketThread = null;

        mSocketThread = new SocketThread(socket);
        mSocketThread.start();
    }

    private class SocketThread extends Thread{
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        private SocketThread(BluetoothSocket socket) {
            mmSocket = socket;

            try{
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
            }catch (IOException e){
                showMessage("Get Stream error");
            }
        }

        public void run(){
            byte [] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = mmInStream.read(buffer);
                    String strBuf = new String(buffer , 0 , bytes);

                    // 명령어 수행 코드


                    showMessage("Receive: "+strBuf);
                    SystemClock.sleep(1);
                }catch (IOException e){
                    showMessage("Socket disconnected");
                    break;
                }
            }
        }

        public void write(String strBuf){
            try{
                byte[] buffer = strBuf.getBytes();
                mmOutStream.write(buffer);
                showMessage("Send: "+strBuf);
            }catch (IOException e)
            {
                showMessage("Socket Write error");
            }
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSend :{
                if(mSocketThread == null)return;

                String strBuf = mEdit.getText().toString();
                if(strBuf.length() < 1 )return;
                mEdit.setText("");
                mSocketThread.write(strBuf);
                break;
            }
        }
    }

    public void onDestory(){
        super.onDestroy();

        stopFindDeivce();

        if(mCThread != null){
            mCThread.cancel();
            mCThread=null;
        }
        if(mSThread != null){
            mSThread.cancel();
            mSThread=null;
        }
        if(mSocketThread != null){
            mSocketThread=null;
        }
    }

}
