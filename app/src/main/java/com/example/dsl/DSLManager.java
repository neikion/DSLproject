package com.example.dsl;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.dsl.notice.MenuActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public final class DSLManager{

    private static class InnerSingleton{
        private static final DSLManager instance=new DSLManager();
    }
    public static DSLManager getInstance(){
        return InnerSingleton.instance;
    }

    //awake
    private DSLManager(){
        //exs= new ThreadPoolExecutor(0,20,10, TimeUnit.MINUTES,new SynchronousQueue<Runnable>());
        exs =Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t=new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        Server=new ServerConnect("13.124.33.18");
//        localDB=new LocalDataBase();
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(()-> Close()));
        }catch (Exception e){
            Close();
            e.printStackTrace();
        }

    }
    private ExecutorService exs;
    private ServerConnect Server;
    private UserInfo userInfo=null;
    public interface NetListener {
        void Result(JSONArray Result);
    }
    private final class UserInfo{
        private final int UserID;
        private UserInfo(int userID) {
            UserID = userID;
        }
        public int getUserID() {
            return UserID;
        }
    }
    private class ServerConnect implements AutoCloseable{
        //debuging
        private final String targetIP;
        private String BaseURL;
        private Context cont;


        public ServerConnect(String targetIP){
            this.targetIP=targetIP;
        }
        private HttpsURLConnection RightConnect(){
            try{
                URL url = new URL(BaseURL);
                return (HttpsURLConnection) url.openConnection();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        //Only Debuging use
        private HttpsURLConnection DangerConnect(){
            try {
                CertificateFactory cf= CertificateFactory.getInstance("X.509");
                InputStream cainput;
                cainput=cont.getResources().openRawResource(R.raw.onlydebugingcrt);
                Certificate ca=cf.generateCertificate(cainput);
                cainput.close();

                String KeyStoreType= KeyStore.getDefaultType();
                KeyStore keystore=KeyStore.getInstance(KeyStoreType);

                keystore.load(null,null);
                keystore.setCertificateEntry("ca",ca);


                String tmalgorithm= TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf=TrustManagerFactory.getInstance(tmalgorithm);
                tmf.init(keystore);


                SSLContext context=SSLContext.getInstance("TLS");
                context.init(null,tmf.getTrustManagers(),null);


                URL url = new URL(BaseURL);
                HttpsURLConnection urlConnection=(HttpsURLConnection)url.openConnection();
                //안전하지 않아도 연결할지 확인
                urlConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        if(hostname.equals(targetIP)){
                            return true;
                        }
                        return false;

                    }
                });
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                return urlConnection;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        private String ConnectWork(HttpsURLConnection urlConnection, JSONObject parameter){
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("Accept","Application/json");
            if(parameter!=null){
                RequestSend(urlConnection,parameter);
            }
            String Result="";

            try{
                urlConnection.setRequestMethod("POST");
                if(urlConnection.getResponseCode()==200){
                    InputStream in=urlConnection.getInputStream();
                    Result=StreamRead(in);

                    in.close();
                }else{
                    Log.e("DSL",urlConnection.getResponseMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            return Result;
        }
        private String setParameter(JSONObject parameter) throws JSONException {
            StringBuffer sb=new StringBuffer();
            Iterator iterator= parameter.keys();
            String id;
            while(iterator.hasNext()){
                id=iterator.next().toString();
                sb.append(id);
                sb.append("=");
                sb.append(parameter.getString(id));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }
        private void RequestSend(HttpsURLConnection con,JSONObject parameter){
            try{
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
//                나중에 설정하면 좋을 듯
//                con.setRequestProperty("Content-Type", "Application/json");
                String param="";
                OutputStream out=new BufferedOutputStream(con.getOutputStream());
                param=setParameter(parameter);
                out.write(param.getBytes(StandardCharsets.UTF_8));
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private String StreamRead(InputStream in){
            StringBuilder s= new StringBuilder();
            int len;
            byte[] buffer=new byte[1024];
            while(true){
                try{
                    len = in.read(buffer,0,buffer.length);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                if(len==-1){
                    break;
                }
                s.append(new String(buffer));
            }
            return s.toString();
        }
        //Only test. must delete
        private HttpsURLConnection Connect(){
            return DangerConnect();
        }
        public void sendRequest(Context context, JSONObject json,String API_URL, NetListener netListener){
            if(cont==null){
                cont=context.getApplicationContext();
            }
            BaseURL ="https://"+targetIP+API_URL;
            DSLUtil.print("target : "+BaseURL);
            Runnable task = ()->{
                JSONArray result;
                try {
                    String resultstr=ConnectWork(Connect(),json);
                    if(resultstr!=null&&!resultstr.isEmpty()){
                        result=new JSONArray(resultstr);
                        if(netListener==null){
                            return;
                        }
                        netListener.Result(result);
                    }else{
                        if(netListener==null){
                            return;
                        }
                        netListener.Result(null);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        public void sendRequestforWeather(Context context,String API_URL, NetListener netListener){
            if(cont==null){
                cont=context.getApplicationContext();
            }
            BaseURL =API_URL;
            Runnable task = ()->{
                JSONArray result;
                try {
                    String resultstr=ConnectWorkforWeather(RightConnect());
                    if(resultstr!=null&&!resultstr.isEmpty()){
                        JSONObject temp=new JSONObject(resultstr);
                        result=new JSONArray();
                        result.put(temp);
                        if(netListener==null){
                            return;
                        }
                        netListener.Result(result);
                    }else{
                        if(netListener==null){
                            return;
                        }
                        netListener.Result(null);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        private String ConnectWorkforWeather(HttpsURLConnection urlConnection){
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            String Result="";
            try{
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept","Application/json");
                if(urlConnection.getResponseCode()==200){
                    InputStream in=urlConnection.getInputStream();
                    Result=StreamRead(in);

                    in.close();
                }else{
                    Log.i("DSL",urlConnection.getResponseMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            return Result;
        }
        @Override
        public void close() {
            exs.shutdown();
            cont=null;
            try {
                if (!exs.awaitTermination(60, TimeUnit.SECONDS)) {
                    exs.shutdownNow();
                    if (!exs.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                exs.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    public void sendRequest(Context context, JSONObject json,String API_URL, NetListener netListener){
        Server.sendRequest(context,json,API_URL,netListener);
    }
    public void sendRequestforWeather(Context context,String API_URL, NetListener netListener){
        Server.sendRequestforWeather(context,API_URL,netListener);
    }
    public static void gomenu(Context context){
        Intent i=new Intent(context, MenuActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
    public void Login(int UserID){
        if(userInfo==null){
            userInfo=new UserInfo(UserID);
        }
    }
    public int getUserCode(){
        return userInfo.getUserID();
    }
    public LocalDataBase localDB;
    private class LocalDataBase{
        private DBopen DBopener;
        private SQLiteDatabase DB;
        public void CreateDataBase(Context context){
            DBopener=new DBopen(context);
        }
        public long Insert(String table_name,ContentValues cv){
            DB=DBopener.getWritableDatabase();
            long result=DB.insert(table_name,null,cv);
            DB.close();
            return result;
        }
        public Cursor Read(String table_name,String[] target,String[] selection,String[] selectionArgs){
            DB=DBopener.getReadableDatabase();
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<selection.length;i++){
                sb.append(selection[i]);
                sb.append("=?");
                if(i<selection.length-1){
                    sb.append(" and ");
                }
            }
            Cursor result=DB.query(table_name,target,sb.toString(),selectionArgs,null,null,null);
            DB.close();
            return result;
        }
        public void close(){
            DBopener.close();
        }
        public class DBEntry implements BaseColumns {
            private DBEntry(){ }
            public static final String TABLE = "time_schedule";
            public static final String ID = "id";
            public static final String USER_CODE = "user_code";
            public static final String SUBJECT = "subject";
            public static final String PROFESSOR = "professor";
            public static final String DAY = "day";
            public static final String START_TIME = "start_time";
            public static final String END_TIME = "end_time";
            public static final String ROOM = "room";
            public static final String ALARM = "alarm";
        }
        private class DBopen extends SQLiteOpenHelper{
            public static final int DATABASE_VERSION = 1;
            private  final static String DATABASE_NAME ="test.db";
            public DBopen(@Nullable Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                StringBuilder sb=new StringBuilder();
                sb.append("create table ").append(DBEntry.TABLE).append(" (").append(DBEntry.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,").append(DBEntry.USER_CODE).append(" INTEGER,")
                        .append(DBEntry.SUBJECT).append(" varchar(20), ").append(DBEntry.PROFESSOR).append(" varchar(20),").append(DBEntry.DAY).append(" integer,")
                        .append(DBEntry.START_TIME).append(" integer, ").append(DBEntry.END_TIME).append(" integer,").append(DBEntry.ROOM).append(" varchar(20),")
                        .append(DBEntry.ALARM).append(" integer);");
                db.execSQL(sb.toString());
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("drop table if exists "+DBEntry.TABLE);
                onCreate(db);
            }
        }
    }
    public void Close() {
        Server.close();
        localDB.close();
        Server=null;

    }

}
