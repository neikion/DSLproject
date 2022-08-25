package com.example.dsl;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

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
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    }
    private ExecutorService exs;
    private ServerConnect Server;
    enum DBIDS{
        TimeSchedule,
        Noti,
        SchoolSchedule
    }
    public interface NetListener {
        void Result(JSONArray Result);
    }
    private class ServerConnect implements AutoCloseable{
        private boolean start=false;
        //debuging
        private final String targetIP;
        private final String BaseURL;
        private Context cont;


        public ServerConnect(String targetIP){
            this.targetIP=targetIP;
            BaseURL ="https://"+targetIP+"/DB.jsp";

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
            String s="";
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
        private String ConnectWork(HttpsURLConnection urlConnection, JSONObject parameter, String type){
            if(parameter!=null){
                RequestSend(urlConnection,parameter, type);
            }
            String Result="";

            try{
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
        private void RequestSend(HttpsURLConnection con,JSONObject parameter,String type){
            StringBuffer sb=new StringBuffer();
            sb.append("input_json");
            sb.append("=");
            sb.append(parameter.toString());
            try{
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept","Application/json");
                con.setRequestProperty("type",type);
                OutputStream out=new BufferedOutputStream(con.getOutputStream());
                String param=sb.toString();
                out.write(param.getBytes(StandardCharsets.UTF_8));
                out.close();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        private String StreamRead(InputStream in){
            String s="";
            int len = 0;
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
                s+=new String(buffer);
            }
            return s;
        }
        //Only test. must delete
        private HttpsURLConnection Connect(){
            return DangerConnect();
        }
        private String getResult(String result) throws JSONException, SQLException {
            String responseString = new JSONArray(result).getJSONObject(0).getString("result");
            if(responseString.equals("200")){
                return "Success";
            }
            throw new SQLException(responseString);
        }
        private JSONArray getResult(JSONArray result) throws SQLException, JSONException {
            String responseString = result.getJSONObject(0).getString("result");
            if(responseString.equals("200")){
                return result;
            }
            throw new SQLException(responseString);
        }
        public String Create(Context context, JSONObject json) throws ExecutionException, InterruptedException, JSONException, SQLException {
            cont=context;
            Callable<String> task=()->{
                return ConnectWork(Connect(),json,"Create");
            };
            Future<String> result=exs.submit(task);

            return getResult(result.get());
        }

        public JSONArray Read(Context con,JSONObject json) throws JSONException, SQLException, ExecutionException, InterruptedException {
            cont=con;
            Callable<String> task=()->{
                return ConnectWork(Connect(),json,"Read");
            };
            Future<String> result=exs.submit(task);
            JSONArray resultjson=new JSONArray(result.get());
            if(resultjson.isNull(0)){
                return resultjson;
            }
            if(resultjson.getJSONObject(0).has("result")){
                throw new SQLException(resultjson.getJSONObject(0).getString("result"));
            }
            return resultjson;
        }
        public String Update(Context context, JSONObject json) throws ExecutionException, InterruptedException, JSONException, SQLException {
            cont=context;
            Callable<String> task=()->{
                return ConnectWork(Connect(),json,"Update");

            };
            Future<String> result=exs.submit(task);
            return getResult(result.get());
        }
        public String Delete(Context context, JSONObject json) throws ExecutionException, InterruptedException, JSONException, SQLException{
            cont=context;
            Callable<String> task=()->{
                return ConnectWork(Connect(),json,"Delete");

            };
            Future<String> result=exs.submit(task);
            return getResult(result.get());
        }

        public void Create(Context context, JSONObject json,NetListener netListener){
            cont=context;
            Runnable task=()->{
                try {
                    JSONArray result=new JSONArray(ConnectWork(Connect(),json,"Create"));
                    netListener.Result(getResult(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        public void Read(Context con, JSONObject json, NetListener netListener){
            cont=con;
            Runnable task = ()->{
                JSONArray result;
                try {
                    result=new JSONArray(ConnectWork(Connect(),json,"Read"));
                    if(result.getJSONObject(0).has("result")){
                        throw new SQLException(result.getJSONObject(0).getString("result"));
                    }
                    netListener.Result(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
            exs.submit(task);
        }
        public void Update(Context context, JSONObject json,NetListener netListener) {
            cont=context;
            Runnable task=()->{
                try {
                    JSONArray result=new JSONArray(ConnectWork(Connect(),json,"Update"));
                    netListener.Result(getResult(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        public void Delete(Context context, JSONObject json,NetListener netListener) {
            cont=context;
            Runnable task=()->{
                try {
                    JSONArray result=new JSONArray(ConnectWork(Connect(),json,"Delete"));
                    netListener.Result(getResult(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        @Override
        public void close() throws Exception {
            exs.shutdown();
            cont=null;
            try {
                // Wait a while for existing tasks to terminate
                if (!exs.awaitTermination(60, TimeUnit.SECONDS)) {
                    exs.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!exs.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                exs.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
    public class ServerReqeust{
        private JSONObject json;
        public ServerReqeust(String table){
            json=new JSONObject();
            try {
                json.put("table",table);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public ServerReqeust Column(String column){
            try {
                json.put("column",column);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public ServerReqeust UpdateValue(String value){
            try {
                json.put("update_value",value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public ServerReqeust Insert(String[] values){
            try {
                json.put("insert_value",strary2str(values));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        private String strary2str(String[] str){
            String result="";
            for(int i=0;i<str.length-1;i++){
                result+=str[i]+",";
            }
            result+=str[str.length-1];
            return result;
        }
        public ServerReqeust Constraint(String constraint,String constraint_value){
            try {
                json.put("constraint",constraint);
                json.put("constraint_value",constraint_value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
        public String Create(Context context) throws JSONException, ExecutionException, InterruptedException, SQLException {
            return Server.Create(context,json);
        }
        public JSONArray Read(Context context) throws Exception {
            return Server.Read(context,json);
        }
        public String Update(Context context) throws ExecutionException, InterruptedException, JSONException, SQLException {
            return Server.Update(context,json);
        }
        public String Delete(Context context) throws ExecutionException, InterruptedException, JSONException, SQLException {
            return Server.Delete(context,json);
        }
        public void Create(Context context,NetListener netListener){
            Server.Create(context,json,netListener);
        }
        public void Read(Context context,NetListener netListener){
            Server.Read(context,json,netListener);
        }
        public void Update(Context context,NetListener netListener){
            Server.Update(context,json,netListener);
        }
        public void Delete(Context context,NetListener netListener){
            Server.Delete(context,json,netListener);
        }
    }
    public ServerReqeust Reqeust(String table){
        return new ServerReqeust(table);
    }
    public void Close() throws Exception {
        Server.close();
        Server=null;
    }



}
